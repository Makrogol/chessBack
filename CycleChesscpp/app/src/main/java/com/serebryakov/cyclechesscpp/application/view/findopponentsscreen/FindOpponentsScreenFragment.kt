package com.serebryakov.cyclechesscpp.application.view.findopponentsscreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import com.serebryakov.cyclechesscpp.application.model.data.OpponentData
import com.serebryakov.cyclechesscpp.application.model.back.responses.UserResponse
import com.serebryakov.cyclechesscpp.application.model.back.socket.messages.DeclineGameReceivedMessage
import com.serebryakov.cyclechesscpp.application.model.back.socket.messages.DeclineGameSentMessage
import com.serebryakov.cyclechesscpp.application.model.back.socket.messages.GameStartReceivedMessage
import com.serebryakov.cyclechesscpp.application.model.back.socket.messages.GameStartSentMessage
import com.serebryakov.cyclechesscpp.application.model.back.socket.messages.UserAvailableReceivedMessage
import com.serebryakov.cyclechesscpp.foundation.socket.utils.SocketMessageUtils
import com.serebryakov.cyclechesscpp.foundation.socket.utils.SocketMessageUtilsImpl
import com.serebryakov.cyclechesscpp.application.model.cppapi.utils.Parser
import com.serebryakov.cyclechesscpp.application.model.cppapi.utils.Unparser
import com.serebryakov.cyclechesscpp.application.model.data.StartGameData
import com.serebryakov.cyclechesscpp.application.model.game.getAnotherColor
import com.serebryakov.cyclechesscpp.application.renderSimpleResult
import com.serebryakov.cyclechesscpp.application.view.containers.acceptdeclinegame.AcceptDeclineGameContainer
import com.serebryakov.cyclechesscpp.application.view.containers.choosestartgamecolor.StartGameContainer
import com.serebryakov.cyclechesscpp.application.view.gamescreen.GameScreenFragment
import com.serebryakov.cyclechesscpp.databinding.FindOpponentsScreenFragmentBinding
import com.serebryakov.cyclechesscpp.foundation.views.BaseFragment
import com.serebryakov.cyclechesscpp.foundation.views.BaseScreen
import com.serebryakov.cyclechesscpp.foundation.views.screenViewModel
import java.util.Locale

class FindOpponentsScreenFragment : BaseFragment(), OpponentsAdapter.Listener {

    class Screen(val params: FindOpponentScreenParams) : BaseScreen

    private lateinit var binding: FindOpponentsScreenFragmentBinding
    override val viewModel by screenViewModel<FindOpponentsScreenViewModel>()

    private lateinit var adapter: OpponentsAdapter
    private lateinit var chooseStartGameColorContainer: StartGameContainer

    private val unparser = Unparser()
    private val socketMessageUtils: SocketMessageUtils = SocketMessageUtilsImpl()
    private var username = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FindOpponentsScreenFragmentBinding.inflate(inflater, container, false)
        chooseStartGameColorContainer = StartGameContainer(binding.root)
        val acceptDeclineGameContainer = AcceptDeclineGameContainer(binding.root)
        var opponentsList = mutableListOf<OpponentData>()
        var params = FindOpponentScreenParams()
        var getOpponentsdata = false
        var getUsername = false

        with(binding) {
            findOpponentsEdittext.addTextChangedListener {
                it?.toString()?.let { text -> findOpponent(text, opponentsList) }
            }
        }

        viewModel.getUsername.observe(viewLifecycleOwner) { result ->
            renderSimpleResult(
                root = binding.root,
                result = result,
                onError = {
                    viewModel.toast("Ошибка при загрузке логина из хранилища")
                },
                onSuccess = { _username ->
                    username = _username
                    viewModel.getAllOpponentsData()
                    getUsername = true
                }
            )
        }

        // TODO можно удалить
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
                    println("FindOpponentScreenFragment socket message = ${socketMessageUtils.toString(message)}")
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
                                declineGameMessage.opponentUsername.value = message.opponentUsername.value
                                declineGameMessage.declineReason.value = "Противник отказался начать игру"
                            }
                            return@renderSimpleResult
                        }
                    }


                    if (message is UserAvailableReceivedMessage && getOpponentsdata) {
                        adapter.changeOpponentDataByUsername(
                            OpponentData(
                                username = message.username.value!!,
                                user_available = message.userAvailable.value!!.toBoolean()
                            )
                        )
                        return@renderSimpleResult
                    }

                    if (message is DeclineGameReceivedMessage && message.username.value == username) {
                        viewModel.toast("Противник ${message.opponentUsername.value} отказался с Вами играть\n по причине ${message.declineReason.value}")
                        return@renderSimpleResult
                    }
                }
            )
        }

        viewModel.opponentsData.observe(viewLifecycleOwner) { result ->
            renderSimpleResult(
                root = binding.root,
                result = result,
                onError = {
                    viewModel.toast("Ошибка при считывании данных об оппонентах")
                },
                onSuccess = { userResponses ->
                    adapter = OpponentsAdapter(this, username)
                    binding.opponentsRecyclerview.adapter = adapter
                    opponentsList =
                        createOpponentDataFromUserResponse(userResponses).toMutableList()
                    adapter.addOpponentData(
                        opponentsList
                    )
                    getOpponentsdata = true
                }
            )
        }

        // TODO можно удалить все парамсы
        viewModel.params.observe(viewLifecycleOwner) { result ->
            renderSimpleResult(
                root = binding.root,
                result = result,
                onError = {
                    viewModel.toast("Ошибка при получении данных с прошлого экрана")
                },
                onSuccess = { _params ->
                    params = _params
                }
            )
        }

        return binding.root
    }

    private fun createOpponentDataFromUserResponse(
        userResponses: List<UserResponse>
    ): List<OpponentData> {
        return userResponses.map { userResponse ->
            OpponentData(
                username = userResponse.username,
                user_available = userResponse.user_available,
            )
        }
    }

    private fun findOpponent(username: String, opponentList: List<OpponentData>) {
        val filteredOpponentList = mutableListOf<OpponentData>()
        filteredOpponentList.addAll(
            opponentList.filter { opponentData ->
                opponentData.username.lowercase(Locale.ROOT).contains(
                    username.lowercase(Locale.ROOT)
                )
            }
        )
        adapter.setOpponentData(filteredOpponentList)
    }

    override fun onOpponentClick(opponentData: OpponentData) {
        with(chooseStartGameColorContainer) {
            show()
            setOnColorChoose{mainColor, isSwitchedColor ->
                val startGameSocketMessage = GameStartSentMessage()
                startGameSocketMessage.username.value = username
                startGameSocketMessage.opponentUsername.value = opponentData.username
                startGameSocketMessage.mainColor.value = Parser().color(mainColor)
                startGameSocketMessage.isSwitchedColor.value = isSwitchedColor.toString()
                startGameSocketMessage.isPlayWithBot.value = false.toString()
                viewModel.sendSocketMessage(startGameSocketMessage)

                viewModel.launch(
                    GameScreenFragment.Screen(
                        StartGameData(
                            username = username,
                            opponentUsername = opponentData.username,
                            // TODO разобраться, что за дичь происходит с mainColor
                            mainColor = mainColor,
                            useSocket = true,
                            fen = null,
                            isOpponentTurn = false,
                            isSwitchedColor = isSwitchedColor,
                            isPlayWithBot = false,
                        )
                    )
                )
            }
        }
    }
}
