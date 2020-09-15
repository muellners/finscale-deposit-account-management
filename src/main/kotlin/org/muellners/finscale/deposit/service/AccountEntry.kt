package org.muellners.finscale.deposit.service

import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

data class AccountEntry(
    var id: UUID? = null,
    var side: Any? = null,
    var amount: BigDecimal? = null,
    var balance: BigDecimal? = null,
    var processedOn: LocalDate? = null,
    var transaction: Any? = null,
    var ledgerAccount: Any? = null
)
