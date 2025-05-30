package com.serebryakov.cyclechesscpp.application.view.profilescreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.serebryakov.cyclechesscpp.application.model.back.responses.TokenResponse
import com.serebryakov.cyclechesscpp.application.model.back.socket.messages.DeclineGameSentMessage
import com.serebryakov.cyclechesscpp.application.model.back.socket.messages.GameEndSentMessage
import com.serebryakov.cyclechesscpp.application.model.back.socket.messages.GameStartReceivedMessage
import com.serebryakov.cyclechesscpp.application.model.back.socket.messages.NotCompletedGameReceivedMessage
import com.serebryakov.cyclechesscpp.foundation.socket.utils.SocketMessageUtils
import com.serebryakov.cyclechesscpp.foundation.socket.utils.SocketMessageUtilsImpl
import com.serebryakov.cyclechesscpp.application.model.cppapi.utils.Unparser
import com.serebryakov.cyclechesscpp.application.model.sharedpref.jwttoken.JwtToken
import com.serebryakov.cyclechesscpp.application.model.data.StartGameData
import com.serebryakov.cyclechesscpp.application.model.game.getAnotherColor
import com.serebryakov.cyclechesscpp.application.renderSimpleResult
import com.serebryakov.cyclechesscpp.application.view.containers.acceptdeclinegame.AcceptDeclineGameContainer
import com.serebryakov.cyclechesscpp.application.view.gamescreen.GameScreenFragment
import com.serebryakov.cyclechesscpp.application.view.profilescreen.utils.ProfileScreenUtils
import com.serebryakov.cyclechesscpp.application.view.profilescreen.utils.ProfileScreenUtilsImpl
import com.serebryakov.cyclechesscpp.application.view.profilescreen.viewutils.ProfileScreenViewUtils
import com.serebryakov.cyclechesscpp.application.view.profilescreen.viewutils.ProfileScreenViewUtilsImpl
import com.serebryakov.cyclechesscpp.databinding.ProfileScreenFragmentBinding
import com.serebryakov.cyclechesscpp.foundation.views.BaseFragment
import com.serebryakov.cyclechesscpp.foundation.views.BaseScreen
import com.serebryakov.cyclechesscpp.foundation.views.screenViewModel

class ProfileScreenFragment : BaseFragment() {

    class Screen : BaseScreen

    private lateinit var binding: ProfileScreenFragmentBinding
    override val viewModel by screenViewModel<ProfileScreenViewModel>()

    private val utils: ProfileScreenUtils = ProfileScreenUtilsImpl()
    private lateinit var viewUtils: ProfileScreenViewUtils

    private var username = ""

    private val unparser = Unparser()
    private val socketMessageUtils: SocketMessageUtils = SocketMessageUtilsImpl()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ProfileScreenFragmentBinding.inflate(inflater, container, false)
        val acceptDeclineGameContainer = AcceptDeclineGameContainer(binding.root)
        viewUtils = ProfileScreenViewUtilsImpl(binding)

        with(binding) {
            registerButton.setOnClickListener {
                val result = utils.checkAll(usernameEdittext.text, passwordEdittext.text)
                viewUtils.setLoginPasswordInfo(result.message)
                if (result.result) {
                    viewModel.createUser(viewUtils.getUserDataFromEditText())
                }
            }

            entryButton.setOnClickListener {
                val result = utils.checkAll(usernameEdittext.text, passwordEdittext.text)
                viewUtils.setLoginPasswordInfo(result.message)
                if (result.result) {
                    viewModel.validateUser(viewUtils.getUserDataFromEditText())
                }
            }

            exitButton.setOnClickListener {
                viewModel.setJwtToken(
                    JwtToken(
                        token = ""
                    )
                )
                viewModel.setUsername("")
                username = ""
                viewUtils.loadRegisterAuthorizationUI()
            }
        }

        viewModel.getJwtToken.observe(viewLifecycleOwner) { result ->
            renderSimpleResult(
                root = binding.root,
                result = result,
                onError = {
                    viewModel.toast("Ошибка при получении токена из хранилища")
                },
                onSuccess = { token ->
                    viewModel.toast("Токен успешно получен из sharedPref")
                    viewModel.validateToken(token)
                }
            )
        }

        viewModel.getUsername.observe(viewLifecycleOwner) { result ->
            renderSimpleResult(
                root = binding.root,
                result = result,
                onError = {
                    viewModel.toast("Ошибка при получении логина из хранилища")
                },
                onSuccess = { username_ ->
                    viewModel.toast("Логин успешно получен из sharedPref")
                    username = username_
                    viewUtils.setUsername(username)
                }
            )
        }

        viewModel.validateTokenResponse.observe(viewLifecycleOwner) { result ->
            renderSimpleResult(
                root = binding.root,
                result = result,
                onError = {
                    viewModel.toast("Ошибка при валидации токена сервером")
                },
                onSuccess = { response ->
                    if (response.success) {
                        viewModel.toast("Токен провалидирован как верный")
                        viewUtils.loadProfileUI()
                        username = response.username
                        viewUtils.setUsername(username)
                        viewModel.setUsername(username)

                        if (!viewModel.isSocketExist()) {
                            viewModel.openSocket(username)
                        }
                    } else {
                        viewModel.toast("Токен провалидирован как ошибочный")
                        viewUtils.loadRegisterAuthorizationUI()
                        username = ""
                        if (viewModel.isSocketExist()) {
                            viewModel.closeSocket()
                        }
                    }
                }
            )
        }

        viewModel.setJwtToken.observe(viewLifecycleOwner) { result ->
            renderSimpleResult(
                root = binding.root,
                result = result,
                onError = {
                    viewModel.toast("Ошибка при загрузке токена в хранилище")
                },
                onSuccess = {
                    viewModel.toast("Токен успешно загружен в хранилище")
                }
            )
        }

        viewModel.setUsername.observe(viewLifecycleOwner) { result ->
            renderSimpleResult(
                root = binding.root,
                result = result,
                onError = {
                    viewModel.toast("Ошибка при загрузке логина в хранилище")
                },
                onSuccess = {
                    viewModel.toast("Логин успешно загружен в хранилище")
                }
            )
        }

        viewModel.createUserResponse.observe(viewLifecycleOwner) { result ->
            renderSimpleResult(
                root = binding.root,
                result = result,
                onError = {
                    viewModel.toast("Ошибка при создании пользователя")
                },
                onSuccess = { response ->
                    onTokenResponse(
                        response.toTokenResponse(),
                        "Регистрация прошла успешно",
                        "Серверная ошибка при регистрации\nВозможно ваш логин уже используется"
                    )
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
                    onTokenResponse(
                        response.toTokenResponse(),
                        "Авторизация прошла успешно",
                        "Серверная ошибка при авторизации\nВозможно ваш логин или пароль неверные"
                    )
                }
            )
        }

        viewModel.socketMessage.observe(viewLifecycleOwner) { result ->
            renderSimpleResult(
                root = binding.root,
                result = result,
                onError = {
                    viewModel.toast("Ошибка при получении сообщения через сокет")
                },
                onSuccess = { message ->
                    println(
                        "MainScreenFragment socket message = ${
                            socketMessageUtils.toString(
                                message
                            )
                        }"
                    )
                    if (message is NotCompletedGameReceivedMessage && message.username.value == username) {
                        println("profileScreenFragment fen ${message.gameFen.value}")
                        with(acceptDeclineGameContainer) {
                            showWithMessage("Хотите продолжить онлайн игру\nс ${message.opponentUsername}?")
                            setOnAcceptGame {
                                viewModel.launch(
                                    GameScreenFragment.Screen(
                                        StartGameData(
                                            username = message.username.value!!,
                                            opponentUsername = message.opponentUsername.value!!,
                                            mainColor = unparser.getColor(message.mainColor.value!!)
                                                .getAnotherColor(),
                                            useSocket = true,
                                            fen = message.gameFen.value!!,
                                            isOpponentTurn = message.isOpponentTurn.value!!.toBoolean(),
                                            isSwitchedColor = message.isSwitchedColor.value!!.toBoolean(),
                                            isPlayWithBot = false,
                                        )
                                    )
                                )
                            }
                            setOnDeclineGame {
                                val endGameMessage = GameEndSentMessage()
                                endGameMessage.username.value = message.username.value
                                endGameMessage.opponentUsername.value =
                                    message.opponentUsername.value
                                endGameMessage.gameEndReason.value = "Противник сдался"
                                viewModel.sendSocketMessage(endGameMessage)
                            }
                        }
                        return@renderSimpleResult
                    }

                    if (message is GameStartReceivedMessage && message.username.value == username) {
                        with(acceptDeclineGameContainer) {
                            showWithMessage("Хотите начать онлайн игру\nс ${message.opponentUsername.value}?")
                            setOnAcceptGame {
                                viewModel.launch(
                                    GameScreenFragment.Screen(
                                        StartGameData(
                                            username = message.username.value!!,
                                            opponentUsername = message.opponentUsername.value!!,
                                            mainColor = unparser.getColor(message.mainColor.value!!)
                                                .getAnotherColor(),
                                            useSocket = true,
                                            fen = null,
                                            isOpponentTurn = false,
                                            isSwitchedColor = message.isSwitchedColor.value!!.toBoolean(),
                                            isPlayWithBot = false,
                                        )
                                    )
                                )
                            }
                            setOnDeclineGame {
                                val declineGameMessage = DeclineGameSentMessage()
                                declineGameMessage.username.value = message.username.value
                                declineGameMessage.opponentUsername.value =
                                    message.opponentUsername.value
                                declineGameMessage.declineReason.value =
                                    "Противник отказался начать игру"
                            }
                        }
                        return@renderSimpleResult
                    }
                }
            )
        }

        viewModel.socketClosing.observe(viewLifecycleOwner) { result ->
            renderSimpleResult(
                root = binding.root,
                result = result,
                onError = {
                    viewModel.toast("Ошибка при закрытии сокета")
                },
                onSuccess = {
                    viewModel.toast("Соединение с сокетом закрыто")
                }
            )
        }

        viewModel.socketOpening.observe(viewLifecycleOwner) { result ->
            renderSimpleResult(
                root = binding.root,
                result = result,
                onError = {
                    viewModel.toast("Ошибка при соединении сокета")
                },
                onSuccess = {
                    viewModel.toast("Успешное соединение с сокетом")
                }
            )
        }

        return binding.root
    }

    private fun onTokenResponse(
        response: TokenResponse, successMessage: String, unsuccessMessage: String
    ) {
        if (response.success) {
            viewModel.toast(successMessage)
            viewModel.setJwtToken(
                JwtToken(
                    token = response.token
                )
            )
            username = response.username
            viewModel.setUsername(username)
            viewUtils.loadProfileUI()
            viewUtils.setUsername(username)

            if (!viewModel.isSocketExist()) {
                viewModel.openSocket(username)
            }
        } else {
            viewModel.toast(unsuccessMessage)
            username = ""
            if (viewModel.isSocketExist()) {
                viewModel.closeSocket()
            }
        }
    }
}
