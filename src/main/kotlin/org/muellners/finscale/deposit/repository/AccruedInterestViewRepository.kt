package org.muellners.finscale.deposit.repository

import java.util.*
import org.muellners.finscale.deposit.view.AccruedInterestView
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Spring Data  repository for the [Accruedinterest] entity.
 */
@Suppress("unused")
@Repository
interface AccruedInterestViewRepository : JpaRepository<AccruedInterestView, String> {
    fun findByCustomerAccountIdentifier(customerAccountIdentifier: String): Optional<AccruedInterestView>
}
