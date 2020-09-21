package org.muellners.finscale.deposit.domain

import org.junit.jupiter.api.Test
import org.muellners.finscale.deposit.view.ProductInstanceView
import org.muellners.finscale.deposit.web.rest.equalsVerifier

class ProductInstanceViewTest {

    @Test
    fun equalsVerifier() {
        equalsVerifier(ProductInstanceView::class)
//        val productInstance1 = ProductInstanceView()
//        productInstance1.id = 1L
//        val productInstance2 = ProductInstanceView()
//        productInstance2.id = productInstance1.id
//        assertThat(productInstance1).isEqualTo(productInstance2)
//        productInstance2.id = 2L
//        assertThat(productInstance1).isNotEqualTo(productInstance2)
//        productInstance1.id = null
//        assertThat(productInstance1).isNotEqualTo(productInstance2)
    }
}
