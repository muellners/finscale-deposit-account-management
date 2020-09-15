package org.muellners.finscale.deposit.service

import java.time.LocalDate
import java.util.*
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.spring.stereotype.Aggregate
import org.muellners.finscale.deposit.command.AccrualCommand
import org.muellners.finscale.deposit.event.AccruedEvent

@Aggregate
class Accrual() {
    @AggregateIdentifier
    var id: UUID? = null
    var dueDate: LocalDate? = null

    @CommandHandler
    constructor(command: AccrualCommand) : this() {
        AggregateLifecycle.apply(
            AccruedEvent(
                id = command.id,
                dueDate = command.dueDate
            )
        )
    }

    @EventSourcingHandler
    fun on(event: AccruedEvent) {
        this.id = event.id
        this.dueDate = event.dueDate
    }
}
