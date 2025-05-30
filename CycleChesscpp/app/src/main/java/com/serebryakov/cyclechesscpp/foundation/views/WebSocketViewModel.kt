package com.serebryakov.cyclechesscpp.foundation.views

import com.serebryakov.cyclechesscpp.foundation.model.PendingResult
import com.serebryakov.cyclechesscpp.foundation.socket.message.ReceivedSocketMessage

open class WebSocketViewModel : BaseViewModel() {
    private val _socketStatus = MutableLiveResult<Boolean>(PendingResult())
    val socketStatus: LiveResult<Boolean> = _socketStatus

    private val _socketMessage = MutableLiveResult<ReceivedSocketMessage>(PendingResult())
    val socketMessage: LiveResult<ReceivedSocketMessage> = _socketMessage

    fun setSocketStatus(status: Boolean) = into(_socketStatus) {
        status
    }

    fun setSocketMessage(message: ReceivedSocketMessage) = into(_socketMessage) {
        message
    }
}
