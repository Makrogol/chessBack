package com.serebryakov.cyclechesscpp.application.view.gamescreen

import com.serebryakov.cyclechesscpp.application.model.back.socket.messages.Dict
import com.serebryakov.cyclechesscpp.application.model.back.socket.messages.SocketMessage
import com.serebryakov.cyclechesscpp.application.model.game.GameColor
import com.serebryakov.cyclechesscpp.application.model.game.GameState
import com.serebryakov.cyclechesscpp.application.model.game.MoveType
import com.serebryakov.cyclechesscpp.application.model.game.PieceType
import com.serebryakov.cyclechesscpp.application.model.game.Position
import com.serebryakov.cyclechesscpp.application.model.game.Route
import com.serebryakov.cyclechesscpp.application.model.user.StartGameData
import com.serebryakov.cyclechesscpp.application.repository.cppconnectionrepository.CppConnectionRepository
import com.serebryakov.cyclechesscpp.application.repository.socketrepository.SocketRepository
import com.serebryakov.cyclechesscpp.foundation.navigator.Navigator
import com.serebryakov.cyclechesscpp.application.view.gamescreen.GameScreenFragment.Screen
import com.serebryakov.cyclechesscpp.foundation.model.PendingResult
import com.serebryakov.cyclechesscpp.foundation.uiActions.UiActions
import com.serebryakov.cyclechesscpp.foundation.views.BaseScreen
import com.serebryakov.cyclechesscpp.foundation.views.LiveResult
import com.serebryakov.cyclechesscpp.foundation.views.MutableLiveResult
import com.serebryakov.cyclechesscpp.foundation.views.WebSocketViewModel
import java.net.Socket

class GameScreenViewModel(
    screen: Screen,
    private val navigator: Navigator,
    private val uiActions: UiActions,
    private val socketRepository: SocketRepository,
) : WebSocketViewModel() {

    private val _startGameData = MutableLiveResult<StartGameData>(PendingResult())
    val startGameData: LiveResult<StartGameData> = _startGameData

    private val _socketMessageSend = MutableLiveResult<Unit>(PendingResult())
    var socketMessageSend: LiveResult<Unit> = _socketMessageSend

    init {
        into(_startGameData) {
            screen.startGameData
        }
    }

    fun sendSocketMessage(message: String) = into(_socketMessageSend) {
        socketRepository.sendMessage(message)
    }

    fun sendSocketMessage(message: SocketMessage) = into(_socketMessageSend) {
        socketRepository.sendMessage(message)
    }

    fun toast(message: String) {
        uiActions.toast(message)
    }

    fun launch(screen: BaseScreen) {
        navigator.launch(screen)
    }
}