package org.muellners.finscale.deposit.view

import java.util.*
import javax.persistence.*
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import org.muellners.finscale.deposit.domain.AbstractAuditingEntity

/**
 * A ProductInstance.
 */
@Entity
@Table(name = "product_instance")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
data class ProductInstanceView(
    @Id
    var id: String? = null,

    @ManyToOne(fetch = FetchType.EAGER, optional = false, cascade = [CascadeType.ALL])
    @JoinColumn(name = "product_identifier", nullable = false)
    var productDefinitionView: ProductDefinitionView? = null,

    @Column(name = "customer_identifier", nullable = false)
    var customerIdentifier: String? = null,

    @Column(name = "account_identifier", nullable = false)
    var accountIdentifier: String? = null,

    @Column(name = "beneficiaries", nullable = false)
    var beneficiaries: String? = null,

    @Column(name = "opened_on", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    var openedOn: Date? = null,

    @Column(name = "last_transaction_date", nullable = false)
    var lastTransactionDate: Date? = null,

    @Column(name = "state", nullable = false)
    var state: Boolean? = null

) : AbstractAuditingEntity() {
// ) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ProductInstanceView) return false

        return id != null && other.id != null && id == other.id
    }

    override fun hashCode() = 31

    override fun toString() = "ProductInstance{" +
        "id=$id" +
        ", productDefinition='$productDefinitionView'" +
        ", customerIdentifier='$customerIdentifier'" +
        ", accountIdentifier='$accountIdentifier'" +
        ", beneficiaries='$beneficiaries'" +
        ", lastTransactionDate='$lastTransactionDate'" +
        ", state='$state'" +
        "}"

    companion object {
        private const val serialVersionUID = 1L
    }
}
