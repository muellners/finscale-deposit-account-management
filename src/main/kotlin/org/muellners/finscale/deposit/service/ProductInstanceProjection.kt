package org.muellners.finscale.deposit.service

import java.lang.IllegalArgumentException
import java.util.*
import org.axonframework.eventhandling.EventHandler
import org.axonframework.queryhandling.QueryHandler
import org.muellners.finscale.deposit.event.*
import org.muellners.finscale.deposit.query.GetAllProductInstancesQuery
import org.muellners.finscale.deposit.query.GetProductInstanceQuery
import org.muellners.finscale.deposit.repository.ProductDefinitionViewRepository
import org.muellners.finscale.deposit.repository.ProductInstanceViewRepository
import org.muellners.finscale.deposit.service.mapper.ProductInstanceMapper
import org.muellners.finscale.deposit.view.ProductInstanceView
import org.springframework.stereotype.Component

@Component
class ProductInstanceProjection(
    val productInstanceViewRepository: ProductInstanceViewRepository,
    val productDefinitionViewRepository: ProductDefinitionViewRepository,
    val productInstanceMapper: ProductInstanceMapper
) {
    @EventHandler
    fun on(event: CreatedProductInstanceEvent): ProductInstance {
        val optionalProductDefinitionView = productDefinitionViewRepository.findById(event.productIdentifier!!)
        if (!optionalProductDefinitionView.isPresent) {
            throw IllegalArgumentException("Product definition id cannot be null")
        } else {
            val productInstanceView = ProductInstanceView(
                id = event.id.toString(),
                customerIdentifier = event.customerIdentifier,
                productDefinitionView = optionalProductDefinitionView.get(),
                accountIdentifier = event.accountIdentifier,
                beneficiaries = event.beneficiaries,
                openedOn = event.openedOn,
                lastTransactionDate = event.lastTransactionDate,
                state = event.state
            )
            return productInstanceMapper.map(productInstanceViewRepository.save(productInstanceView))
        }
    }

    @EventHandler
    fun on(event: UpdatedProductInstanceEvent): ProductInstance {

        val optionalProductInstanceView = productInstanceViewRepository.findById(event.id!!.toString())
        val optionalProductDefinitionView = productDefinitionViewRepository.findById(event.productIdentifier!!)
        if (!optionalProductInstanceView.isPresent) {
            throw IllegalArgumentException("Product instance id cannot be null")
        } else if (!optionalProductDefinitionView.isPresent) {
            throw IllegalArgumentException("Product definition id cannot be null")
        } else {
            val productInstanceView = optionalProductInstanceView.get()
            productInstanceView.customerIdentifier = event.customerIdentifier
            productInstanceView.productDefinitionView = optionalProductDefinitionView.get()
            productInstanceView.accountIdentifier = event.accountIdentifier
            productInstanceView.beneficiaries = event.beneficiaries
            productInstanceView.openedOn = event.openedOn
            productInstanceView.lastTransactionDate = event.lastTransactionDate
            productInstanceView.state = event.state
            return productInstanceMapper.map(productInstanceViewRepository.save(productInstanceView))
        }
    }

    @EventHandler
    fun on(event: PerformedActionProductInstanceEvent): ProductInstance {
        val productInstanceView = productInstanceViewRepository.findById(event.id!!.toString()).orElse(null)
        productInstanceView.state = event.state
        return productInstanceMapper.map(productInstanceViewRepository.save(productInstanceView))
    }

    @EventHandler
    fun on(event: DeletedProductInstanceEvent) {
        val productInstanceView = productInstanceViewRepository.findById(event.id!!.toString()).orElse(null)!!
        return productInstanceViewRepository.delete(productInstanceView)
    }

    @EventHandler
    fun on(event: TransactionProcessedEvent): ProductInstance {
        val productInstanceView = productInstanceViewRepository.findById(event.id!!.toString()).orElse(null)!!
        productInstanceView.lastTransactionDate = Calendar.getInstance().time
        return productInstanceMapper.map(productInstanceViewRepository.save(productInstanceView))
    }

    @QueryHandler
    fun handle(query: GetProductInstanceQuery): ProductInstance {
        return productInstanceMapper.map(productInstanceViewRepository.findById(query.id!!.toString()).orElse(null)!!)
    }

    @QueryHandler
    fun handle(query: GetAllProductInstancesQuery): MutableList<ProductInstance> {
        val productInstances: MutableList<ProductInstance> = mutableListOf()
        val productInstancViews = productInstanceViewRepository.findAll()
        productInstancViews.forEach {
            productInstances.add(productInstanceMapper.map(it))
        }
        return productInstances
    }
}
