package org.muellners.finscale.deposit.command

import java.time.LocalDate
import java.util.*
import org.axonframework.modelling.command.TargetAggregateIdentifier

data class DividendDistributionCommand(
    @TargetAggregateIdentifier
    val id: UUID?,
    val productDefinitionId: String?,
    val dueDate: LocalDate?,
    val rate: Double?
)
