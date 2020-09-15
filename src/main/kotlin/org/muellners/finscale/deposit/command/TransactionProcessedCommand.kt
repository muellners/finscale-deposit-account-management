package org.muellners.finscale.deposit.command

import java.util.*
import org.axonframework.modelling.command.TargetAggregateIdentifier

data class TransactionProcessedCommand(
    @TargetAggregateIdentifier
    val id: UUID?
)
