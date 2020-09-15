package org.muellners.finscale.deposit.service

import org.axonframework.eventhandling.EventHandler
import org.axonframework.queryhandling.QueryHandler
import org.muellners.finscale.deposit.event.CreatedActionEvent
import org.muellners.finscale.deposit.query.GetAllActionsQuery
import org.muellners.finscale.deposit.repository.ActionViewRepository
import org.muellners.finscale.deposit.service.mapper.ActionMapper
import org.muellners.finscale.deposit.view.ActionView
import org.springframework.stereotype.Component

@Component
class ActionProjection(
    val actionViewRepository: ActionViewRepository,
    val actionMapper: ActionMapper
) {
    @EventHandler
    fun on(event: CreatedActionEvent): Action {
        val actionView = ActionView(
            id = event.id.toString(),
            identifier = event.identifier,
            name = event.name,
            description = event.description,
            transactionType = event.transactionType
        )
        return actionMapper.map(actionViewRepository.save(actionView))
    }

    @QueryHandler
    fun handle(query: GetAllActionsQuery): MutableList<ActionView> {
        return actionViewRepository.findAll()
    }
}
