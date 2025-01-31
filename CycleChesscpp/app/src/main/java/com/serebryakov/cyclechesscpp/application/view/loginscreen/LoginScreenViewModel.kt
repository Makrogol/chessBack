package com.serebryakov.cyclechesscpp.application.view.loginscreen

import com.serebryakov.cyclechesscpp.application.model.back.responses.CreateUserResponse
import com.serebryakov.cyclechesscpp.application.model.back.responses.ValidateUserResponse
import com.serebryakov.cyclechesscpp.application.model.user.UserData
import com.serebryakov.cyclechesscpp.application.repository.backrepository.BackRepository
import com.serebryakov.cyclechesscpp.foundation.model.EmptyResult
import com.serebryakov.cyclechesscpp.foundation.navigator.Navigator
import com.serebryakov.cyclechesscpp.foundation.uiActions.UiActions
import com.serebryakov.cyclechesscpp.foundation.views.BaseScreen
import com.serebryakov.cyclechesscpp.foundation.views.BaseViewModel
import com.serebryakov.cyclechesscpp.foundation.views.LiveResult
import com.serebryakov.cyclechesscpp.foundation.views.MutableLiveResult

class LoginScreenViewModel(
    private val navigator: Navigator,
    private val uiActions: UiActions,
    private val backRepository: BackRepository
) : BaseViewModel() {

    private val _createUserResponse = MutableLiveResult<CreateUserResponse>(EmptyResult())
    val createUserResponse: LiveResult<CreateUserResponse> = _createUserResponse

    private val _validateUserResponse = MutableLiveResult<ValidateUserResponse>(EmptyResult())
    val validateUserResponse: LiveResult<ValidateUserResponse> = _validateUserResponse

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
}