package org.muellners.finscale.deposit.event

import java.time.LocalDate
import java.util.*

data class AccruedEvent(
    val id: UUID?,
    val dueDate: LocalDate?
)
