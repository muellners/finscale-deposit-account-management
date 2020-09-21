package org.muellners.finscale.deposit.view

import java.io.Serializable
import javax.persistence.*
import javax.validation.constraints.*
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import org.muellners.finscale.deposit.domain.enumeration.InterestPayable
import org.muellners.finscale.deposit.domain.enumeration.TimeUnit

/**
 * A Term.
 */
@Entity
@Table(name = "term")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
data class TermView(
    @Id
    var id: String? = null,

    @OneToOne
    @JoinColumn(name = "product_identifier", nullable = false, unique = true)
    var productDefinitionView: ProductDefinitionView,

    @Column(name = "period")
    var period: Int? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "time_unit")
    var timeUnit: TimeUnit? = null,

    @get: NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "interest_payable", nullable = false)
    var interestPayable: InterestPayable? = null

    // jhipster-needle-entity-add-field - JHipster will add fields here
) : Serializable {
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TermView) return false

        return id != null && other.id != null && id == other.id
    }

    override fun hashCode() = 31

    override fun toString() = "Term{" +
        "id=$id" +
        ", period=$period" +
        ", timeUnit='$timeUnit'" +
        ", interestPayable='$interestPayable'" +
        "}"

    companion object {
        private const val serialVersionUID = 1L
    }
}
