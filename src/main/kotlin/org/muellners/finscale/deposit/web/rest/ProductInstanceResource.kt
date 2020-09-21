package org.muellners.finscale.deposit.web.rest

import io.github.jhipster.web.util.HeaderUtil
import java.net.URISyntaxException
import java.util.*
import java.util.concurrent.CompletableFuture
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.messaging.responsetypes.ResponseTypes
import org.axonframework.queryhandling.QueryGateway
import org.muellners.finscale.deposit.command.*
import org.muellners.finscale.deposit.query.GetAllProductInstancesQuery
import org.muellners.finscale.deposit.query.GetProductInstanceQuery
import org.muellners.finscale.deposit.service.ProductInstance
import org.muellners.finscale.deposit.web.rest.errors.BadRequestAlertException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*

private const val ENTITY_NAME = "depositAccountManagementProductInstance"
/**
 * REST controller for managing [org.muellners.finscale.deposit.domain.productInstance].
 */
@RestController
@RequestMapping("/product-instances")
@Transactional
class ProductInstanceResource(
    private val commandGateway: CommandGateway,
    private val queryGateway: QueryGateway
) {

    private val log = LoggerFactory.getLogger(javaClass)
    private val ACTIVATE_PRODUCT_INSTANCE_COMMAND = "activate"
    private val DEACTIVATE_PRODUCT_INSTANCE_COMMAND = "deactivate"
    private val PRODUCT_INSTANCE_TRANSACTION = "transaction"
    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  ` : Create a new productInstance.
     * @param productInstance the productInstance to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new productInstance, or with status `400 (Bad Request)` if the productInstance has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    fun createProductInstance(@RequestBody productInstance: ProductInstance): CompletableFuture<ProductInstance>? {
        log.debug("REST request to save ProductInstance : $productInstance")
        val command = CreateProductInstanceCommand(

            id = UUID.randomUUID(),
            productIdentifier = productInstance.productIdentifier,
            customerIdentifier = productInstance.customerIdentifier,
            accountIdentifier = productInstance.accountIdentifier,
            beneficiaries = productInstance.beneficiaries,
            openedOn = productInstance.openedOn,
            lastTransactionDate = productInstance.lastTransactionDate,
            state = productInstance.state
        )
        return commandGateway.send<ProductInstance>(command)
    }

    /**
     * `POST  ` : Perform an action on a productInstance.
     *
     * @param id of the productInstance
     * @param command to perform on the productInstance.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new productInstance, or with status `400 (Bad Request)` if the productInstance has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/{id}")
    fun performActionOnProductInstance(@PathVariable("id") id: String, @RequestBody command: String): CompletableFuture<ProductInstance>? {
        log.debug("REST request to perform action : $command")
        return when (command.toUpperCase()) {
            ACTIVATE_PRODUCT_INSTANCE_COMMAND -> commandGateway.send<ProductInstance>(ActivateProductInstanceCommand(UUID.fromString(id), true))
            DEACTIVATE_PRODUCT_INSTANCE_COMMAND -> commandGateway.send<ProductInstance>(DeactivateProductInstanceCommand(UUID.fromString(id), false))
            PRODUCT_INSTANCE_TRANSACTION -> commandGateway.send<ProductInstance>(TransactionProcessedCommand(UUID.fromString(id)))
            else -> throw BadRequestAlertException("Unsupported command $command", ENTITY_NAME, "unknown command")
        }
    }

    /**
     * `PUT  ` : Updates an existing productInstance.
     *
     * @param productInstance the productInstance to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated productInstance,
     * or with status `400 (Bad Request)` if the productInstance is not valid,
     * or with status `500 (Internal Server Error)` if the productInstance couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    fun updateProductInstance(@PathVariable("id") id: String, @RequestBody productInstance: ProductInstance): CompletableFuture<ProductInstance>? {
        log.debug("REST request to update ProductInstance : $productInstance")
        if (productInstance.id != UUID.fromString(id) || productInstance.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        val command = UpdateProductInstanceCommand(
            id = UUID.fromString(id),
            productIdentifier = productInstance.productIdentifier,
            customerIdentifier = productInstance.customerIdentifier,
            accountIdentifier = productInstance.accountIdentifier,
            beneficiaries = productInstance.beneficiaries,
            openedOn = productInstance.openedOn,
            lastTransactionDate = productInstance.lastTransactionDate,
            state = productInstance.state
        )
        return commandGateway.send<ProductInstance>(command)
    }

    /**
     * `GET  ` : get all the productInstances.
     *

     * @return the [ResponseEntity] with status `200 (OK)` and the list of productInstances in body.
     */
    @GetMapping("")
    fun getAllProductInstances(): CompletableFuture<MutableList<ProductInstance>>? {
        log.debug("REST request to get all ProductInstances")
        return queryGateway.query(GetAllProductInstancesQuery(), ResponseTypes.multipleInstancesOf((ProductInstance::class.java)))
    }

    /**
     * `GET  /:id` : get the "id" productInstance.
     *
     * @param id the id of the productInstance to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the productInstance, or with status `404 (Not Found)`.
     */
    @GetMapping("/{id}")
    fun getProductInstance(@PathVariable id: String): CompletableFuture<ProductInstance>? {
        log.debug("REST request to get ProductInstance : $id")
        return queryGateway.query(GetProductInstanceQuery(UUID.fromString(id)), ResponseTypes.instanceOf((ProductInstance::class.java)))
    }

    /**
     *  `DELETE  /:id` : delete the "id" productInstance.
     *
     * @param id the id of the productInstance to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/{id}")
    fun deleteProductInstance(@PathVariable id: String): ResponseEntity<Void> {
        log.debug("REST request to delete ProductInstance : $id")
            commandGateway.send<String>(DeleteProductInstanceCommand(UUID.fromString(id)))
            return ResponseEntity.noContent()
                .headers(
                    HeaderUtil.createEntityDeletionAlert(
                        applicationName, true, ENTITY_NAME, id.toString())
                ).build()
    }
}
