package org.muellners.finscale.deposit.event

import java.util.*

data class CreatedActionEvent(
    val id: UUID?,
    val identifier: String?,
    val name: String?,
    val description: String?,
    val transactionType: String?
)
