package org.muellners.finscale.deposit.service.mapper

import java.lang.IllegalArgumentException
import java.util.*
import org.muellners.finscale.deposit.repository.ProductDefinitionViewRepository
import org.muellners.finscale.deposit.service.ProductInstance
import org.muellners.finscale.deposit.view.ProductInstanceView
import org.springframework.stereotype.Service

@Service
class ProductInstanceMapper(
    val productDefinitionViewRepository: ProductDefinitionViewRepository
) {
    fun map(productInstance: ProductInstance): ProductInstanceView {
        val optionalProductDefinitionView = productDefinitionViewRepository.findById(productInstance.productIdentifier!!)
        if (!optionalProductDefinitionView.isPresent) {
            throw IllegalArgumentException("Product definition id cannot be null")
        } else {
            return ProductInstanceView(
                id = productInstance.id.toString(),
                productDefinitionView = optionalProductDefinitionView.get(),
                customerIdentifier = productInstance.customerIdentifier,
                accountIdentifier = productInstance.accountIdentifier,
                beneficiaries = productInstance.beneficiaries,
                openedOn = productInstance.openedOn,
                lastTransactionDate = productInstance.lastTransactionDate,
                state = productInstance.state
            )
        }
    }

    fun map(productInstanceView: ProductInstanceView): ProductInstance {
        val productInstance = ProductInstance()
        productInstance.id = UUID.fromString(productInstanceView.id)
        productInstance.productIdentifier = productInstanceView.productDefinitionView?.id.toString()
        productInstance.customerIdentifier = productInstanceView.customerIdentifier
        productInstance.accountIdentifier = productInstanceView.accountIdentifier
        productInstance.beneficiaries = productInstanceView.beneficiaries
        productInstance.openedOn = productInstanceView.openedOn
        productInstance.lastTransactionDate = productInstanceView.lastTransactionDate
        productInstance.state = productInstanceView.state
        return productInstance
    }
}
