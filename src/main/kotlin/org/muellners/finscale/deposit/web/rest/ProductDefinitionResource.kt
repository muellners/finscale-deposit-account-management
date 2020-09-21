package org.muellners.finscale.deposit.web.rest

import io.github.jhipster.web.util.HeaderUtil
import java.net.URISyntaxException
import java.util.*
import java.util.concurrent.CompletableFuture
import javax.validation.Valid
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.messaging.responsetypes.ResponseTypes
import org.axonframework.queryhandling.QueryGateway
import org.muellners.finscale.deposit.command.*
import org.muellners.finscale.deposit.query.GetAllProductDefinitionsQuery
import org.muellners.finscale.deposit.query.GetDividendDistributionsQuery
import org.muellners.finscale.deposit.query.GetProductDefinitionQuery
import org.muellners.finscale.deposit.service.DividendDistribution
import org.muellners.finscale.deposit.service.ProductDefinition
import org.muellners.finscale.deposit.web.rest.errors.BadRequestAlertException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*

private const val ENTITY_NAME = "depositAccountManagementProductDefinition"
/**
 * REST controller for managing [org.muellners.finscale.deposit.domain.product].
 */
@RestController
@RequestMapping("/definitions")
@Transactional
class ProductDefinitionResource(
    private val commandGateway: CommandGateway,
    private val queryGateway: QueryGateway
) {

    private val log = LoggerFactory.getLogger(javaClass)
    private val ACTIVATE_PRODUCT_DEFINITION_COMMAND = "activate"
    private val DEACTIVATE_PRODUCT_DEFINITION_COMMAND = "deactivate"
    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  ` : Create a new productDefinition.
     *
     * @param productDefinition the productDefinition to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new productDefinition, or with status `400 (Bad Request)` if the productDefinition has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    fun createProductDefinition(@Valid @RequestBody productDefinition: ProductDefinition): CompletableFuture<ProductDefinition>? {
        log.debug("REST request to create a ProductDefinition : $productDefinition")
        if (productDefinition.id != null) {
            throw BadRequestAlertException(
                "A new productDefinition cannot already have an ID",
                ENTITY_NAME, "idexists"
            )
        }
        val command = CreateProductDefinitionCommand(
            id = UUID.randomUUID(),
            identifier = productDefinition.identifier,
            type = productDefinition.type,
            name = productDefinition.name,
            description = productDefinition.description,
            minimumBalance = productDefinition.minimumBalance,
            equityLedgerIdentifier = productDefinition.equityLedgerIdentifier,
            cashAccountIdentifier = productDefinition.cashAccountIdentifier,
            expenseAccountIdentifier = productDefinition.expenseAccountIdentifier,
            accrueAccountIdentifier = productDefinition.accrueAccountIdentifier,
            interest = productDefinition.interest,
            flexible = productDefinition.flexible,
            active = productDefinition.active
        )
        return commandGateway.send<ProductDefinition>(command)
    }

    /**
     * `POST  /:id` : Perform an action on a productDefinition.
     *
     * @param id of the productDefinition
     * @param command to perform on the productDefinition.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new productInstance, or with status `400 (Bad Request)` if the productInstance has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/{id}")
    fun performActionOnProductInstance(@PathVariable("id") id: String, @RequestBody command: String): CompletableFuture<ProductDefinition>? {
        log.debug("REST request to perform action : $command")
        return when (command.toUpperCase()) {
            ACTIVATE_PRODUCT_DEFINITION_COMMAND -> commandGateway.send<ProductDefinition>(
                ActivateProductDefinitionCommand(UUID.fromString(id), true)
            )
            DEACTIVATE_PRODUCT_DEFINITION_COMMAND -> commandGateway.send<ProductDefinition>(
                DeactivateProductDefinitionCommand(UUID.fromString(id), false)
            )
            else -> throw BadRequestAlertException("Unsupported command $command", ENTITY_NAME, "unknown command")
        }
    }

    /**
     * `GET  ` : get all the productDefinitions.
     *
     * @return the [ResponseEntity] with status `200 (OK)` and the list of productDefinitions in body.
     */
    @GetMapping("")
    fun getAllProductDefinitions(): CompletableFuture<MutableList<ProductDefinition>>? {
        log.debug("REST request to get all ProductDefinitions")
        return queryGateway.query<MutableList<ProductDefinition>, GetAllProductDefinitionsQuery>(
            GetAllProductDefinitionsQuery(),
            ResponseTypes.multipleInstancesOf(ProductDefinition::class.java)
        )
    }

    /**
     * `GET  /:id` : get the "id" productDefinition.
     *
     * @param id the id of the productDefinition to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the productDefinition, or with status `404 (Not Found)`.
     */
    @GetMapping("/{id}")
    fun getProductDefinition(@PathVariable id: String): CompletableFuture<ProductDefinition>? {
        log.debug("REST request to get ProductDefinition : $id")
        return queryGateway.query<ProductDefinition, GetProductDefinitionQuery>(
            GetProductDefinitionQuery(UUID.fromString(id)),
            ResponseTypes.instanceOf(ProductDefinition::class.java)
        )
    }

    /**
     *  `DELETE  /:id` : delete the "id" productDefinition.
     *
     * @param id the id of the productDefinition to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/{id}")
    fun deleteProductDefinition(@PathVariable id: String): ResponseEntity<Void> {
        log.debug("REST request to delete ProductDefinition : $id")
            commandGateway.send<String>(DeleteProductDefinitionCommand(UUID.fromString(id)))
            return ResponseEntity.noContent()
                .headers(
                    HeaderUtil.createEntityDeletionAlert(
                        applicationName, true, ENTITY_NAME, id.toString())
                ).build()
    }

    /**
     * `POST  /:id` : Perform an action on a productDefinition.
     *
     * @param id of the productDefinition
     * @param productDefinition's dividendDistribution
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new productInstance, or with status `400 (Bad Request)` if the productInstance has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/{id}/dividends")
    fun dividendDistributions(@PathVariable("id") id: String, @RequestBody dividendDistribution: DividendDistribution): CompletableFuture<DividendDistribution>? {
        log.debug("REST request to calculate dividend distributions : $dividendDistribution")
        val command = DividendDistributionCommand(
            id = UUID.randomUUID(),
            productIdentifier = id,
            dueDate = dividendDistribution.dueDate,
            rate = dividendDistribution.rate
        )
        return commandGateway.send<DividendDistribution>(command)
    }

    /**
     * `GET  /:id/dividends` : get the "id" productDefinition.
     *
     * @param id the id of the productDefinition to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the productDefinition, or with status `404 (Not Found)`.
     */
    @GetMapping("/{id}/dividends")
    fun getDividendDistribution(@PathVariable id: String): CompletableFuture<DividendDistribution>? {
        log.debug("REST request to get DividendDistributions : $id")
        return queryGateway.query<DividendDistribution, GetDividendDistributionsQuery>(
            GetDividendDistributionsQuery(UUID.fromString(id)),
            ResponseTypes.instanceOf(DividendDistribution::class.java)
        )
    }
}
