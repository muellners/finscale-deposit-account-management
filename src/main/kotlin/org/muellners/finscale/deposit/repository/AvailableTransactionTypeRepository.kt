package org.muellners.finscale.deposit.repository

import org.muellners.finscale.deposit.domain.AvailableTransactionType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Spring Data  repository for the [AvailableTransactionType] entity.
 */
@Suppress("unused")
@Repository
interface AvailableTransactionTypeRepository : JpaRepository<AvailableTransactionType, Long>
