package org.muellners.finscale.deposit.service

import java.time.LocalDate
import java.util.*
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.spring.stereotype.Aggregate
import org.muellners.finscale.deposit.command.BeatListenerCommand
import org.muellners.finscale.deposit.event.BeatListenedEvent

@Aggregate
class BeatListener() {
    @AggregateIdentifier
    var id: UUID? = null
    var forTime: LocalDate? = null

    @CommandHandler
    constructor(command: BeatListenerCommand) : this() {
        AggregateLifecycle.apply(
            BeatListenedEvent(
                id = command.id,
                forTime = command.forTime
            )
        )
    }

    @EventSourcingHandler
    fun on(event: BeatListenedEvent) {
        this.id = event.id
        this.forTime = event.forTime
    }
}
