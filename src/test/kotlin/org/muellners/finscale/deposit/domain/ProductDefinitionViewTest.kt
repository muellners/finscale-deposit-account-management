package org.muellners.finscale.deposit.domain

import java.util.*
import org.junit.jupiter.api.Test
import org.muellners.finscale.deposit.view.ProductDefinitionView
import org.muellners.finscale.deposit.web.rest.equalsVerifier

class ProductDefinitionViewTest {

    @Test
    fun equalsVerifier() {
        equalsVerifier(ProductDefinitionView::class)
//        val productDefinition1 = ProductDefinitionView()
//        productDefinition1.id = UUID.randomUUID().toString()
//        val productDefinition2 = ProductDefinitionView()
//        productDefinition2.id = productDefinition1.id
//        assertThat(productDefinition1).isEqualTo(productDefinition2)
//        productDefinition2.id = UUID.randomUUID().toString()
//        assertThat(productDefinition1).isNotEqualTo(productDefinition2)
//        productDefinition1.id = null
//        assertThat(productDefinition1).isNotEqualTo(productDefinition2)
    }
}
