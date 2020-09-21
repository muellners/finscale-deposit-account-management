package org.muellners.finscale.deposit.view

import javax.persistence.*
import javax.validation.constraints.*
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import org.muellners.finscale.deposit.domain.AbstractAuditingEntity
import org.muellners.finscale.deposit.domain.enumeration.Type

/**
 * A ProductDefinition.
 */
@Entity
@Table(name = "product_definition")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
data class ProductDefinitionView(
    @Id
    var id: String?,

    @Column(name = "identifier", nullable = false, unique = true)
    var identifier: String?,

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    var type: Type?,

    @get: NotNull
    @Column(name = "name", nullable = false)
    var name: String?,

    @Column(name = "description", nullable = false)
    var description: String?,

    @get: NotNull
    @Column(name = "minimum_balance", nullable = false)
    var minimumBalance: Double?,

    @Column(name = "equity_ledger_identifier", nullable = false)
    var equityLedgerIdentifier: String?,

    @Column(name = "cash_account_identifier", nullable = false)
    var cashAccountIdentifier: String?,

    @Column(name = "expense_account_identifier", nullable = false)
    var expenseAccountIdentifier: String?,

    @Column(name = "accrue_account_identifier", nullable = false)
    var accrueAccountIdentifier: String?,

    @Column(name = "interest", nullable = false)
    var interest: Double?,

    @get: NotNull
    @Column(name = "flexible", nullable = false)
    var flexible: Boolean?,

    @Column(name = "active")
    var active: Boolean?

    // jhipster-needle-entity-add-field - JHipster will add fields here
) : AbstractAuditingEntity() {
// ) : Serializable {
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ProductDefinitionView) return false

        return id != null && other.id != null && id == other.id
    }

    override fun hashCode() = 31

    override fun toString() = "ProductDefinition{" +
        "id=$id" +
        ", type='$type'" +
        ", identifier='$identifier'" +
        ", name='$name'" +
        ", description='$description'" +
        ", minimumBalance=$minimumBalance" +
        ", equityLedgerIdentifier='$equityLedgerIdentifier'" +
        ", cashAccountIdentifier='$cashAccountIdentifier'" +
        ", expenseAccountIdentifier='$expenseAccountIdentifier'" +
        ", accrueAccountIdentifier='$accrueAccountIdentifier'" +
        ", interest=$interest" +
        ", flexible='$flexible'" +
        ", active='$active'" +
        "}"
}
