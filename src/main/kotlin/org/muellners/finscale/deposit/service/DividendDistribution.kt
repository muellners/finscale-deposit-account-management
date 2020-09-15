package org.muellners.finscale.deposit.service

import java.time.LocalDate
import java.util.*
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.spring.stereotype.Aggregate
import org.muellners.finscale.deposit.command.DividendDistributionCommand
import org.muellners.finscale.deposit.event.DividendDistributedEvent

@Aggregate
class DividendDistribution() {
    @AggregateIdentifier
    var id: UUID? = null
    var productDefinitionId: String? = null
    var dueDate: LocalDate? = null
    var rate: Double? = null

    @CommandHandler
    constructor(command: DividendDistributionCommand) : this() {
        AggregateLifecycle.apply(
            DividendDistributedEvent(
                id = command.id,
                productDefinitionId = command.productDefinitionId,
                dueDate = command.dueDate,
                rate = command.rate
            )
        )
    }

    @EventSourcingHandler
    fun on(event: DividendDistributedEvent) {
        this.id = event.id
        this.productDefinitionId = event.productDefinitionId
        this.dueDate = event.dueDate
        this.rate = event.rate
    }
}
