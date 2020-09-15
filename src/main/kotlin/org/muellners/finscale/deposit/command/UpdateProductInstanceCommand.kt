package org.muellners.finscale.deposit.command

import java.util.*
import org.axonframework.modelling.command.TargetAggregateIdentifier

data class UpdateProductInstanceCommand(
    @TargetAggregateIdentifier
    val id: UUID?,
    val productDefinitionId: String?,
    val customerIdentifier: String?,
    val accountIdentifier: String?,
    val beneficiaries: String?,
    val openedOn: Date?,
    val lastTransactionDate: Date?,
    val state: Boolean?
)
