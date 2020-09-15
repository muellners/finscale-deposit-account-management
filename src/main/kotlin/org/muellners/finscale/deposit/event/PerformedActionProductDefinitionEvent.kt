package org.muellners.finscale.deposit.event

import java.util.*

data class PerformedActionProductDefinitionEvent(
    val id: UUID?,
    val state: Boolean?
)
