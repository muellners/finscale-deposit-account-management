package org.muellners.finscale.deposit.domain

import java.io.Serializable
import java.util.*
import javax.persistence.*
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener

/**
 * Base abstract class for entities which will hold definitions for created, last modified by, created by,
 * last modified by attributes.
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class AbstractAuditingEntity : Serializable {
    @CreatedBy
    @Column(name = "created_by", insertable = false, updatable = false)
    var createdBy: String? = null

    @CreatedDate
    @Column(name = "created_on", insertable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    var createdOn: Date? = null

    @LastModifiedBy
    @Column(name = "last_modified_by", insertable = false, updatable = false)
    var lastModifiedBy: String? = null

    @LastModifiedDate
    @Column(name = "last_modified_on", insertable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    var lastModifiedOn: Date? = null

    override fun hashCode(): Int {
        return super.hashCode()
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}
