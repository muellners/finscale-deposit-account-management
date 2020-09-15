package org.muellners.finscale.deposit.service.mapper

import java.lang.IllegalArgumentException
import java.util.*
import org.muellners.finscale.deposit.repository.ProductDefinitionViewRepository
import org.muellners.finscale.deposit.service.DividendDistribution
import org.muellners.finscale.deposit.view.DividendDistributionView
import org.springframework.stereotype.Service

@Service
class DividendDistributionMapper(
    val productDefinitionViewRepository: ProductDefinitionViewRepository
) {
    fun map(dividendDistribution: DividendDistribution): DividendDistributionView {
        val optionalProductDefinitionView = productDefinitionViewRepository.findById(dividendDistribution.productDefinitionId.toString())
        if (optionalProductDefinitionView.isPresent) {
            return DividendDistributionView(
                id = dividendDistribution.id?.toString(),
                productDefinitionView = optionalProductDefinitionView.get(),
                dueDate = dividendDistribution.dueDate,
                rate = dividendDistribution.rate
            )
        } else {
            throw IllegalArgumentException("Dividend must have a product definition id")
        }
    }

    fun map(dividendDistributionView: DividendDistributionView): DividendDistribution {
        val dividendDistribution = DividendDistribution()
        dividendDistribution.id = UUID.fromString(dividendDistributionView.id)
        dividendDistribution.productDefinitionId = dividendDistributionView.productDefinitionView!!.id
        dividendDistribution.dueDate = dividendDistributionView.dueDate
        dividendDistribution.rate = dividendDistributionView.rate
        return dividendDistribution
    }
}
