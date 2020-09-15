package org.muellners.finscale.deposit.service

import java.util.*
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.spring.stereotype.Aggregate
import org.muellners.finscale.deposit.command.CreateActionCommand
import org.muellners.finscale.deposit.event.CreatedActionEvent

@Aggregate
class Action() {
    @AggregateIdentifier
    var id: UUID? = null
    var identifier: String? = null
    var name: String? = null
    var description: String? = null
    var transactionType: String? = null

    @CommandHandler
    constructor(command: CreateActionCommand) : this() {
        AggregateLifecycle.apply(
            CreatedActionEvent(
                id = command.id,
                identifier = command.identifier,
                name = command.name,
                description = command.description,
                transactionType = command.transactionType
            )
        )
    }

    @EventSourcingHandler
    fun on(event: CreatedActionEvent) {
        this.id = event.id
        this.identifier = event.identifier
        this.name = event.name
        this.description = event.description
        this.transactionType = event.transactionType
    }
}
