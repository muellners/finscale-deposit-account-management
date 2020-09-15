package org.muellners.finscale.deposit.command

import java.math.BigDecimal
import java.time.LocalDate
import java.util.*
import kotlin.collections.HashMap
import org.axonframework.commandhandling.RoutingKey

data class PostJournalEntryCommand(
    @RoutingKey
    var id: UUID? = null,

    var transactionTypeId: UUID? = null,

    var transactionDate: LocalDate? = null,

    var debtors: HashMap<UUID, BigDecimal> = hashMapOf(),

    var creditors: HashMap<UUID, BigDecimal> = hashMapOf(),

    var note: String? = null,

    var message: String? = null

)
