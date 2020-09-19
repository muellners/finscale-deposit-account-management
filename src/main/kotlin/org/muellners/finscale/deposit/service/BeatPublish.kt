package org.muellners.finscale.deposit.service

import java.time.LocalDate
import java.util.*

data class BeatPublish(
    val id: UUID?,
    val forTime: LocalDate?
)
