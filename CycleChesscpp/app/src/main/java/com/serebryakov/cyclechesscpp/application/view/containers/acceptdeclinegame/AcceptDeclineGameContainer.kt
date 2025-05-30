package com.serebryakov.cyclechesscpp.application.view.containers.acceptdeclinegame

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import com.serebryakov.cyclechesscpp.databinding.AcceptDeclineGameContainerBinding
import com.serebryakov.cyclechesscpp.foundation.container.BaseContainer

typealias OnAcceptGame = () -> Unit
typealias OnDeclineGame = () -> Unit

class AcceptDeclineGameContainer(private val root: ViewGroup) : BaseContainer(root) {
    private val binding = AcceptDeclineGameContainerBinding.bind(root)
    private var _onAcceptGame: OnAcceptGame = {}
    private var _onDeclineGame: OnDeclineGame = {}

    init {
        with(binding) {
            acceptGameButton.setOnClickListener {
                _onAcceptGame()
                hide()
            }
            darkBackgroundLayout.setOnClickListener {
                _onDeclineGame()
                hide()
            }
            declineGameButton.setOnClickListener {
                _onDeclineGame()
                hide()
            }
        }
    }

    fun setOnAcceptGame(onAcceptGame: OnAcceptGame) {
        _onAcceptGame = onAcceptGame
    }

    fun setOnDeclineGame(onDeclineGame: OnDeclineGame) {
        _onDeclineGame = onDeclineGame
    }

    fun showWithMessage(message: String) {
        binding.darkBackgroundLayout.visibility = View.VISIBLE
        binding.darkBackgroundLayout.alpha = 0.8f
        binding.acceptDeclineGameLayout.visibility = View.VISIBLE
        binding.acceptDeclineGameMessageTextview.text = message
    }

    fun hide() {
        binding.acceptDeclineGameLayout.visibility = View.GONE
        binding.darkBackgroundLayout.visibility = View.GONE
    }
}
