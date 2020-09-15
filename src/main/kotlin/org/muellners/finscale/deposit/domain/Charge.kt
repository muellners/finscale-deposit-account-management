package org.muellners.finscale.deposit.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.io.Serializable
import javax.persistence.*
import javax.validation.constraints.*
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import org.muellners.finscale.deposit.view.ProductDefinitionView

/**
 * A Charge.
 */
@Entity
@Table(name = "charge")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
data class Charge(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    var id: Long? = null,

    @Column(name = "action_identifier")
    var actionIdentifier: String? = null,

    @Column(name = "income_account_identifier")
    var incomeAccountIdentifier: String? = null,

    @get: NotNull
    @Column(name = "name", nullable = false)
    var name: String? = null,

    @Column(name = "description")
    var description: String? = null,

    @Column(name = "proportional")
    var proportional: Boolean? = null,

    @Column(name = "amount")
    var amount: Double? = null,

    @ManyToOne @JsonIgnoreProperties(value = ["charges"], allowSetters = true)
    var productDefinitionView: ProductDefinitionView? = null

    // jhipster-needle-entity-add-field - JHipster will add fields here
) : Serializable {
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Charge) return false

        return id != null && other.id != null && id == other.id
    }

    override fun hashCode() = 31

    override fun toString() = "Charge{" +
        "id=$id" +
        ", actionIdentifier='$actionIdentifier'" +
        ", incomeAccountIdentifier='$incomeAccountIdentifier'" +
        ", name='$name'" +
        ", description='$description'" +
        ", proportional='$proportional'" +
        ", amount=$amount" +
        "}"

    companion object {
        private const val serialVersionUID = 1L
    }
}
