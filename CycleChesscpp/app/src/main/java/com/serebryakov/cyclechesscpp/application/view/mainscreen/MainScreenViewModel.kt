package com.serebryakov.cyclechesscpp.application.view.mainscreen

import com.serebryakov.cyclechesscpp.application.model.back.responses.ValidateTokenResponse
import com.serebryakov.cyclechesscpp.application.model.sharedpref.jwttoken.JwtToken
import com.serebryakov.cyclechesscpp.application.repository.backrepository.BackRepository
import com.serebryakov.cyclechesscpp.application.repository.sharedprefrepository.jwttoken.JwtTokenSharedPrefRepository
import com.serebryakov.cyclechesscpp.application.repository.sharedprefrepository.localgamefen.LocalGameFenSharedPrefRepository
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

class MainScreenViewModel(
    private val navigator: Navigator,
    private val uiActions: UiActions,
    private val backRepository: BackRepository,
    private val jwtTokenSharedPrefRepository: JwtTokenSharedPrefRepository,
    private val usernameSharedPrefRepository: UsernameSharedPrefRepository,
    private val localGameFenSharedPrefRepository: LocalGameFenSharedPrefRepository,
    private val socketRepository: SocketRepository,
    private val webSocketListener: BaseWebSocketListener,
) : WebSocketViewModel() {

    private val _validateTokenResponse = MutableLiveResult<ValidateTokenResponse>(EmptyResult())
    val validateTokenResponse: LiveResult<ValidateTokenResponse> = _validateTokenResponse

    private val _getJwtToken = MutableLiveResult<JwtToken>(EmptyResult())
    val getJwtToken: LiveResult<JwtToken> = _getJwtToken

    private val _setJwtToken = MutableLiveResult<Unit>(EmptyResult())
    val setJwtToken: LiveResult<Unit> = _setJwtToken

    private val _setUsername = MutableLiveResult<Unit>(EmptyResult())
    val setUsername: LiveResult<Unit> = _setUsername

    private val _getUsername = MutableLiveResult<String>(EmptyResult())
    val getUsername: LiveResult<String> = _getUsername

    private val _socketOpening = MutableLiveResult<Unit>(EmptyResult())
    var socketOpening: LiveResult<Unit> = _socketOpening

    private val _socketClosing = MutableLiveResult<Unit>(EmptyResult())
    var socketClosing: LiveResult<Unit> = _socketClosing

    private val _getLocalGameFen = MutableLiveResult<String>(EmptyResult())
    var getLocalGameFen: LiveResult<String> = _getLocalGameFen

    private val _setLocalGameFen = MutableLiveResult<Unit>(PendingResult())
    var setLocalGameFen: LiveResult<Unit> = _setLocalGameFen

    private val _socketMessageSend = MutableLiveResult<Unit>(PendingResult())
    var socketMessageSend: LiveResult<Unit> = _socketMessageSend

    init {
        start()
    }

    fun start() {
        getJwtToken()
        webSocketListener.setViewModel(this)
        getLocalGameFen()
    }

    fun sendSocketMessage(message: String) = into(_socketMessageSend) {
        socketRepository.sendMessage(message)
    }

    fun sendSocketMessage(message: SentSocketMessage) = into(_socketMessageSend) {
        socketRepository.sendMessage(message)
    }

    fun validateToken(token: JwtToken) = into(_validateTokenResponse) {
        backRepository.validateToken(token)
    }

    private fun getJwtToken() = into(_getJwtToken) {
        jwtTokenSharedPrefRepository.getJwtToken()
    }

    fun setJwtToken(token: JwtToken) = into(_setJwtToken) {
        jwtTokenSharedPrefRepository.setJwtToken(token)
    }

    fun setUsername(username: String) = into(_setUsername) {
        usernameSharedPrefRepository.setUsername(username)
    }

    fun getUsername() = into(_getUsername) {
        usernameSharedPrefRepository.getUsername()
    }

    fun openSocket(username: String) = into(_socketOpening) {
        socketRepository.openSocket(username)
    }

    fun closeSocket() = into(_socketClosing) {
        socketRepository.closeSocket()
    }

    fun isSocketExist() = socketRepository.isSocketExist()

    fun getLocalGameFen() = into(_getLocalGameFen) {
        localGameFenSharedPrefRepository.getFen()
    }

    fun setLocalGameFen(fen: String) = into(_setLocalGameFen) {
        localGameFenSharedPrefRepository.setFen(fen)
    }

    fun launch(screen: BaseScreen) {
        navigator.launch(screen)
    }

    fun toast(message: String) {
        uiActions.toast(message)
    }
}
