package org.muellners.finscale.deposit.web.rest

import java.net.URISyntaxException
import java.util.*
import java.util.concurrent.CompletableFuture
import javax.validation.Valid
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.messaging.responsetypes.ResponseTypes
import org.axonframework.queryhandling.QueryGateway
import org.muellners.finscale.deposit.command.CreateActionCommand
import org.muellners.finscale.deposit.query.GetAllActionsQuery
import org.muellners.finscale.deposit.repository.ActionViewRepository
import org.muellners.finscale.deposit.service.Action
import org.muellners.finscale.deposit.web.rest.errors.BadRequestAlertException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*

private const val ENTITY_NAME = "depositAccountManagementAction"
/**
 * REST controller for managing [org.muellners.finscale.deposit.view.ActionView].
 */
@RestController
@RequestMapping("/actions")
@Transactional
class ActionResource(
    private val actionViewRepository: ActionViewRepository,
    private val commandGateway: CommandGateway,
    private val queryGateway: QueryGateway
) {

    private val log = LoggerFactory.getLogger(javaClass)
    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  ` : Create a new action.
     *
     * @param action the action to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new action, or with status `400 (Bad Request)` if the action has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    fun createAction(@Valid @RequestBody action: Action): CompletableFuture<Action>? {
        log.debug("REST request to save Action : $action")
        if (action.id != null) {
            throw BadRequestAlertException(
                "A new action cannot already have an ID",
                ENTITY_NAME, "idexists"
            )
        }
        val command = CreateActionCommand(
            id = UUID.randomUUID(),
            identifier = action.identifier,
            name = action.name,
            description = action.description,
            transactionType = action.transactionType
        )
        return commandGateway.send<Action>(command)
    }

    /**
     * `GET  ` : get all the actions.
     *

     * @return the [ResponseEntity] with status `200 (OK)` and the list of actions in body.
     */
    @GetMapping("")
    fun getAllActions(): CompletableFuture<MutableList<Action>>? {
        log.debug("REST request to get all Actions")
        return queryGateway.query(GetAllActionsQuery(), ResponseTypes.multipleInstancesOf((Action::class.java)))
    }
}
