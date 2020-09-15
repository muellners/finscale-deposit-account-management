package org.muellners.finscale.deposit.domain.productInstance.commands

import java.util.*
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.spring.stereotype.Aggregate
import org.muellners.finscale.deposit.command.*
import org.muellners.finscale.deposit.domain.enumeration.Type
import org.muellners.finscale.deposit.event.CreatedProductDefinitionEvent
import org.muellners.finscale.deposit.event.DeletedProductDefinitionEvent
import org.muellners.finscale.deposit.event.PerformedActionProductDefinitionEvent
import org.muellners.finscale.deposit.event.UpdatedProductDefinitionEvent
import org.springframework.lang.NonNull

@Aggregate
class ProductDefinition() {
    @AggregateIdentifier
    var id: UUID? = null
    var identifier: String? = null
    var type: Type? = null
    @NonNull
    var name: String? = null
    var description: String? = null
    var minimumBalance: Double? = null
    var equityLedgerIdentifier: String? = null
    var cashAccountIdentifier: String? = null
    var expenseAccountIdentifier: String? = null
    var accrueAccountIdentifier: String? = null
    var interest: Double? = null
    var flexible: Boolean? = null
    var active: Boolean? = null

    @CommandHandler
    constructor(command: CreateProductDefinitionCommand) : this() {

        if (command.id == null) {
            throw IllegalArgumentException("Product Definition ID cannot be null!")
        }

        AggregateLifecycle.apply(
            CreatedProductDefinitionEvent(
                id = command.id,
                identifier = command.identifier,
                type = command.type,
                name = command.name,
                description = command.description,
                minimumBalance = command.minimumBalance,
                equityLedgerIdentifier = command.equityLedgerIdentifier,
                cashAccountIdentifier = command.cashAccountIdentifier,
                expenseAccountIdentifier = command.expenseAccountIdentifier,
                accrueAccountIdentifier = command.accrueAccountIdentifier,
                interest = command.interest,
                flexible = command.flexible,
                active = command.active
            )
        )
    }

    @CommandHandler
    fun handle(command: UpdateProductDefinitionCommand) {
        val event = UpdatedProductDefinitionEvent(
            id = command.id,
            identifier = command.identifier,
            type = command.type,
            name = command.name,
            description = command.description,
            minimumBalance = command.minimumBalance,
            equityLedgerIdentifier = command.equityLedgerIdentifier,
            cashAccountIdentifier = command.cashAccountIdentifier,
            expenseAccountIdentifier = command.expenseAccountIdentifier,
            accrueAccountIdentifier = command.accrueAccountIdentifier,
            interest = command.interest,
            flexible = command.flexible,
            active = command.active
        )
        AggregateLifecycle.apply(event)
    }

    @CommandHandler
    fun handle(command: DeleteProductDefinitionCommand) {
        val event = DeletedProductDefinitionEvent(id)
        AggregateLifecycle.apply(event)
    }

    @CommandHandler
    fun handle(command: ActivateProductDefinitionCommand) {
        val event = PerformedActionProductDefinitionEvent(
            id = command.id,
            state = command.state
        )
        AggregateLifecycle.apply(event)
    }

    @CommandHandler
    fun handle(command: DeactivateProductDefinitionCommand) {
        val event = PerformedActionProductDefinitionEvent(
            id = command.id,
            state = command.state
        )
        AggregateLifecycle.apply(event)
    }

    @EventSourcingHandler
    fun on(event: CreatedProductDefinitionEvent) {
        this.id = event.id
        this.identifier = event.identifier
        this.type = event.type
        this.name = event.name
        this.description = event.description
        this.minimumBalance = event.minimumBalance
        this.equityLedgerIdentifier = event.equityLedgerIdentifier
        this.cashAccountIdentifier = event.cashAccountIdentifier
        this.expenseAccountIdentifier = event.expenseAccountIdentifier
        this.accrueAccountIdentifier = event.accrueAccountIdentifier
        this.interest = event.interest
        this.flexible = event.flexible
        this.active = event.active
    }

    @EventSourcingHandler
    fun on(event: UpdatedProductDefinitionEvent) {
        this.id = event.id
        this.identifier = event.identifier
        this.type = event.type
        this.name = event.name
        this.description = event.description
        this.minimumBalance = event.minimumBalance
        this.equityLedgerIdentifier = event.equityLedgerIdentifier
        this.cashAccountIdentifier = event.cashAccountIdentifier
        this.expenseAccountIdentifier = event.expenseAccountIdentifier
        this.accrueAccountIdentifier = event.accrueAccountIdentifier
        this.interest = event.interest
        this.flexible = event.flexible
        this.active = event.active
    }

    @EventSourcingHandler
    fun on(event: DeletedProductDefinitionEvent) {
        this.id = event.id
    }

    @EventSourcingHandler
    fun on(event: ActivateProductDefinitionCommand) {
        this.id = event.id
        this.active = event.state
    }

    @EventSourcingHandler
    fun on(event: DeactivateProductDefinitionCommand) {
        this.id = event.id
        this.active = event.state
    }
}
