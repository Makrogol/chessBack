package com.serebryakov.cyclechesscpp.application.model.back.socket.listener

import com.serebryakov.cyclechesscpp.application.model.back.socket.messages.DeclineGameReceivedMessage
import com.serebryakov.cyclechesscpp.application.model.back.socket.messages.GameEndReceivedMessage
import com.serebryakov.cyclechesscpp.application.model.back.socket.messages.GameStartReceivedMessage
import com.serebryakov.cyclechesscpp.application.model.back.socket.messages.NotCompletedGameReceivedMessage
import com.serebryakov.cyclechesscpp.application.model.back.socket.messages.TurnReceivedMessage
import com.serebryakov.cyclechesscpp.application.model.back.socket.messages.UserAvailableReceivedMessage
import com.serebryakov.cyclechesscpp.foundation.socket.listner.BaseWebSocketListener
import com.serebryakov.cyclechesscpp.foundation.socket.message.ReceivedSocketMessage

class WebSocketListener : BaseWebSocketListener() {
    override val allReceivedMessages: List<ReceivedSocketMessage> = listOf(
        UserAvailableReceivedMessage(),
        NotCompletedGameReceivedMessage(),
        TurnReceivedMessage(),
        GameStartReceivedMessage(),
        GameEndReceivedMessage(),
        DeclineGameReceivedMessage(),
    )
}
