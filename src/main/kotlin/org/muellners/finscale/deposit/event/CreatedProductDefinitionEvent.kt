package org.muellners.finscale.deposit.event

import java.util.*
import org.muellners.finscale.deposit.domain.enumeration.Type

@Suppress("SpellCheckingInspection")
data class CreatedProductDefinitionEvent(
    val id: UUID?,
    val identifier: String?,
    val type: Type?,
    val name: String?,
    val description: String?,
    val minimumBalance: Double?,
    val equityLedgerIdentifier: String?,
    val cashAccountIdentifier: String?,
    val expenseAccountIdentifier: String?,
    val accrueAccountIdentifier: String?,
    val interest: Double?,
    val flexible: Boolean?,
    val active: Boolean?
)
