package org.muellners.finscale.deposit.service

import java.lang.IllegalStateException
import org.axonframework.eventhandling.EventHandler
import org.axonframework.queryhandling.QueryHandler
import org.muellners.finscale.deposit.domain.productInstance.commands.ProductDefinition
import org.muellners.finscale.deposit.event.CreatedProductDefinitionEvent
import org.muellners.finscale.deposit.event.DeletedProductDefinitionEvent
import org.muellners.finscale.deposit.event.PerformedActionProductDefinitionEvent
import org.muellners.finscale.deposit.event.UpdatedProductDefinitionEvent
import org.muellners.finscale.deposit.query.GetAllProductDefinitionsQuery
import org.muellners.finscale.deposit.query.GetDividendDistributionsQuery
import org.muellners.finscale.deposit.query.GetProductDefinitionQuery
import org.muellners.finscale.deposit.repository.DividendDistributionViewRepository
import org.muellners.finscale.deposit.repository.ProductDefinitionViewRepository
import org.muellners.finscale.deposit.service.mapper.DividendDistributionMapper
import org.muellners.finscale.deposit.service.mapper.ProductDefinitionMapper
import org.muellners.finscale.deposit.view.ProductDefinitionView
import org.springframework.stereotype.Component

@Component
class ProductDefinitionProjection(
    val productDefinitionViewRepository: ProductDefinitionViewRepository,
    val dividendDistributionViewRepository: DividendDistributionViewRepository,
    val productDefinitionMapper: ProductDefinitionMapper,
    val dividendDistributionMapper: DividendDistributionMapper
) {
    @EventHandler
    fun on(event: CreatedProductDefinitionEvent): ProductDefinition {
        val productDefinitionView = ProductDefinitionView(
            id = event.id.toString(),
            identifier = event.identifier,
            type = event.type,
            name = event.name,
            description = event.description,
            minimumBalance = event.minimumBalance,
            equityLedgerIdentifier = event.equityLedgerIdentifier,
            cashAccountIdentifier = event.cashAccountIdentifier,
            expenseAccountIdentifier = event.expenseAccountIdentifier,
            accrueAccountIdentifier = event.accrueAccountIdentifier,
            interest = event.interest,
            flexible = event.flexible,
            active = event.active
        )
        return productDefinitionMapper.map(productDefinitionViewRepository.save(productDefinitionView))
    }

    @EventHandler
    fun on(event: UpdatedProductDefinitionEvent): ProductDefinition {
        val productDefinitionView = productDefinitionViewRepository.findById(event.id!!.toString()).orElse(null)!!
            productDefinitionView.identifier = event.identifier
            productDefinitionView.type = event.type
            productDefinitionView.name = event.name
            productDefinitionView.description = event.description
            productDefinitionView.minimumBalance = event.minimumBalance
            productDefinitionView.equityLedgerIdentifier = event.equityLedgerIdentifier
            productDefinitionView.cashAccountIdentifier = event.cashAccountIdentifier
            productDefinitionView.expenseAccountIdentifier = event.expenseAccountIdentifier
            productDefinitionView.accrueAccountIdentifier = event.accrueAccountIdentifier
            productDefinitionView.interest = event.interest
            productDefinitionView.flexible = event.flexible
            productDefinitionView.active = event.active
        return productDefinitionMapper.map(productDefinitionViewRepository.save(productDefinitionView))
    }

    @EventHandler
    fun on(event: PerformedActionProductDefinitionEvent): ProductDefinition {
        val productDefinitionView = productDefinitionViewRepository.findById(event.id!!.toString()).orElse(null)!!
        productDefinitionView.active = event.state
        return productDefinitionMapper.map(productDefinitionViewRepository.save(productDefinitionView))
    }

    @EventHandler
    fun on(event: DeletedProductDefinitionEvent) {
        val productDefinitionView = productDefinitionViewRepository.findById(event.id!!.toString()).orElse(null)!!
        return productDefinitionViewRepository.delete(productDefinitionView)
    }

    @QueryHandler
    fun handle(query: GetAllProductDefinitionsQuery): MutableList<ProductDefinition> {
        val productDefinitionViews = productDefinitionViewRepository.findAll()
        val productDefinitions: MutableList<ProductDefinition> = mutableListOf()
        productDefinitionViews.forEach {
            productDefinitions.add(productDefinitionMapper.map(it))
        }
        return productDefinitions
    }

    @QueryHandler
    fun handle(query: GetProductDefinitionQuery): ProductDefinition {
        val productDefinitionView = productDefinitionViewRepository.findById(query.id!!.toString())
        if (productDefinitionView.isEmpty) {
            throw IllegalStateException("Product definition not found")
        } else {
            return productDefinitionMapper.map(productDefinitionView.get())
        }
    }

    @QueryHandler
    fun handle(query: GetDividendDistributionsQuery): DividendDistribution {
        val optionalProductDefinitionView = productDefinitionViewRepository.findById(query.id!!.toString())
        if (optionalProductDefinitionView.isEmpty) {
            throw IllegalStateException("Product definition not found")
        } else {
            val productDefinitionView = optionalProductDefinitionView.get()
            val dividendDistributionView = dividendDistributionViewRepository.findByProductDefinitionViewOrderByDueDateAsc(productDefinitionView)
            return dividendDistributionMapper.map(dividendDistributionView)
        }
    }
}
