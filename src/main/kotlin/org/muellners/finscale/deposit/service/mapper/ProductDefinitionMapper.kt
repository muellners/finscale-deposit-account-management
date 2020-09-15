package org.muellners.finscale.deposit.service.mapper

import java.util.*
import org.muellners.finscale.deposit.domain.productInstance.commands.ProductDefinition
import org.muellners.finscale.deposit.view.ProductDefinitionView
import org.springframework.stereotype.Service

@Service
class ProductDefinitionMapper() {
    fun map(productDefinition: ProductDefinition): ProductDefinitionView {
        val productDefinitionView = ProductDefinitionView(
            id = productDefinition.id.toString(),
            identifier = productDefinition.identifier,
            type = productDefinition.type,
            name = productDefinition.name,
            description = productDefinition.description,
            minimumBalance = productDefinition.minimumBalance,
            equityLedgerIdentifier = productDefinition.equityLedgerIdentifier,
            cashAccountIdentifier = productDefinition.cashAccountIdentifier,
            expenseAccountIdentifier = productDefinition.expenseAccountIdentifier,
            accrueAccountIdentifier = productDefinition.accrueAccountIdentifier,
            interest = productDefinition.interest,
            flexible = productDefinition.flexible,
            active = productDefinition.active
        )
        return productDefinitionView
    }

    fun map(productDefinitionView: ProductDefinitionView): ProductDefinition {
        val productDefinition = ProductDefinition()
        productDefinition.id = UUID.fromString(productDefinitionView.id)
        productDefinition.identifier = productDefinitionView.identifier
        productDefinition.type = productDefinitionView.type
        productDefinition.name = productDefinitionView.name
        productDefinition.description = productDefinitionView.description
        productDefinition.minimumBalance = productDefinitionView.minimumBalance
        productDefinition.equityLedgerIdentifier = productDefinitionView.equityLedgerIdentifier
        productDefinition.cashAccountIdentifier = productDefinitionView.cashAccountIdentifier
        productDefinition.expenseAccountIdentifier = productDefinitionView.expenseAccountIdentifier
        productDefinition.accrueAccountIdentifier = productDefinitionView.accrueAccountIdentifier
        productDefinition.interest = productDefinitionView.interest
        productDefinition.flexible = productDefinitionView.flexible
        productDefinition.active = productDefinitionView.active
        return productDefinition
    }
}
