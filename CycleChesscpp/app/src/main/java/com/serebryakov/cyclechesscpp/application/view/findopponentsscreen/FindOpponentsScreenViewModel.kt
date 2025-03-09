package com.serebryakov.cyclechesscpp.application.view.findopponentsscreen

import com.serebryakov.cyclechesscpp.application.model.user.OpponentData
import com.serebryakov.cyclechesscpp.application.view.findopponentsscreen.FindOpponentsScreenFragment.Screen
import com.serebryakov.cyclechesscpp.application.model.back.responses.UserResponse
import com.serebryakov.cyclechesscpp.application.model.back.socket.messages.StartGameSocketMessage
import com.serebryakov.cyclechesscpp.application.repository.backrepository.BackRepository
import com.serebryakov.cyclechesscpp.application.repository.sharedprefrepository.username.UsernameSharedPrefRepository
import com.serebryakov.cyclechesscpp.application.repository.socketrepository.SocketRepository
import com.serebryakov.cyclechesscpp.foundation.model.EmptyResult
import com.serebryakov.cyclechesscpp.foundation.model.PendingResult
import com.serebryakov.cyclechesscpp.foundation.model.takeSuccess
import com.serebryakov.cyclechesscpp.foundation.navigator.Navigator
import com.serebryakov.cyclechesscpp.foundation.socket.BaseWebSocketListener
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
    private val usernameSharedPrefRepository: UsernameSharedPrefRepository
) : WebSocketViewModel(), OpponentsAdapter.Listener {
    private var username: String = ""

    private val _opponentsData = MutableLiveResult<List<UserResponse>>(PendingResult())
    var opponentsData: LiveResult<List<UserResponse>> = _opponentsData

    private val _params = MutableLiveResult<FindOpponentScreenParams>(PendingResult())
    var params: LiveResult<FindOpponentScreenParams> = _params

    private val _socketOpening = MutableLiveResult<Unit>(PendingResult())
    var socketOpening: LiveResult<Unit> = _socketOpening

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
        getAllOpponentsData()
    }

    private fun getAllOpponentsData() = into(_opponentsData) {
        backRepository.getUsers()
    }

    fun toast(message: String) {
        uiActions.toast(message)
    }

    fun launch(screen: BaseScreen) {
        navigator.launch(screen)
    }

    fun openSocket(webSocketListener: BaseWebSocketListener, username: String) = into(_socketOpening) {
        socketRepository.openSocket(webSocketListener, username)
    }

    fun closeSocket() = into(_socketClosing) {
        socketRepository.closeSocket()
    }

    private fun getUsername() = into(_getUsername) {
        username = usernameSharedPrefRepository.getUsername()
        username
    }

    override fun onOpponentClick(opponentData: OpponentData) = into(_gameStarted) {
        val startGameSocketMessage = StartGameSocketMessage()
        startGameSocketMessage.username = username
        startGameSocketMessage.opponentUsername = opponentData.username
        socketRepository.sendMessage(startGameSocketMessage)
        opponentData
    }
}