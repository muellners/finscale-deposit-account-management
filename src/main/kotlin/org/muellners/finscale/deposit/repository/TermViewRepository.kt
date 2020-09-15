package org.muellners.finscale.deposit.repository

import org.muellners.finscale.deposit.view.ProductDefinitionView
import org.muellners.finscale.deposit.view.TermView
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Spring Data  repository for the [TermView] entity.
 */
@Suppress("unused")
@Repository
interface TermViewRepository : JpaRepository<TermView, String> {
    fun findByProductDefinitionView(productDefinitionView: ProductDefinitionView): TermView
}
