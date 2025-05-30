package com.serebryakov.cyclechesscpp.application.view.profilescreen

import com.serebryakov.cyclechesscpp.application.model.back.responses.CreateUserResponse
import com.serebryakov.cyclechesscpp.application.model.back.responses.ValidateTokenResponse
import com.serebryakov.cyclechesscpp.application.model.back.responses.ValidateUserResponse
import com.serebryakov.cyclechesscpp.application.model.sharedpref.jwttoken.JwtToken
import com.serebryakov.cyclechesscpp.application.model.data.UserData
import com.serebryakov.cyclechesscpp.application.repository.backrepository.BackRepository
import com.serebryakov.cyclechesscpp.application.repository.sharedprefrepository.jwttoken.JwtTokenSharedPrefRepository
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

class ProfileScreenViewModel(
    private val navigator: Navigator,
    private val uiActions: UiActions,
    private val backRepository: BackRepository,
    private val jwtTokenSharedPrefRepository: JwtTokenSharedPrefRepository,
    private val usernameSharedPrefRepository: UsernameSharedPrefRepository,
    private val socketRepository: SocketRepository,
    private val webSocketListener: BaseWebSocketListener,
) : WebSocketViewModel() {

    private val _createUserResponse = MutableLiveResult<CreateUserResponse>(EmptyResult())
    val createUserResponse: LiveResult<CreateUserResponse> = _createUserResponse

    private val _validateUserResponse = MutableLiveResult<ValidateUserResponse>(EmptyResult())
    val validateUserResponse: LiveResult<ValidateUserResponse> = _validateUserResponse

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

    private val _socketOpening = MutableLiveResult<Unit>(PendingResult())
    var socketOpening: LiveResult<Unit> = _socketOpening

    private val _socketClosing = MutableLiveResult<Unit>(PendingResult())
    var socketClosing: LiveResult<Unit> = _socketClosing

    private val _socketMessageSend = MutableLiveResult<Unit>(PendingResult())
    var socketMessageSend: LiveResult<Unit> = _socketMessageSend

    init {
        getJwtToken()
        // TODO придумать, как перенести это и сам webSocketListener во WebSocketViewModel
        webSocketListener.setViewModel(this)
    }

    fun sendSocketMessage(message: String) = into(_socketMessageSend) {
        socketRepository.sendMessage(message)
    }

    fun sendSocketMessage(message: SentSocketMessage) = into(_socketMessageSend) {
        socketRepository.sendMessage(message)
    }

    fun launch(screen: BaseScreen) {
        navigator.launch(screen)
    }

    fun toast(message: String) {
        uiActions.toast(message)
    }

    fun createUser(userData: UserData) = into(_createUserResponse) {
       backRepository.createUser(userData)
    }

    fun validateUser(userData: UserData) = into(_validateUserResponse) {
        backRepository.validateUser(userData)
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
}
