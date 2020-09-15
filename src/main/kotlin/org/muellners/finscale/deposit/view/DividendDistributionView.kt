package org.muellners.finscale.deposit.view

import java.io.Serializable
import java.time.LocalDate
import java.util.*
import javax.persistence.*
import javax.validation.constraints.*
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate

/**
 * A DividendDistribution.
 */
@Entity
@Table(name = "dividend_distribution")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
data class DividendDistributionView(
    @Id
    var id: String? = null,

    @ManyToOne(fetch = FetchType.EAGER, optional = false, cascade = [CascadeType.ALL])
    @JoinColumn(name = "product_definition_id", nullable = false)
    var productDefinitionView: ProductDefinitionView?,

    @get: NotNull
    @Column(name = "due_date", nullable = false)
    var dueDate: LocalDate? = null,

    @get: NotNull
    @Column(name = "rate", nullable = false)
    var rate: Double? = null,

    @CreatedBy
    @Column(name = "created_by", insertable = false, updatable = false)
    var createdBy: String? = null,

    @CreatedDate
    @Column(name = "created_on", insertable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    var createdOn: Date? = null

) : Serializable {

    constructor() : this(id = null, productDefinitionView = null, dueDate = null, rate = null, createdBy = null, createdOn = null) {
        var id: String? = null
        var productDefinitionView: ProductDefinitionView
        var dueDate: LocalDate? = null
        var rate: Double? = null
        var createdBy: String? = null
        var createdOn: Date? = null
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DividendDistributionView) return false

        return id != null && other.id != null && id == other.id
    }

    override fun hashCode() = 31

    override fun toString() = "DividendDistribution{" +
        "id=$id" +
        ", productDefinition='$productDefinitionView'" +
        ", dueDate='$dueDate'" +
        ", dividendRate='$rate'" +
        ", createdBy='$createdBy'" +
        ", createdOn='$createdOn'" +
        "}"

    companion object {
        private const val serialVersionUID = 1L
    }
}
