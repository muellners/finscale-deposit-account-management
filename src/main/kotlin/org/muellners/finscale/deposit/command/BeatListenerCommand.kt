package org.muellners.finscale.deposit.command

import java.time.LocalDate
import java.util.*
import org.axonframework.modelling.command.TargetAggregateIdentifier

data class BeatListenerCommand(
    @TargetAggregateIdentifier
    val id: UUID?,
    val forTime: LocalDate?
)
