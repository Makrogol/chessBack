package com.serebryakov.cyclechesscpp.application.view.historyscreen

import com.serebryakov.cyclechesscpp.application.model.data.HistoryData
import com.serebryakov.cyclechesscpp.application.repository.backrepository.BackRepository
import com.serebryakov.cyclechesscpp.application.repository.sharedprefrepository.jwttoken.JwtTokenSharedPrefRepository
import com.serebryakov.cyclechesscpp.application.repository.sharedprefrepository.localgamefen.LocalGameFenSharedPrefRepository
import com.serebryakov.cyclechesscpp.application.repository.sharedprefrepository.username.UsernameSharedPrefRepository
import com.serebryakov.cyclechesscpp.application.repository.socketrepository.SocketRepository
import com.serebryakov.cyclechesscpp.foundation.navigator.Navigator
import com.serebryakov.cyclechesscpp.foundation.socket.listner.BaseWebSocketListener
import com.serebryakov.cyclechesscpp.foundation.uiActions.UiActions
import com.serebryakov.cyclechesscpp.foundation.views.BaseScreen
import com.serebryakov.cyclechesscpp.foundation.views.WebSocketViewModel

class HistoryScreenViewModel(
    private val navigator: Navigator,
    private val uiActions: UiActions,
    private val backRepository: BackRepository,
    private val jwtTokenSharedPrefRepository: JwtTokenSharedPrefRepository,
    private val usernameSharedPrefRepository: UsernameSharedPrefRepository,
    private val localGameFenSharedPrefRepository: LocalGameFenSharedPrefRepository,
    private val socketRepository: SocketRepository,
    private val webSocketListener: BaseWebSocketListener,
) : WebSocketViewModel(), HistoryAdapter.Listener {
    fun launch(screen: BaseScreen) {
        navigator.launch(screen)
    }

    fun toast(message: String) {
        uiActions.toast(message)
    }

    override fun onHistoryElementClick(historyData: HistoryData) {

    }
}
