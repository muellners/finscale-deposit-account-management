package org.muellners.finscale.deposit.service

import java.time.LocalDate
import java.util.*
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.spring.stereotype.Aggregate
import org.muellners.finscale.deposit.command.PayInterestCommand
import org.muellners.finscale.deposit.event.PaidInterestEvent

@Aggregate
class PayInterest() {
    @AggregateIdentifier
    var id: UUID? = null
    var date: LocalDate? = null

    @CommandHandler
    constructor(command: PayInterestCommand) : this() {
        AggregateLifecycle.apply(
            PaidInterestEvent(
                id = command.id,
                date = command.date
            )
        )
    }

    @EventSourcingHandler
    fun on(event: PaidInterestEvent) {
        this.id = event.id
        this.date = event.date
    }
}
