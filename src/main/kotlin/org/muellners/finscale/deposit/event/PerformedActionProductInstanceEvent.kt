package org.muellners.finscale.deposit.event

import java.util.*

data class PerformedActionProductInstanceEvent(
    val id: UUID?,
    val state: Boolean?
)
