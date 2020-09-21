package org.muellners.finscale.deposit.domain

import java.util.*
import org.junit.jupiter.api.Test
import org.muellners.finscale.deposit.view.TermView
import org.muellners.finscale.deposit.web.rest.equalsVerifier

class TermTest {

    @Test
    fun equalsVerifier() {
        equalsVerifier(TermView::class)
//        val term1 = TermView()
//        term1.id = UUID.randomUUID().toString()
//        val term2 = TermView()
//        term2.id = term1.id
//        assertThat(term1).isEqualTo(term2)
//        term2.id = UUID.randomUUID().toString()
//        assertThat(term1).isNotEqualTo(term2)
//        term1.id = null
//        assertThat(term1).isNotEqualTo(term2)
    }
}
