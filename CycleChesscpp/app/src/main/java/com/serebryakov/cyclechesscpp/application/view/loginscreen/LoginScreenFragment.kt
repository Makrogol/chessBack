package com.serebryakov.cyclechesscpp.application.view.loginscreen

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.serebryakov.cyclechesscpp.application.model.user.OpponentData
import com.serebryakov.cyclechesscpp.application.model.game.GameColor
import com.serebryakov.cyclechesscpp.application.model.user.StartGameData
import com.serebryakov.cyclechesscpp.application.model.user.UserData
import com.serebryakov.cyclechesscpp.application.renderSimpleResult
import com.serebryakov.cyclechesscpp.application.view.findopponentsscreen.FindOpponentsScreenFragment
import com.serebryakov.cyclechesscpp.application.view.gamescreen.GameScreenFragment
import com.serebryakov.cyclechesscpp.databinding.LogInScreenFragmentBinding
import com.serebryakov.cyclechesscpp.foundation.socket.BaseWebSocketListener
import com.serebryakov.cyclechesscpp.foundation.views.BaseFragment
import com.serebryakov.cyclechesscpp.foundation.views.BaseScreen
import com.serebryakov.cyclechesscpp.foundation.views.screenViewModel

class LoginScreenFragment : BaseFragment() {

    class Screen : BaseScreen

    private lateinit var binding: LogInScreenFragmentBinding
    override val viewModel by screenViewModel<LoginScreenViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LogInScreenFragmentBinding.inflate(inflater, container, false)

        with(binding) {
            usernameEdittext.setText("abcd")
            passwordEdittext.setText("123456")

            registerButton.setOnClickListener {
                if (checkAll()) {
                    binding.loginPasswordInfoTextview.text = ""
                    viewModel.createUser(getUserDataFromEditText())
                }
            }

            entryButton.setOnClickListener {
                if (checkAll()) {
                    binding.loginPasswordInfoTextview.text = ""
                    viewModel.validateUser(getUserDataFromEditText())
                }
            }

            oneDeskButton.setOnClickListener {
                viewModel.launch(
                    GameScreenFragment.Screen(
                        StartGameData(
                            username = "",
                            opponentUsername = "",
                            color = GameColor.black,
                            webSocketListener = BaseWebSocketListener(),
                            useSocket = false
                        )
                    )
                )
            }
        }

        viewModel.createUserResponse.observe(viewLifecycleOwner) { result ->
            renderSimpleResult(
                root = binding.root,
                result = result,
                onError = {
                    viewModel.toast("Ошибка при создании пользователя")
                },
                onSuccess = { response ->
                    if (response.success) {
                        viewModel.toast("Регистрация прошла успешно")
                        viewModel.launch(FindOpponentsScreenFragment.Screen(binding.usernameEdittext.text.toString()))
                    } else {
                        viewModel.toast("Серверная ошибка при регистрации\nВозможно ваш логин уже используется")
                    }
                }
            )
        }

        viewModel.validateUserResponse.observe(viewLifecycleOwner) { result ->
            renderSimpleResult(
                root = binding.root,
                result = result,
                onError = {
                    viewModel.toast("Ошибка при валидации пользователя")
                },
                onSuccess = { response ->
                    if (response.validate_result) {
                        viewModel.toast("Авторизация прошла успешно")
                        viewModel.launch(FindOpponentsScreenFragment.Screen(binding.usernameEdittext.text.toString()))
                    } else {
                        viewModel.toast("Серверная ошибка при авторизации\nВозможно ваш логин или пароль неверные")
                    }
                }
            )
        }

        return binding.root
    }

    private fun getUserDataFromEditText(): UserData = UserData(
        username = binding.usernameEdittext.text.toString(),
        password = binding.passwordEdittext.text.toString(),
    )

    private fun checkAll(): Boolean {
        return checkPassword() && checkLogin()
    }

    private fun checkLogin(): Boolean {
        with(binding.usernameEdittext.text) {
            if (isEmpty()) {
                binding.loginPasswordInfoTextview.text = "Логин не может быть пустым"
                return false
            }
            if (length >= 20) {
                binding.loginPasswordInfoTextview.text = "Логин не может быть\nбольше 20 символов"
                return false
            }
            return true
        }
    }

    private fun checkPassword(): Boolean {
        with(binding.passwordEdittext.text) {
            if (isEmpty()) {
                binding.loginPasswordInfoTextview.text = "Пароль не может быть пустым"
                return false
            }
            if (length < 6) {
                binding.loginPasswordInfoTextview.text = "Пароль не может быть\nменьше 6 символов"
                return false
            }
            return true
        }
    }
}