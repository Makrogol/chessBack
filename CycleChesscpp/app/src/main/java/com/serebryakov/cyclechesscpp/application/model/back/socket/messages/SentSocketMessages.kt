package com.serebryakov.cyclechesscpp.application.model.back.socket.messages

import com.serebryakov.cyclechesscpp.foundation.socket.MessageField
import com.serebryakov.cyclechesscpp.foundation.socket.message.SentSocketMessage

class GameEndSentMessage: SentSocketMessage {
    val username = MessageField(USERNAME_MESSAGE)
    val opponentUsername = MessageField(OPPONENT_USERNAME_MESSAGE)
    val gameEndReason = MessageField(GAME_END_REASON_MESSAGE)
}

class GameStartSentMessage: SentSocketMessage {
    val username = MessageField(USERNAME_MESSAGE)
    val opponentUsername = MessageField(OPPONENT_USERNAME_MESSAGE)
    val mainColor = MessageField(MAIN_COLOR_MESSAGE)
    val isSwitchedColor = MessageField(IS_SWITCHED_COLOR_MESSAGE)
    val isPlayWithBot = MessageField(IS_PLAY_WITH_BOT_MESSAGE)
}

class TurnSentMessage: SentSocketMessage {
    val username = MessageField(USERNAME_MESSAGE)
    val opponentUsername = MessageField(OPPONENT_USERNAME_MESSAGE)
    val turn = MessageField(TURN_MESSAGE)
    val gameFen = MessageField(GAME_FEN_MESSAGE)
}

class DeclineGameSentMessage: SentSocketMessage {
    val username = MessageField(USERNAME_MESSAGE)
    val opponentUsername = MessageField(OPPONENT_USERNAME_MESSAGE)
    val declineReason = MessageField(DECLINE_REASON_MESSAGE)
}
