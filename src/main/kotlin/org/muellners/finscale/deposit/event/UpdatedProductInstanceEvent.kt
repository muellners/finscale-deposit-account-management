package org.muellners.finscale.deposit.event

import java.util.*

data class UpdatedProductInstanceEvent(
    val id: UUID?,
    val productDefinitionId: String?,
    val customerIdentifier: String?,
    val accountIdentifier: String?,
    val beneficiaries: String?,
    val openedOn: Date?,
    val lastTransactionDate: Date?,
    val state: Boolean?
)
