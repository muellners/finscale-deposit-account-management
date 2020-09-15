package org.muellners.finscale.deposit.event

import java.time.LocalDate
import java.util.*

data class PaidInterestEvent(
    val id: UUID?,
    val date: LocalDate?
)
