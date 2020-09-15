package org.muellners.finscale.deposit.repository

import org.muellners.finscale.deposit.view.DividendDistributionView
import org.muellners.finscale.deposit.view.ProductDefinitionView
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Spring Data  repository for the [DividendDistributionView] entity.
 */
@Suppress("unused")
@Repository
interface DividendDistributionViewRepository : JpaRepository<DividendDistributionView, String> {
    fun findByProductDefinitionViewOrderByDueDateAsc(productDefinitionView: ProductDefinitionView): DividendDistributionView
}
