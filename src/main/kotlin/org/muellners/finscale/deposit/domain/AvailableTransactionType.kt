package org.muellners.finscale.deposit.domain

import java.io.Serializable
import javax.persistence.*
import javax.validation.constraints.*
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy

/**
 * A AvailableTransactionType.
 */
@Entity
@Table(name = "available_transaction_type")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
data class AvailableTransactionType(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    var id: Long? = null,

    @get: NotNull
    @Column(name = "transaction_type", nullable = false)
    var transactionType: String? = null

    // jhipster-needle-entity-add-field - JHipster will add fields here
) : Serializable {
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AvailableTransactionType) return false

        return id != null && other.id != null && id == other.id
    }

    override fun hashCode() = 31

    override fun toString() = "AvailableTransactionType{" +
        "id=$id" +
        ", transactionType='$transactionType'" +
        "}"

    companion object {
        private const val serialVersionUID = 1L
    }
}
