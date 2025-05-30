package com.serebryakov.cyclechesscpp.application.view.containers.choosestartgamecolor

import android.view.View
import android.view.ViewGroup
import com.serebryakov.cyclechesscpp.application.model.game.GameColor
import com.serebryakov.cyclechesscpp.application.model.game.generateRandomGameColor
import com.serebryakov.cyclechesscpp.databinding.StartGameContainerBinding
import com.serebryakov.cyclechesscpp.foundation.container.BaseContainer

typealias OnColorChoose = (color: GameColor, isSwitchedColor: Boolean) -> Unit

class StartGameContainer(private val root: ViewGroup) : BaseContainer(root) {
    private val binding = StartGameContainerBinding.bind(root)
    private var _onColorChoose: OnColorChoose = { _: GameColor, _: Boolean -> }

    init {
        with(binding) {
            darkBackgroundLayout.setOnClickListener {
                hide()
            }
            whiteColorRadioButton.setOnClickListener {
                _onColorChoose(GameColor.white, false)
            }
            blackColorRadioButton.setOnClickListener {
                _onColorChoose(GameColor.black, false)
            }
            switchedColorRadioButton.setOnClickListener {
                _onColorChoose(generateRandomGameColor(), true)
            }
        }
    }

    fun setOnColorChoose(onColorChoose: OnColorChoose) {
        _onColorChoose = onColorChoose
    }

    fun show() {
        binding.chooseStartGameColorLayout.visibility = View.VISIBLE
        binding.darkBackgroundLayout.visibility = View.VISIBLE
        binding.darkBackgroundLayout.alpha = 0.8f
    }

    fun hide() {
        binding.chooseStartGameColorLayout.visibility = View.GONE
        binding.darkBackgroundLayout.visibility = View.GONE
    }
}
