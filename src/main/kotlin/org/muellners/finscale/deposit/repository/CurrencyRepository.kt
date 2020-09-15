package org.muellners.finscale.deposit.repository

import org.muellners.finscale.deposit.domain.Currency
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Spring Data  repository for the [Currency] entity.
 */
@Suppress("unused")
@Repository
interface CurrencyRepository : JpaRepository<Currency, Long>
