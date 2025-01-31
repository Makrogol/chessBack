package com.serebryakov.cyclechesscpp.application.view.findopponentsscreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import com.serebryakov.cyclechesscpp.application.model.user.OpponentData
import com.serebryakov.cyclechesscpp.application.model.back.responses.UserResponse
import com.serebryakov.cyclechesscpp.application.model.back.socket.messages.StartGameSocketMessage
import com.serebryakov.cyclechesscpp.application.model.game.GameColor
import com.serebryakov.cyclechesscpp.application.model.user.StartGameData
import com.serebryakov.cyclechesscpp.application.renderSimpleResult
import com.serebryakov.cyclechesscpp.application.view.gamescreen.GameScreenFragment
import com.serebryakov.cyclechesscpp.foundation.socket.BaseWebSocketListener
import com.serebryakov.cyclechesscpp.databinding.FindOpponentsScreenFragmentBinding
import com.serebryakov.cyclechesscpp.foundation.views.BaseFragment
import com.serebryakov.cyclechesscpp.foundation.views.BaseScreen
import com.serebryakov.cyclechesscpp.foundation.views.screenViewModel
import java.util.Locale

class FindOpponentsScreenFragment : BaseFragment() {

    class Screen(val username: String) : BaseScreen

    private lateinit var binding: FindOpponentsScreenFragmentBinding
    override val viewModel by screenViewModel<FindOpponentsScreenViewModel>()

    private lateinit var adapter: OpponentsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FindOpponentsScreenFragmentBinding.inflate(inflater, container, false)
        var username = ""
        val webSocketListener = BaseWebSocketListener(viewModel)
        var opponentsList = mutableListOf<OpponentData>()

        with(binding) {
            findOpponentsEdittext.addTextChangedListener {
                it?.toString()?.let { text -> findOpponent(text, opponentsList) }
            }
        }

        viewModel.socketMessage.observe(viewLifecycleOwner) { result ->
            renderSimpleResult(
                root = binding.root,
                result = result,
                onError = {
                    viewModel.toast("Ошибка при получении сообщения через сокет")
                },
                onSuccess = { message ->
                    val startGameSocketMessage = StartGameSocketMessage()
                    startGameSocketMessage.tryFillFromString(message)

                    if (startGameSocketMessage.allFieldFill() && startGameSocketMessage.username == username) {
                        viewModel.launch(
                            GameScreenFragment.Screen(
                                StartGameData(
                                    username = username,
                                    opponentUsername = startGameSocketMessage.opponentUsername!!,
                                    color = GameColor.white,
                                    webSocketListener = webSocketListener,
                                    useSocket = true
                                )
                            )
                        )
                    }
                }
            )
        }

        viewModel.gameStarted.observe(viewLifecycleOwner) { result ->
            renderSimpleResult(
                root = binding.root,
                result = result,
                onError = {
                    viewModel.toast("Ошибка при отправке сообщения через сокет")
                },
                onSuccess = { opponentData ->
                    viewModel.launch(
                        GameScreenFragment.Screen(
                            StartGameData(
                                username = username,
                                opponentUsername = opponentData.username,
                                color = GameColor.black,
                                webSocketListener = webSocketListener,
                                useSocket = true
                            )
                        )
                    )
                }
            )
        }

        viewModel.socketConnection.observe(viewLifecycleOwner) { result ->
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

        viewModel.opponentsData.observe(viewLifecycleOwner) { result ->
            renderSimpleResult(
                root = binding.root,
                result = result,
                onError = {
                    viewModel.toast("Ошибка при считывании данных об оппонентах")
                },
                onSuccess = { userResponses ->
                    adapter = OpponentsAdapter(viewModel, username)
                    binding.opponentsRecyclerview.adapter = adapter
                    opponentsList =
                        createOpponentDataFromUserResponse(userResponses).toMutableList()
                    adapter.addOpponentData(
                        opponentsList
                    )
                }
            )
        }

        viewModel.username.observe(viewLifecycleOwner) { result ->
            renderSimpleResult(
                root = binding.root,
                result = result,
                onError = {
                    viewModel.toast("Ошибка при считывании данных об оппонентах")
                },
                onSuccess = {
                    username = it
                    viewModel.createSocket(webSocketListener, it)
                }
            )
        }

        return binding.root
    }

    private fun createOpponentDataFromUserResponse(
        userResponses: List<UserResponse>
    ): List<OpponentData> {
        val opponentsData = mutableListOf<OpponentData>()
        for (userResponse in userResponses) {
            opponentsData.add(
                OpponentData(
                    username = userResponse.username,
                )
            )
        }
        return opponentsData
    }

    private fun findOpponent(userTypedLogin: String, opponentList: List<OpponentData>) {
        val filteredOpponentList = mutableListOf<OpponentData>()
        filteredOpponentList.addAll(
            opponentList.filter {
                it.username.lowercase(Locale.ROOT).contains(
                    userTypedLogin.lowercase(Locale.ROOT)
                )
            }
        )
        adapter.setOpponentData(filteredOpponentList)
    }
}