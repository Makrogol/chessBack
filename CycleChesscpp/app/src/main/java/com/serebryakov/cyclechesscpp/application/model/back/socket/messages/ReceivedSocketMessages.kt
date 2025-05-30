package com.serebryakov.cyclechesscpp.application.model.back.socket.messages

import com.serebryakov.cyclechesscpp.foundation.socket.MessageField
import com.serebryakov.cyclechesscpp.foundation.socket.message.ReceivedSocketMessage

class UserAvailableReceivedMessage: ReceivedSocketMessage {
    val username = MessageField(USERNAME_MESSAGE)
    val userAvailable = MessageField(USER_AVAILABLE_MESSAGE)
}

class NotCompletedGameReceivedMessage: ReceivedSocketMessage {
    val username = MessageField(USERNAME_MESSAGE)
    val opponentUsername = MessageField(OPPONENT_USERNAME_MESSAGE)
    val mainColor = MessageField(MAIN_COLOR_MESSAGE)
    val gameFen = MessageField(GAME_FEN_MESSAGE)
    val isOpponentTurn = MessageField(IS_OPPONENT_TURN_MESSAGE)
    val isSwitchedColor = MessageField(IS_SWITCHED_COLOR_MESSAGE)
    val isPlayingWithBot = MessageField(IS_PLAY_WITH_BOT_MESSAGE)
}

class TurnReceivedMessage: ReceivedSocketMessage {
    val username = MessageField(USERNAME_MESSAGE)
    // TODO мб удалить, на клиенте не нужно
    val opponentUsername = MessageField(OPPONENT_USERNAME_MESSAGE)
    val turn = MessageField(TURN_MESSAGE)
}

class GameStartReceivedMessage: ReceivedSocketMessage {
    val username = MessageField(USERNAME_MESSAGE)
    val opponentUsername = MessageField(OPPONENT_USERNAME_MESSAGE)
    val mainColor = MessageField(MAIN_COLOR_MESSAGE)
    val isSwitchedColor = MessageField(IS_SWITCHED_COLOR_MESSAGE)
}

class GameEndReceivedMessage: ReceivedSocketMessage {
    val username = MessageField(USERNAME_MESSAGE)
    // TODO мб удалить, на клиенте не нужно
    val opponentUsername = MessageField(OPPONENT_USERNAME_MESSAGE)
    val gameEndReason = MessageField(GAME_END_REASON_MESSAGE)
}

class DeclineGameReceivedMessage: ReceivedSocketMessage {
    val username = MessageField(USERNAME_MESSAGE)
    val opponentUsername = MessageField(OPPONENT_USERNAME_MESSAGE)
    val declineReason = MessageField(DECLINE_REASON_MESSAGE)
}
