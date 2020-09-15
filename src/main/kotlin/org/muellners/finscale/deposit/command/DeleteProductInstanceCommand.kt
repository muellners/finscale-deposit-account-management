package org.muellners.finscale.deposit.command

import java.util.*
import org.axonframework.modelling.command.TargetAggregateIdentifier

data class DeleteProductInstanceCommand(
    @TargetAggregateIdentifier
    val id: UUID?
)
