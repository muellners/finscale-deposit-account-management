package org.muellners.finscale.deposit.command

import java.time.LocalDate
import java.util.UUID
import org.axonframework.modelling.command.TargetAggregateIdentifier

data class AccrualCommand(
    @TargetAggregateIdentifier
    val id: UUID?,
    val dueDate: LocalDate?
)
