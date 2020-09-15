package org.muellners.finscale.deposit.repository

import java.util.*
import org.muellners.finscale.deposit.view.ProductDefinitionView
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Spring Data  repository for the [ProductDefinitionView] entity.
 */
@Suppress("unused")
@Repository
interface ProductDefinitionViewRepository : JpaRepository<ProductDefinitionView, String> {
    fun findByIdentifier(productDefinition: Any): Optional<ProductDefinitionView>
}
