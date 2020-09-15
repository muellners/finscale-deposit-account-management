package org.muellners.finscale.deposit.service

import java.math.BigDecimal
import java.util.*

data class Account(
    var id: UUID? = null,
    var identifier: String? = null,
    var alternativeAccountNumber: String? = null,
    var name: String? = null,
    var type: Any? = null,
    var holders: String? = null,
    var signatureAuthorities: String? = null,
    var state: Any? = null,
    var balance: BigDecimal? = null,
    var actions: MutableSet<Any> = mutableSetOf(),
    var entries: MutableSet<Any> = mutableSetOf(),
    var ledger: Any? = null,
    var referenceAccount: Any? = null
)
