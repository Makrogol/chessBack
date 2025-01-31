package com.serebryakov.cyclechesscpp.application.view.findopponentsscreen

import com.serebryakov.cyclechesscpp.application.model.user.OpponentData
import com.serebryakov.cyclechesscpp.application.view.findopponentsscreen.FindOpponentsScreenFragment.Screen
import com.serebryakov.cyclechesscpp.application.model.back.responses.UserResponse
import com.serebryakov.cyclechesscpp.application.model.back.socket.messages.StartGameSocketMessage
import com.serebryakov.cyclechesscpp.application.repository.backrepository.BackRepository
import com.serebryakov.cyclechesscpp.application.repository.socketrepository.SocketRepository
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
    private val socketRepository: SocketRepository
) : WebSocketViewModel(), OpponentsAdapter.Listener {

    private val _opponentsData = MutableLiveResult<List<UserResponse>>(PendingResult())
    var opponentsData: LiveResult<List<UserResponse>> = _opponentsData

    private val _username = MutableLiveResult<String>(PendingResult())
    var username: LiveResult<String> = _username

    private val _socketConnection = MutableLiveResult<Unit>(PendingResult())
    var socketConnection: LiveResult<Unit> = _socketConnection

    private val _gameStarted = MutableLiveResult<OpponentData>(PendingResult())
    var gameStarted: LiveResult<OpponentData> = _gameStarted

    init {
        into(_username) {
            screen.username
        }
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

    fun createSocket(webSocketListener: BaseWebSocketListener, username: String) = into(_socketConnection) {
        socketRepository.createSocket(webSocketListener, username)
    }

    override fun onOpponentClick(opponentData: OpponentData) = into(_gameStarted) {
        val startGameSocketMessage = StartGameSocketMessage()
        startGameSocketMessage.username = username.value.takeSuccess()
        startGameSocketMessage.opponentUsername = opponentData.username
        socketRepository.sendMessage(startGameSocketMessage)
        opponentData
    }
}