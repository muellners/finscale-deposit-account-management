package org.muellners.finscale.deposit.event

import java.time.LocalDate
import java.util.*

data class BeatListenedEvent(
    val id: UUID?,
    val forTime: LocalDate?
)
