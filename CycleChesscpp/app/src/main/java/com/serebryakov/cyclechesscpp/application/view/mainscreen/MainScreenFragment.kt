package com.serebryakov.cyclechesscpp.application.view.mainscreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.serebryakov.cyclechesscpp.application.model.back.socket.messages.DeclineGameSentMessage
import com.serebryakov.cyclechesscpp.application.model.back.socket.messages.GameEndSentMessage
import com.serebryakov.cyclechesscpp.application.model.back.socket.messages.GameStartReceivedMessage
import com.serebryakov.cyclechesscpp.application.model.back.socket.messages.GameStartSentMessage
import com.serebryakov.cyclechesscpp.application.model.back.socket.messages.NotCompletedGameReceivedMessage
import com.serebryakov.cyclechesscpp.application.model.cppapi.utils.Parser
import com.serebryakov.cyclechesscpp.foundation.socket.utils.SocketMessageUtils
import com.serebryakov.cyclechesscpp.foundation.socket.utils.SocketMessageUtilsImpl
import com.serebryakov.cyclechesscpp.application.model.cppapi.utils.Unparser
import com.serebryakov.cyclechesscpp.application.model.game.GameColor
import com.serebryakov.cyclechesscpp.application.model.data.StartGameData
import com.serebryakov.cyclechesscpp.application.model.game.getAnotherColor
import com.serebryakov.cyclechesscpp.application.renderSimpleResult
import com.serebryakov.cyclechesscpp.application.view.containers.acceptdeclinegame.AcceptDeclineGameContainer
import com.serebryakov.cyclechesscpp.application.view.containers.choosestartgamecolor.StartGameContainer
import com.serebryakov.cyclechesscpp.application.view.findopponentsscreen.FindOpponentScreenParams
import com.serebryakov.cyclechesscpp.application.view.findopponentsscreen.FindOpponentsScreenFragment
import com.serebryakov.cyclechesscpp.application.view.gamescreen.GameScreenFragment
import com.serebryakov.cyclechesscpp.application.view.historyscreen.HistoryScreenFragment
import com.serebryakov.cyclechesscpp.application.view.profilescreen.ProfileScreenFragment
import com.serebryakov.cyclechesscpp.databinding.MainScreenFragmentBinding
import com.serebryakov.cyclechesscpp.foundation.views.BaseFragment
import com.serebryakov.cyclechesscpp.foundation.views.BaseScreen
import com.serebryakov.cyclechesscpp.foundation.views.screenViewModel

class MainScreenFragment : BaseFragment() {

    class Screen : BaseScreen

    private lateinit var binding: MainScreenFragmentBinding
    override val viewModel by screenViewModel<MainScreenViewModel>()

    private val unparser = Unparser()
    private val socketMessageUtils: SocketMessageUtils = SocketMessageUtilsImpl()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MainScreenFragmentBinding.inflate(inflater, container, false)
        val chooseStartGameColorContainer = StartGameContainer(binding.root)
        // TODO как будто можно удалить
        var username = ""

        with(binding) {
            oneDeskButton.setOnClickListener {
                viewModel.launch(
                    GameScreenFragment.Screen(
                        StartGameData(
                            username = "Аноним",
                            opponentUsername = "Аноним",
                            mainColor = GameColor.white,
                            useSocket = false,
                            fen = null,
                            isOpponentTurn = false,
                            isSwitchedColor = false,
                            isPlayWithBot = false,
                        )
                    )
                )
            }

            profileButton.setOnClickListener {
                viewModel.launch(ProfileScreenFragment.Screen())
            }

            historyButton.setOnClickListener {
                viewModel.launch(HistoryScreenFragment.Screen())
            }

            playWithBotButton.setOnClickListener {
                with(chooseStartGameColorContainer) {
                    show()
                    setOnColorChoose { mainColor, isSwitchedColor ->
                        val startGameSocketMessage = GameStartSentMessage()
                        startGameSocketMessage.username.value = username
                        startGameSocketMessage.opponentUsername.value = "Бот"
                        startGameSocketMessage.mainColor.value = Parser().color(mainColor)
                        startGameSocketMessage.isSwitchedColor.value = isSwitchedColor.toString()
                        startGameSocketMessage.isPlayWithBot.value = true.toString()
                        viewModel.sendSocketMessage(startGameSocketMessage)

                        viewModel.launch(
                            GameScreenFragment.Screen(
                                StartGameData(
                                    username = username,
                                    opponentUsername = "Бот",
                                    mainColor = mainColor,
                                    useSocket = true,
                                    fen = null,
                                    isOpponentTurn = false,
                                    isSwitchedColor = isSwitchedColor,
                                    isPlayWithBot = true,
                                )
                            )
                        )
                    }
                }
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
                onSuccess = { _username ->
                    viewModel.toast("Логин успешно получен из sharedPref")
                    username = _username
                    if (username == "") {
                        binding.usernameTextview.text = "Аноним"
                        binding.multiplayerButton.visibility = View.GONE
                        binding.playWithBotButton.visibility = View.GONE
                    } else {
                        binding.usernameTextview.text = username
                        binding.multiplayerButton.visibility = View.VISIBLE
                        binding.playWithBotButton.visibility = View.VISIBLE
                    }
                }
            )
        }

        viewModel.validateTokenResponse.observe(viewLifecycleOwner) { result ->
            renderSimpleResult(
                root = binding.root,
                result = result,
                onError = {
                    viewModel.toast("Ошибка при валидации токена сервером")
                    println(it.message)
                },
                onSuccess = { response ->
                    if (response.success) {
                        viewModel.toast("Токен провалидирован сервером")
                        username = response.username
                        viewModel.setUsername(response.username)
                        binding.usernameTextview.text = response.username

                        if (!viewModel.isSocketExist()) {
                            viewModel.openSocket(username)
                        }

                        // TODO вынести во viewUtils
                        binding.multiplayerButton.visibility = View.VISIBLE
                        binding.playWithBotButton.visibility = View.VISIBLE
                        binding.multiplayerButton.setOnClickListener {
                            val params = FindOpponentScreenParams()
                            viewModel.launch(FindOpponentsScreenFragment.Screen(params))
                        }
                    } else {
                        if (viewModel.isSocketExist()) {
                            viewModel.closeSocket()
                        }
                        username = ""
                        binding.playWithBotButton.visibility = View.GONE
                        binding.multiplayerButton.visibility = View.GONE
                        binding.usernameTextview.text = "Аноним"
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

        viewModel.socketMessage.observe(viewLifecycleOwner) { result ->
            renderSimpleResult(
                root = binding.root,
                result = result,
                onError = {
                    viewModel.toast("Ошибка при получении сообщения через сокет")
                },
                onSuccess = { message ->
                    println("MainScreenFragment socket message = ${socketMessageUtils.toString(message)}")
                    if (message is NotCompletedGameReceivedMessage && message.username.value == username) {
                        println("mainScreenFragment fen ${message.gameFen.value}")
                        with(AcceptDeclineGameContainer(binding.root)) {
                            showWithMessage("Хотите продолжить онлайн игру\nс ${message.opponentUsername.value}?")
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
                                            isPlayWithBot = message.isPlayingWithBot.value!!.toBoolean(),
                                        )
                                    )
                                )
                            }
                            setOnDeclineGame {
                                val endGameSocketMessage = GameEndSentMessage()
                                endGameSocketMessage.username.value =
                                    message.username.value
                                endGameSocketMessage.opponentUsername.value =
                                    message.opponentUsername.value
                                endGameSocketMessage.gameEndReason.value = "Противник сдался"
                                viewModel.sendSocketMessage(endGameSocketMessage)
                            }
                        }
                        return@renderSimpleResult
                    }

                    if (message is GameStartReceivedMessage && message.username.value == username) {
                        with(AcceptDeclineGameContainer(binding.root)) {
                            showWithMessage("Хотите продолжить онлайн игру\nс ${message.opponentUsername.value}?")
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
                                declineGameMessage.opponentUsername.value = message.opponentUsername.value
                                declineGameMessage.declineReason.value = "Противник отказался начать игру"
                            }
                        }
                        return@renderSimpleResult
                    }
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

        viewModel.setLocalGameFen.observe(viewLifecycleOwner) { result ->
            renderSimpleResult(
                root = binding.root,
                result = result,
                onError = {
                    viewModel.toast("Ошибка при получении поля из локального хранилища")
                },
                onSuccess = {
                    viewModel.toast("Успешная запись поля в локальное хранилище")
                }
            )
        }

        viewModel.getLocalGameFen.observe(viewLifecycleOwner) { result ->
            renderSimpleResult(
                root = binding.root,
                result = result,
                onError = {
                    viewModel.toast("Ошибка при получении поля из локального хранилища")
                },
                onSuccess = { fen ->
                    println("MainScreenFragment getLocalGameFen fen = $fen")
                    if (fen == "") {
                        return@renderSimpleResult
                    }
                    with(AcceptDeclineGameContainer(binding.root)) {
                        showWithMessage("Хотите продолжить локальную игру?")
                        setOnAcceptGame {
                            viewModel.launch(
                                GameScreenFragment.Screen(
                                    StartGameData(
                                        username = "Аноним",
                                        opponentUsername = "Аноним",
                                        mainColor = GameColor.white,
                                        useSocket = false,
                                        fen = fen,
                                        isOpponentTurn = false,
                                        isSwitchedColor = false,
                                        isPlayWithBot = false,
                                    )
                                )
                            )
                        }
                        setOnDeclineGame {
                            viewModel.setLocalGameFen("")
                        }
                    }
                }
            )
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.start()
    }
}
