package org.muellners.finscale.deposit.command

import java.util.*
import org.axonframework.modelling.command.TargetAggregateIdentifier

data class CreateActionCommand(
    @TargetAggregateIdentifier
    val id: UUID?,
    val identifier: String?,
    val name: String?,
    val description: String?,
    val transactionType: String?
)
