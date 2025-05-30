package com.serebryakov.cyclechesscpp.application.view.findopponentsscreen

import com.serebryakov.cyclechesscpp.application.model.data.OpponentData
import com.serebryakov.cyclechesscpp.application.view.findopponentsscreen.FindOpponentsScreenFragment.Screen
import com.serebryakov.cyclechesscpp.application.model.back.responses.UserResponse
import com.serebryakov.cyclechesscpp.application.repository.backrepository.BackRepository
import com.serebryakov.cyclechesscpp.application.repository.sharedprefrepository.username.UsernameSharedPrefRepository
import com.serebryakov.cyclechesscpp.application.repository.socketrepository.SocketRepository
import com.serebryakov.cyclechesscpp.foundation.model.EmptyResult
import com.serebryakov.cyclechesscpp.foundation.model.PendingResult
import com.serebryakov.cyclechesscpp.foundation.navigator.Navigator
import com.serebryakov.cyclechesscpp.foundation.socket.listner.BaseWebSocketListener
import com.serebryakov.cyclechesscpp.foundation.socket.message.SentSocketMessage
import com.serebryakov.cyclechesscpp.foundation.uiActions.UiActions
import com.serebryakov.cyclechesscpp.foundation.views.BaseScreen
import com.serebryakov.cyclechesscpp.foundation.views.LiveResult
import com.serebryakov.cyclechesscpp.foundation.views.MutableLiveResult
import com.serebryakov.cyclechesscpp.foundation.views.WebSocketViewModel

class FindOpponentsScreenViewModel(
    screen: Screen,
    private val navigator: Navigator,
    private val uiActions: UiActions,
    private val backRepository: BackRepository,
    private val socketRepository: SocketRepository,
    private val usernameSharedPrefRepository: UsernameSharedPrefRepository,
    webSocketListener: BaseWebSocketListener,
) : WebSocketViewModel() {
    private val _opponentsData = MutableLiveResult<List<UserResponse>>(PendingResult())
    var opponentsData: LiveResult<List<UserResponse>> = _opponentsData

    private val _params = MutableLiveResult<FindOpponentScreenParams>(PendingResult())
    var params: LiveResult<FindOpponentScreenParams> = _params

    private val _socketMessageSend = MutableLiveResult<Unit>(PendingResult())
    var socketMessageSend: LiveResult<Unit> = _socketMessageSend

    private val _socketClosing = MutableLiveResult<Unit>(PendingResult())
    var socketClosing: LiveResult<Unit> = _socketClosing

        private val _gameStarted = MutableLiveResult<OpponentData>(PendingResult())
    var gameStarted: LiveResult<OpponentData> = _gameStarted

    private val _getUsername = MutableLiveResult<String>(EmptyResult())
    val getUsername: LiveResult<String> = _getUsername

    init {
        into(_params) {
            screen.params
        }
        getUsername()
        webSocketListener.setViewModel(this)
    }

    fun getAllOpponentsData() = into(_opponentsData) {
        backRepository.getUsers()
    }

    fun sendSocketMessage(message: SentSocketMessage) = into(_socketMessageSend) {
        socketRepository.sendMessage(message)
    }

    fun closeSocket() = into(_socketClosing) {
        socketRepository.closeSocket()
    }

    fun getUsername() = into(_getUsername) {
        usernameSharedPrefRepository.getUsername()
    }

    fun toast(message: String) {
        uiActions.toast(message)
    }

    fun launch(screen: BaseScreen) {
        navigator.launch(screen)
    }
}
