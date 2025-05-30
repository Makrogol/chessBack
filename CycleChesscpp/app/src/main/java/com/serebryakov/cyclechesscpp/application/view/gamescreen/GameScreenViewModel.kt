package com.serebryakov.cyclechesscpp.application.view.gamescreen

import com.serebryakov.cyclechesscpp.application.model.data.StartGameData
import com.serebryakov.cyclechesscpp.application.repository.sharedprefrepository.localgamefen.LocalGameFenSharedPrefRepository
import com.serebryakov.cyclechesscpp.application.repository.socketrepository.SocketRepository
import com.serebryakov.cyclechesscpp.foundation.navigator.Navigator
import com.serebryakov.cyclechesscpp.application.view.gamescreen.GameScreenFragment.Screen
import com.serebryakov.cyclechesscpp.foundation.model.PendingResult
import com.serebryakov.cyclechesscpp.foundation.socket.listner.BaseWebSocketListener
import com.serebryakov.cyclechesscpp.foundation.socket.message.SentSocketMessage
import com.serebryakov.cyclechesscpp.foundation.uiActions.UiActions
import com.serebryakov.cyclechesscpp.foundation.views.BaseScreen
import com.serebryakov.cyclechesscpp.foundation.views.LiveResult
import com.serebryakov.cyclechesscpp.foundation.views.MutableLiveResult
import com.serebryakov.cyclechesscpp.foundation.views.WebSocketViewModel

class GameScreenViewModel(
    screen: Screen,
    private val navigator: Navigator,
    private val uiActions: UiActions,
    private val socketRepository: SocketRepository,
    private val localGameFenSharedPrefRepository: LocalGameFenSharedPrefRepository,
    webSocketListener: BaseWebSocketListener,
) : WebSocketViewModel() {

    private val _startGameData = MutableLiveResult<StartGameData>(PendingResult())
    val startGameData: LiveResult<StartGameData> = _startGameData

    private val _socketMessageSend = MutableLiveResult<Unit>(PendingResult())
    var socketMessageSend: LiveResult<Unit> = _socketMessageSend

    private val _socketClosing = MutableLiveResult<Unit>(PendingResult())
    var socketClosing: LiveResult<Unit> = _socketClosing

    private val _setLocalGameFen = MutableLiveResult<Unit>(PendingResult())
    var setLocalGameFen: LiveResult<Unit> = _setLocalGameFen

    private val _setIsPlayWithBot = MutableLiveResult<Unit>(PendingResult())
    var setIsPlayWithBot: LiveResult<Unit> = _setIsPlayWithBot

    init {
        into(_startGameData) {
            screen.startGameData
        }
        webSocketListener.setViewModel(this)
    }

    fun sendSocketMessage(message: String) = into(_socketMessageSend) {
        socketRepository.sendMessage(message)
    }

    fun sendSocketMessage(message: SentSocketMessage) = into(_socketMessageSend) {
        socketRepository.sendMessage(message)
    }

    fun closeSocket() = into(_socketClosing) {
        socketRepository.closeSocket()
    }

    fun setLocalGameFen(fen: String) = into(_setLocalGameFen) {
        localGameFenSharedPrefRepository.setFen(fen)
    }

    fun setIsPlayWithBot(isPlayWithBot: Boolean) = into(_setIsPlayWithBot) {
        localGameFenSharedPrefRepository.setIsPlayingWithBot(isPlayWithBot)
    }

    fun toast(message: String) {
        uiActions.toast(message)
    }

    fun launch(screen: BaseScreen) {
        navigator.launch(screen)
    }
}
