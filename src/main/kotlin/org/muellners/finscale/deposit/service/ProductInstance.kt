package org.muellners.finscale.deposit.service

import java.util.*
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.spring.stereotype.Aggregate
import org.muellners.finscale.deposit.command.*
import org.muellners.finscale.deposit.event.*

@Aggregate
class ProductInstance() {
    @AggregateIdentifier
    var id: UUID? = null
    var productIdentifier: String? = null
    var customerIdentifier: String? = null
    var accountIdentifier: String? = null
    var beneficiaries: String? = null
    var openedOn: Date? = null
    var lastTransactionDate: Date? = null
    var state: Boolean? = null

    @CommandHandler
    constructor(command: CreateProductInstanceCommand) : this() {

        if (command.id == null) {
            throw IllegalArgumentException("Product instance ID cannot be null!")
        }

        if (command.customerIdentifier == null) {
            throw IllegalArgumentException("Customer identifier cannot be null!")
        }

        if (command.productIdentifier == null) {
            throw IllegalArgumentException("Product definition cannot be null!")
        }

        if (command.accountIdentifier == null) {
            throw IllegalArgumentException("Account identifier type cannot be null!")
        }

        AggregateLifecycle.apply(
            CreatedProductInstanceEvent(
                id = command.id,
                productIdentifier = command.productIdentifier,
                customerIdentifier = command.customerIdentifier,
                accountIdentifier = command.accountIdentifier,
                beneficiaries = command.beneficiaries,
                openedOn = command.openedOn,
                lastTransactionDate = command.lastTransactionDate,
                state = command.state
            )
        )
    }

    @CommandHandler
    fun handle(command: UpdateProductInstanceCommand) {
        AggregateLifecycle.apply(
            UpdatedProductInstanceEvent(
                id = command.id,
                productIdentifier = command.productIdentifier,
                customerIdentifier = command.customerIdentifier,
                accountIdentifier = command.accountIdentifier,
                beneficiaries = command.beneficiaries,
                openedOn = command.openedOn,
                lastTransactionDate = command.lastTransactionDate,
                state = command.state
            )
        )
    }

    @CommandHandler
    fun handle(command: DeleteProductInstanceCommand) {
        AggregateLifecycle.apply(
            DeletedProductInstanceEvent(
                id = command.id
            )
        )
    }

    @CommandHandler
    fun handle(command: ActivateProductInstanceCommand) {
        AggregateLifecycle.apply(
            PerformedActionProductInstanceEvent(
                id = command.id,
                state = command.state
            )
        )
    }

    @CommandHandler
    fun handle(command: DeactivateProductInstanceCommand) {
        AggregateLifecycle.apply(
            PerformedActionProductInstanceEvent(
                id = command.id,
                state = command.state
            )
        )
    }

    @CommandHandler
    fun handle(command: TransactionProcessedCommand) {
        AggregateLifecycle.apply(
            TransactionProcessedEvent(id = command.id)
        )
    }

    @EventSourcingHandler
    fun on(event: CreatedProductInstanceEvent) {
        this.id = event.id
        this.productIdentifier = event.productIdentifier
        this.customerIdentifier = event.customerIdentifier
        this.accountIdentifier = event.accountIdentifier
        this.beneficiaries = event.beneficiaries
        this.openedOn = event.openedOn
        this.lastTransactionDate = event.lastTransactionDate
        this.state = event.state
    }

    @EventSourcingHandler
    fun on(event: UpdatedProductInstanceEvent) {
        this.id = event.id
        this.productIdentifier = event.productIdentifier
        this.customerIdentifier = event.customerIdentifier
        this.accountIdentifier = event.accountIdentifier
        this.beneficiaries = event.beneficiaries
        this.openedOn = event.openedOn
        this.lastTransactionDate = event.lastTransactionDate
        this.state = event.state
    }

    @EventSourcingHandler
    fun on(event: DeletedProductInstanceEvent) {
        this.id = event.id
    }

    @EventSourcingHandler
    fun on(event: PerformedActionProductInstanceEvent) {
        this.id = event.id
        this.state = event.state
    }

    @EventSourcingHandler
    fun on(event: TransactionProcessedEvent) {
        this.id = event.id
        this.lastTransactionDate = Calendar.getInstance().time
    }
}
