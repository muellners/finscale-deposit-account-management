package org.muellners.finscale.deposit.service

import java.math.BigDecimal
import java.math.MathContext
import java.time.Clock
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import java.util.function.Consumer
import java.util.stream.Collectors
import java.util.stream.IntStream
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.eventhandling.EventHandler
import org.axonframework.messaging.responsetypes.ResponseTypes
import org.axonframework.queryhandling.QueryGateway
import org.muellners.finscale.deposit.command.PostJournalEntryCommand
import org.muellners.finscale.deposit.domain.enumeration.InterestPayable
import org.muellners.finscale.deposit.domain.enumeration.Type
import org.muellners.finscale.deposit.event.AccruedEvent
import org.muellners.finscale.deposit.event.DividendDistributedEvent
import org.muellners.finscale.deposit.event.PaidInterestEvent
import org.muellners.finscale.deposit.query.FindAllLedgerAccountEntriesQuery
import org.muellners.finscale.deposit.query.FindLedgerAccountQuery
import org.muellners.finscale.deposit.repository.*
import org.muellners.finscale.deposit.view.AccruedInterestView
import org.muellners.finscale.deposit.view.DividendDistributionView
import org.muellners.finscale.deposit.view.ProductDefinitionView
import org.muellners.finscale.deposit.view.ProductInstanceView
import org.muellners.finscale.deposit.view.TermView
import org.springframework.stereotype.Component
import org.threeten.extra.YearQuarter

@Component
class InterestCalculationProjection(
    private val productDefinitionViewRepository: ProductDefinitionViewRepository,
    private val productInstanceViewRepository: ProductInstanceViewRepository,
    private val termViewRepository: TermViewRepository,
    private val accruedInterestViewRepository: AccruedInterestViewRepository,
    private val dividendDistributionViewRepository: DividendDistributionViewRepository,
    private val queryGateway: QueryGateway,
    private val commandGateway: CommandGateway
) {
    @EventHandler
    fun on(event: AccruedEvent): LocalDate? {
        val accrualDate = event.dueDate
        val productDefinitions: List<ProductDefinitionView> = productDefinitionViewRepository.findAll()
        productDefinitions.forEach(Consumer<ProductDefinitionView> { productDefinitionView: ProductDefinitionView ->
            if (accruableProduct(productDefinitionView)) {
                val accruedValues = ArrayList<Double>()
                val termView: TermView = termViewRepository.findByProductDefinitionView(productDefinitionView)
                val productInstances: List<ProductInstanceView> = productInstanceViewRepository.findByProductDefinitionView(productDefinitionView)
                productInstances.forEach(Consumer<ProductInstanceView> { productInstanceView: ProductInstanceView ->
                    if (productInstanceView.state!!.equals(ACTIVE)) {
                        val account = queryGateway.query<Account, FindLedgerAccountQuery>(
                            FindLedgerAccountQuery(UUID.fromString(productInstanceView.accountIdentifier)),
                            ResponseTypes.instanceOf(Account::class.java)
                        ).join()
                        if (account.balance!! > BigDecimal.valueOf(0.00)) {
                            val balance: BigDecimal = account.balance!!
                            val rate: BigDecimal = BigDecimal.valueOf(productDefinitionView.interest!!)
                                .divide(BigDecimal.valueOf(100), INTEREST_PRECISION, BigDecimal.ROUND_HALF_EVEN)
                            val accruedInterest = accruedInterest(balance, rate,
                                periodOfInterestPayable(termView.interestPayable.toString()), accrualDate!!.lengthOfYear())
                            if (accruedInterest > BigDecimal.ZERO) {
                                val doubleValue = accruedInterest.setScale(5, BigDecimal.ROUND_HALF_EVEN).toDouble()
                                accruedValues.add(doubleValue)
                                val optionalAccruedInterest: Optional<AccruedInterestView> = accruedInterestViewRepository.findByCustomerAccountIdentifier(account.identifier!!)
                                if (optionalAccruedInterest.isPresent) {
                                    val accruedInterestView: AccruedInterestView = optionalAccruedInterest.get()
                                    accruedInterestView.amount = accruedInterestView.amount?.plus(doubleValue)
                                    accruedInterestViewRepository.save(accruedInterestView)
                                } else {
                                    val accruedInterestView = AccruedInterestView()
                                    accruedInterestView.accrueAccountIdentifier = productDefinitionView.accrueAccountIdentifier
                                    accruedInterestView.customerAccountIdentifier = account.identifier
                                    accruedInterestView.amount = doubleValue
                                    accruedInterestViewRepository.save(accruedInterestView)
                                }
                            }
                        }
                    }
                })
                val roundedAmount = BigDecimal.valueOf(accruedValues.parallelStream().reduce(0.00) { a: Double, b: Double -> java.lang.Double.sum(a, b) })
                    .setScale(2, BigDecimal.ROUND_HALF_EVEN)
                val cashToAccrueJournalEntryCommand = PostJournalEntryCommand()
                cashToAccrueJournalEntryCommand.transactionDate = LocalDateTime.now(Clock.systemUTC()).toLocalDate()
                cashToAccrueJournalEntryCommand.transactionTypeId = UUID.fromString("INTR")
                cashToAccrueJournalEntryCommand.note = "Daily accrual for product " + productDefinitionView.identifier.toString() + "."

                val cashDebtorAccountId = UUID.fromString(productDefinitionView.cashAccountIdentifier)
                cashToAccrueJournalEntryCommand.debtors[cashDebtorAccountId] = roundedAmount

//                val accrueCreditor = TransactionDTO()
                val accrueCreditorAccountId = UUID.fromString(productDefinitionView.accrueAccountIdentifier)
                cashToAccrueJournalEntryCommand.creditors[accrueCreditorAccountId] = roundedAmount

                commandGateway.send<Any>(cashToAccrueJournalEntryCommand)
            }
        })
        return accrualDate
    }

    @EventHandler
    fun on(event: PaidInterestEvent): String {
        val productDefinitionEntities: List<ProductDefinitionView> = productDefinitionViewRepository.findAll()
        productDefinitionEntities.forEach(Consumer<ProductDefinitionView> { productDefinitionView: ProductDefinitionView ->
            if (productDefinitionView.active!! &&
                productDefinitionView.type!! != Type.SHARE) {
                val termView: TermView = termViewRepository.findByProductDefinitionView(productDefinitionView)
                if (shouldPayInterest(termView.interestPayable.toString(), event.date!!)) {
                    val productInstances: List<ProductInstanceView> = productInstanceViewRepository.findByProductDefinitionView(productDefinitionView)
                    productInstances.forEach(Consumer<ProductInstanceView> { productInstanceView: ProductInstanceView ->
                        val optionalAccruedInterestView: Optional<AccruedInterestView> = accruedInterestViewRepository.findByCustomerAccountIdentifier(productInstanceView.accountIdentifier!!)
                        if (optionalAccruedInterestView.isPresent) {
                            val accruedInterestView: AccruedInterestView = optionalAccruedInterestView.get()
                            val roundedAmount = BigDecimal.valueOf(accruedInterestView.amount!!)
                                .setScale(2, BigDecimal.ROUND_HALF_EVEN)
                            val accrueToExpenseJournalEntry = PostJournalEntryCommand()
                            accrueToExpenseJournalEntry.transactionDate = LocalDateTime.now(Clock.systemUTC()).toLocalDate()
                            accrueToExpenseJournalEntry.transactionTypeId = UUID.fromString("INTR")
                            accrueToExpenseJournalEntry.note = "Interest paid."

//                            val accrueDebtor = TransactionDTO()
                            val accrueDebtorAccountId = UUID.fromString(accruedInterestView.accrueAccountIdentifier)
                            accrueToExpenseJournalEntry.debtors[accrueDebtorAccountId] = roundedAmount

//                            val expenseCreditor = TransactionDTO()
                            val expenseCreditorAccountId = UUID.fromString(productDefinitionView.expenseAccountIdentifier)
                            accrueToExpenseJournalEntry.creditors[expenseCreditorAccountId] = roundedAmount

                            accruedInterestViewRepository.delete(accruedInterestView)
                            commandGateway.send<Any>(accrueToExpenseJournalEntry)
                            payoutInterest(
                                productDefinitionView.expenseAccountIdentifier!!,
                                accruedInterestView.customerAccountIdentifier!!,
                                roundedAmount
                            )
                        }
                    })
                }
            }
        })
        return "interest-payed"
    }

    @EventHandler
    fun on(event: DividendDistributedEvent) {
        val optionalProductDefinition: Optional<ProductDefinitionView> = productDefinitionViewRepository.findByIdentifier(event.productIdentifier!!)
        if (optionalProductDefinition.isPresent) {
            val productDefinitionView: ProductDefinitionView = optionalProductDefinition.get()
            if (productDefinitionView.active!!) {
                val rate: BigDecimal = BigDecimal.valueOf(event.rate!!)
                val termView: TermView = termViewRepository.findByProductDefinitionView(productDefinitionView)
                val dateRanges = dateRanges(event.dueDate!!, termView.interestPayable!!.toString())
                val productInstanceEntities: List<ProductInstanceView> = productInstanceViewRepository.findByProductDefinitionView(productDefinitionView)
                productInstanceEntities.forEach { productInstanceView: ProductInstanceView ->
                    if (productInstanceView.state!!.equals(ACTIVE)) {
                        val account = queryGateway.query<Account, FindLedgerAccountQuery>(
                            FindLedgerAccountQuery(UUID.fromString(productInstanceView.accountIdentifier)),
                            ResponseTypes.instanceOf(Account::class.java)
                        ).join()
                        val startDate: LocalDate = event.dueDate!!.plusDays(1)
                        val now: LocalDate = LocalDate.now(Clock.systemUTC())
                        val findCurrentEntries: String = "$startDate..$now"
                        val currentAccountEntries: List<AccountEntry> = queryGateway.query<List<AccountEntry>, FindAllLedgerAccountEntriesQuery>(
                            FindAllLedgerAccountEntriesQuery(UUID.fromString(account.identifier)),
                            ResponseTypes.multipleInstancesOf(AccountEntry::class.java)
                        ).join()
                        val balanceHolder: BalanceHolder
                        balanceHolder = if (currentAccountEntries.isEmpty()) {
                            BalanceHolder(account.balance!!)
                        } else {
                            val accountEntry: AccountEntry = currentAccountEntries[0]
                            BalanceHolder(accountEntry.balance!!.subtract(accountEntry.amount))
                        }
                        val dividendHolder = DividendHolder()
                        dateRanges.forEach(Consumer {
                            val accountEntries: List<AccountEntry> = queryGateway.query<List<AccountEntry>, FindAllLedgerAccountEntriesQuery>(
                                FindAllLedgerAccountEntriesQuery(UUID.fromString(account.identifier)),
                                ResponseTypes.multipleInstancesOf(AccountEntry::class.java)
                            ).join()
                            if (accountEntries.isNotEmpty()) {
                                balanceHolder.balance = accountEntries[0].balance!!
                            }
                            val currentBalance: BigDecimal = balanceHolder.balance
                            dividendHolder.addAmount(
                                accruedInterest(currentBalance, rate, 12, event.dueDate.lengthOfYear())
                            )
                        })
                        if (dividendHolder.amount > BigDecimal.ZERO) {
                            val roundedAmount = dividendHolder.amount
                                .setScale(2, BigDecimal.ROUND_HALF_EVEN)
                            val cashToExpenseJournalEntryCommand = PostJournalEntryCommand()
                            cashToExpenseJournalEntryCommand.transactionDate = now
                            cashToExpenseJournalEntryCommand.transactionTypeId = UUID.fromString("INTR")
                            cashToExpenseJournalEntryCommand.note = "Dividend distribution."

                            val cashDebtorAccountId = UUID.fromString(productDefinitionView.cashAccountIdentifier)
                            cashToExpenseJournalEntryCommand.debtors[cashDebtorAccountId] = roundedAmount

                            val expenseCreditorAccountId = UUID.fromString(productDefinitionView.expenseAccountIdentifier)
                            cashToExpenseJournalEntryCommand.creditors[expenseCreditorAccountId] = roundedAmount
                            commandGateway.send<Any>(cashToExpenseJournalEntryCommand)
                            payoutInterest(
                                productDefinitionView.expenseAccountIdentifier!!,
                                account.identifier!!,
                                roundedAmount
                            )
                        }
                    }
                }
            }
            val dividendDistributionView = DividendDistributionView()
            dividendDistributionView.dueDate = event.dueDate
            dividendDistributionView.rate = event.rate
            dividendDistributionViewRepository.save<DividendDistributionView>(dividendDistributionView)
        }
    }

    private fun periodOfInterestPayable(interestPayable: String): Int {
        return when (InterestPayable.valueOf(interestPayable)) {
            InterestPayable.MONTHLY -> 12
            InterestPayable.QUARTERLY -> 4
            else -> 1
        }
    }

    private fun shouldPayInterest(interestPayable: String, date: LocalDate): Boolean {
        return when (InterestPayable.valueOf(interestPayable)) {
            InterestPayable.MONTHLY -> date == date.withDayOfMonth(date.lengthOfMonth())
            InterestPayable.QUARTERLY -> date == YearQuarter.from(date).atEndOfQuarter()
            InterestPayable.ANNUALLY -> date.dayOfYear == date.lengthOfYear()
            else -> false
        }
    }

    private fun dateRanges(dueDate: LocalDate, interestPayable: String): List<String> {
        val pastDays: Int = when (InterestPayable.valueOf(interestPayable)) {
            InterestPayable.MONTHLY -> dueDate.lengthOfMonth()
            InterestPayable.QUARTERLY -> YearQuarter.from(dueDate).lengthOfQuarter()
            else -> dueDate.lengthOfYear()
        }
        return IntStream
            .range(1, pastDays)
            .mapToObj { value ->
                val before: LocalDate = dueDate.minusDays(value.toLong())
                before.toString() + ".." + dueDate.minusDays((value - 1).toLong())
            }.collect(Collectors.toList())
    }

    private inner class BalanceHolder internal constructor(internal var balance: BigDecimal)
    private inner class DividendHolder internal constructor() {
        internal var amount: BigDecimal

        internal fun addAmount(toAdd: BigDecimal) {
            amount = amount.add(toAdd)
        }

        init {
            amount = BigDecimal.ZERO
        }
    }

    private fun accruableProduct(productDefinitionView: ProductDefinitionView): Boolean {
        return (productDefinitionView.active!! &&
            productDefinitionView.type!! != Type.SHARE &&
            productDefinitionView.interest != null && productDefinitionView.interest!! > 0.00)
    }

    private fun payoutInterest(expenseAccount: String, customerAccount: String, amount: BigDecimal) {
        val expenseToCustomerJournalEntryCommand = PostJournalEntryCommand()
        expenseToCustomerJournalEntryCommand.transactionDate = LocalDateTime.now(Clock.systemUTC()).toLocalDate()
        expenseToCustomerJournalEntryCommand.transactionTypeId = UUID.fromString("INTR")
        expenseToCustomerJournalEntryCommand.note = "Interest paid."

//        val expenseDebtor = TransactionDTO()
        val expenseDebtorAccountId = UUID.fromString(expenseAccount)
        expenseToCustomerJournalEntryCommand.debtors.put(expenseDebtorAccountId, amount)

//        val customerCreditor = TransactionDTO()
        val customerCreditorAccountId = UUID.fromString(customerAccount)
        expenseToCustomerJournalEntryCommand.creditors[customerCreditorAccountId] = amount

        commandGateway.send<Any>(expenseToCustomerJournalEntryCommand)
    }

    companion object {
        private const val INTEREST_PRECISION = 7
        private const val ACTIVE = "ACTIVE"

        /**
         * Copied from JavaMoney AnnualPercentageYield.calculate, and adjusted.
         * @return the resulting amount, never null.
         */
        private fun accruedInterest(
            amount: BigDecimal,
            rate: BigDecimal,
            periods: Int,
            lengthOfYear: Int
        ): BigDecimal {
            val baseFactor = rate.divide(BigDecimal.valueOf(periods.toLong()), MathContext.DECIMAL64)
                .add(BigDecimal.ONE)
            val annualInterest = amount.multiply(baseFactor.pow(periods).subtract(BigDecimal.ONE))
            return annualInterest
                .divide(BigDecimal.valueOf(lengthOfYear.toLong()),
                    amount.scale() + INTEREST_PRECISION, BigDecimal.ROUND_HALF_EVEN)
        }
    }
}
