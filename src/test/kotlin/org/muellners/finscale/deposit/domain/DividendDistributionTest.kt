package org.muellners.finscale.deposit.domain

import org.junit.jupiter.api.Test
import org.muellners.finscale.deposit.view.DividendDistributionView
import org.muellners.finscale.deposit.web.rest.equalsVerifier

class DividendDistributionTest {

    @Test
    fun equalsVerifier() {
        equalsVerifier(DividendDistributionView::class)
//        val dividendDistribution1 = DividendDistributionView()
//        dividendDistribution1.id = 1L
//        val dividendDistribution2 = DividendDistributionView()
//        dividendDistribution2.id = dividendDistribution1.id
//        assertThat(dividendDistribution1).isEqualTo(dividendDistribution2)
//        dividendDistribution2.id = 2L
//        assertThat(dividendDistribution1).isNotEqualTo(dividendDistribution2)
//        dividendDistribution1.id = null
//        assertThat(dividendDistribution1).isNotEqualTo(dividendDistribution2)
    }
}
