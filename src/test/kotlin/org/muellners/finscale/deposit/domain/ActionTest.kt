package org.muellners.finscale.deposit.domain

import java.util.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.muellners.finscale.deposit.service.Action
import org.muellners.finscale.deposit.web.rest.equalsVerifier

class ActionTest {

    @Test
    fun equalsVerifier() {
        equalsVerifier(Action::class)
        val action1 = Action()
        action1.id = UUID.randomUUID()
        val action2 = Action()
        action2.id = action1.id
        assertThat(action1).isEqualTo(action2)
        action2.id = UUID.randomUUID()
        assertThat(action1).isNotEqualTo(action2)
        action1.id = null
        assertThat(action1).isNotEqualTo(action2)
    }
}
