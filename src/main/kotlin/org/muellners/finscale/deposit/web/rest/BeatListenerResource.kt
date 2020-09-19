package org.muellners.finscale.deposit.web.rest

import java.net.URISyntaxException
import java.util.*
import javax.validation.Valid
import org.axonframework.commandhandling.gateway.CommandGateway
import org.muellners.finscale.deposit.command.BeatListenerCommand
import org.muellners.finscale.deposit.service.BeatPublish
import org.muellners.finscale.deposit.web.rest.errors.BadRequestAlertException
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*

private const val ENTITY_NAME = "depositAccountManagementBeatListener"
/**
 * REST controller for managing [org.muellners.finscale.deposit.service.BeatListener].
 */
@RestController
@RequestMapping("/beatlistener")
@Transactional
class BeatListenerResource(
    private val commandGateway: CommandGateway
) {
    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * `POST  ` : Publish a new beat.
     *
     * @param beat the beat to publish.
     * @return the [ResponseEntity] with status `202 (Accepted)` and with body the new beat, or with status `400 (Bad Request)` if the beat has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    fun publishBeat(@Valid @RequestBody beatPublish: BeatPublish): ResponseEntity<Void?>? {
        log.debug("REST request to save Action : $beatPublish")
        if (beatPublish.id != null) {
            throw BadRequestAlertException(
                "A new beat cannot already have an ID",
                ENTITY_NAME, "idexists"
            )
        }
        val command = BeatListenerCommand(
            id = UUID.randomUUID(),
            forTime = beatPublish.forTime
        )
        commandGateway.send<Any>(command)
        return ResponseEntity.accepted().build()
    }
}
