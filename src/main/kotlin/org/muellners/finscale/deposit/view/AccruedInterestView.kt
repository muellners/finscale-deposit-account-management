package org.muellners.finscale.deposit.view

import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy

/**
 * An Accrued Interest.
 */
@Entity
@Table(name = "accrued_interest")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
data class AccruedInterestView(
    @Id
    @Column(name = "id")
    var id: String? = null,

    @Column(name = "accrue_account_identifier", nullable = false)
    var accrueAccountIdentifier: String? = null,

    @Column(name = "customer_account_identifier", nullable = false)
    var customerAccountIdentifier: String? = null,

    @Column(name = "amount", nullable = false)
    var amount: Double? = null

) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TermView) return false

        return id != null && other.id != null && id == other.id
    }

    override fun hashCode() = 31

    override fun toString() = "Term{" +
        "id=$id" +
        ", accrueAccountIdentifier=$accrueAccountIdentifier" +
        ", customerAccountIdentifier='$customerAccountIdentifier'" +
        ", amount='$amount'" +
        "}"

    companion object {
        private const val serialVersionUID = 1L
    }
}
