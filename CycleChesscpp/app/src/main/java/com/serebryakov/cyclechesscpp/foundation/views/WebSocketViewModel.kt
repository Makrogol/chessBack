package com.serebryakov.cyclechesscpp.foundation.views

import com.serebryakov.cyclechesscpp.foundation.model.PendingResult

open class WebSocketViewModel : BaseViewModel() {
    private val _socketStatus = MutableLiveResult<Boolean>(PendingResult())
    val socketStatus: LiveResult<Boolean> = _socketStatus

    private val _socketMessage = MutableLiveResult<String>(PendingResult())
    val socketMessage: LiveResult<String> = _socketMessage

    fun setSocketStatus(status: Boolean) = into(_socketStatus) {
        status
    }

    fun setSocketMessage(message: String) = into(_socketMessage) {
        message
    }
}