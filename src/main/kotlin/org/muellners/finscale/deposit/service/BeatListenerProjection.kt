package org.muellners.finscale.deposit.service

import java.time.LocalDate
import java.util.*
import java.util.concurrent.CompletableFuture
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.eventhandling.EventHandler
import org.muellners.finscale.deposit.command.AccrualCommand
import org.muellners.finscale.deposit.command.PayInterestCommand
import org.muellners.finscale.deposit.event.BeatListenedEvent
import org.springframework.stereotype.Component

@Component
class BeatListenerProjection(
    private val commandGateway: CommandGateway
) {
    @EventHandler
    fun on(event: BeatListenedEvent): CompletableFuture<String>? {
        val date = commandGateway.send<LocalDate>(
            AccrualCommand(
                id = UUID.randomUUID(),
                dueDate = event.forTime
            )
        )
        return commandGateway.send<String>(
            PayInterestCommand(
                id = UUID.randomUUID(),
                date = date.get()
            )
        )
    }
}
