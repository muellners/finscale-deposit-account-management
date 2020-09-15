package org.muellners.finscale.deposit.event

import java.time.LocalDate
import java.util.*

data class DividendDistributedEvent(
    val id: UUID?,
    val productDefinitionId: String?,
    val dueDate: LocalDate?,
    val rate: Double?
)
