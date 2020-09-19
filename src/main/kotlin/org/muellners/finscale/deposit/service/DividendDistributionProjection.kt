package org.muellners.finscale.deposit.service

import java.lang.IllegalArgumentException
import org.axonframework.eventhandling.EventHandler
import org.axonframework.queryhandling.QueryHandler
import org.muellners.finscale.deposit.event.DividendDistributedEvent
import org.muellners.finscale.deposit.query.GetDividendDistributionsQuery
import org.muellners.finscale.deposit.repository.DividendDistributionViewRepository
import org.muellners.finscale.deposit.repository.ProductDefinitionViewRepository
import org.muellners.finscale.deposit.service.mapper.DividendDistributionMapper
import org.muellners.finscale.deposit.view.DividendDistributionView
import org.springframework.stereotype.Component

@Component
class DividendDistributionProjection(
    val productDefinitionViewRepository: ProductDefinitionViewRepository,
    val dividendDistributionViewRepository: DividendDistributionViewRepository,
    val dividendDistributionMapper: DividendDistributionMapper
) {
    @EventHandler
    fun on(event: DividendDistributedEvent): DividendDistribution {
        val optionalProductDefinition = productDefinitionViewRepository.findById(event.productIdentifier.toString())
        if (optionalProductDefinition.isPresent) {
            var dividendDistributionView = DividendDistributionView()
            dividendDistributionView.id = event.id.toString()
            dividendDistributionView.productDefinitionView = optionalProductDefinition.get()
            dividendDistributionView.dueDate = event.dueDate
            dividendDistributionView.rate = event.rate
            dividendDistributionView = dividendDistributionViewRepository.save(dividendDistributionView)
            return dividendDistributionMapper.map(dividendDistributionView)
        } else {
            throw IllegalArgumentException("Dividend must have a product definition id")
        }
    }

    @QueryHandler
    fun handle(query: GetDividendDistributionsQuery): DividendDistribution {
        val optionalDividendDistributionView = dividendDistributionViewRepository.findById(query.id.toString())
        if (optionalDividendDistributionView.isPresent) {
            return dividendDistributionMapper.map(optionalDividendDistributionView.get())
        } else {
            throw IllegalArgumentException("Dividend distribution id cannot be null")
        }
    }
}
