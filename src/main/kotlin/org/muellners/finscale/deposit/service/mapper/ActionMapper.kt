package org.muellners.finscale.deposit.service.mapper

import java.util.*
import org.muellners.finscale.deposit.service.Action
import org.muellners.finscale.deposit.view.ActionView
import org.springframework.stereotype.Service

@Service
class ActionMapper() {
    fun map(action: Action): ActionView {
        val actionView = ActionView(
            id = action.id.toString(),
            identifier = action.identifier,
            name = action.name,
            description = action.description,
            transactionType = action.transactionType
        )
        return actionView
    }

    fun map(actionView: ActionView): Action {
        val action = Action()
        action.id = UUID.fromString(actionView.id)
        action.identifier = actionView.identifier
        action.name = actionView.name
        action.description = actionView.description
        action.transactionType = actionView.transactionType
        return action
    }
}
