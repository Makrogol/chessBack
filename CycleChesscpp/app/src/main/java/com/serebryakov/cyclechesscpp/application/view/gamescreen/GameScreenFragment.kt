package com.serebryakov.cyclechesscpp.application.view.gamescreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.res.stringResource
import com.serebryakov.cyclechesscpp.application.model.back.socket.messages.DeclineGameReceivedMessage
import com.serebryakov.cyclechesscpp.application.model.back.socket.messages.GameEndReceivedMessage
import com.serebryakov.cyclechesscpp.application.model.back.socket.messages.GameEndSentMessage
import com.serebryakov.cyclechesscpp.application.model.back.socket.messages.GameStartReceivedMessage
import com.serebryakov.cyclechesscpp.application.model.back.socket.messages.GameStartSentMessage
import com.serebryakov.cyclechesscpp.application.model.back.socket.messages.TurnReceivedMessage
import com.serebryakov.cyclechesscpp.application.model.back.socket.messages.TurnSentMessage
import com.serebryakov.cyclechesscpp.foundation.socket.utils.SocketMessageUtils
import com.serebryakov.cyclechesscpp.foundation.socket.utils.SocketMessageUtilsImpl
import com.serebryakov.cyclechesscpp.application.model.data.StartGameData
import com.serebryakov.cyclechesscpp.application.model.game.gamefield.GameField
import com.serebryakov.cyclechesscpp.application.model.game.GameColor
import com.serebryakov.cyclechesscpp.application.model.game.GameState
import com.serebryakov.cyclechesscpp.application.model.game.MoveType
import com.serebryakov.cyclechesscpp.application.model.game.PieceType
import com.serebryakov.cyclechesscpp.application.model.game.Position
import com.serebryakov.cyclechesscpp.application.model.cppapi.utils.Parser
import com.serebryakov.cyclechesscpp.application.model.cppapi.utils.Unparser
import com.serebryakov.cyclechesscpp.application.model.game.Move
import com.serebryakov.cyclechesscpp.application.model.game.gamefield.holder.GameFieldHolderImpl
import com.serebryakov.cyclechesscpp.application.model.game.getAnotherColor
import com.serebryakov.cyclechesscpp.application.renderSimpleResult
import com.serebryakov.cyclechesscpp.application.view.gamescreen.viewutils.GameScreenViewUtilsImpl
import com.serebryakov.cyclechesscpp.application.view.gamescreen.viewutils.tagholder.Tag
import com.serebryakov.cyclechesscpp.databinding.GameScreenFragmentBinding
import com.serebryakov.cyclechesscpp.foundation.views.BaseFragment
import com.serebryakov.cyclechesscpp.foundation.views.BaseScreen
import com.serebryakov.cyclechesscpp.foundation.views.screenViewModel

typealias OnGameFieldUiCellClick = (position: Position) -> Unit

class GameScreenFragment : BaseFragment() {

    class Screen(val startGameData: StartGameData) : BaseScreen

    private lateinit var binding: GameScreenFragmentBinding
    override val viewModel by screenViewModel<GameScreenViewModel>()

    private val parser = Parser()
    private val unparser = Unparser()
    private val gameFieldHolder = GameFieldHolderImpl()
    private val socketMessageUtils: SocketMessageUtils = SocketMessageUtilsImpl()

    private lateinit var viewUtils: GameScreenViewUtilsImpl
    private lateinit var startGameData: StartGameData

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = GameScreenFragmentBinding.inflate(inflater, container, false)
        viewUtils = GameScreenViewUtilsImpl(binding, context, gameFieldHolder)

        viewModel.startGameData.observe(viewLifecycleOwner) { result ->
            renderSimpleResult(
                root = binding.root,
                result = result,
                onError = { viewModel.toast("Ошибка при получении данных об оппоненте") },
                onSuccess = { _startGameData ->
                    startGameData = _startGameData
                    viewUtils.setUsernames(
                        startGameData.username,
                        startGameData.opponentUsername
                    )
                    startNewGame()
                }
            )
        }

        viewModel.socketMessage.observe(viewLifecycleOwner) { result ->
            renderSimpleResult(
                root = binding.root,
                result = result,
                onError = { viewModel.toast("Ошибка при получении сообщения через сокет") },
                onSuccess = { message ->
                    println("GameScreenFragmentMessage = ${socketMessageUtils.toString(message)}")
                    if (message is GameEndReceivedMessage && message.username.value == startGameData.username) {
                        println("message from opponent game end ${message.gameEndReason}")
                        viewUtils.disableGameField()
                        viewUtils.clearFieldWithoutCheckColor()
                        if (viewUtils.isEmptyGameResult()) {
                            viewUtils.setGameResult(message.gameEndReason.value!!)
                        }
                        return@renderSimpleResult
                    }

                    if (message is TurnReceivedMessage && message.username.value == startGameData.username) {
                        viewUtils.enableGameField()
                        val move = unparser.getMove(message.turn.value!!)
                        gameFieldHolder.get()?.setCurrentActivePosition(move.positionFirst)
                        defaultMovePipeline(move, false)
                        return@renderSimpleResult
                    }

                    if (message is GameStartReceivedMessage && message.username.value == startGameData.username) {
                        viewModel.toast("Новая игра")
                        startGameData.mainColor = unparser.getColor(message.mainColor.value!!)
                        startNewGame()
                        return@renderSimpleResult
                    }

                    if (message is DeclineGameReceivedMessage && message.username.value == startGameData.username) {
                        viewModel.toast("Противник ${message.opponentUsername.value} отказался с Вами играть\n по причине ${message.declineReason.value}")
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

        viewModel.setLocalGameFen.observe(viewLifecycleOwner) { result ->
            renderSimpleResult(
                root = binding.root,
                result = result,
                onError = {
                    viewModel.toast("Ошибка при записи поля в локальное хранилище")
                },
                onSuccess = {
                    viewModel.toast("Успешная запись поля в локальное хранилище")
                }
            )
        }

        viewModel.setIsPlayWithBot.observe(viewLifecycleOwner) { result ->
            renderSimpleResult(
                root = binding.root,
                result = result,
                onError = {
                    viewModel.toast("Ошибка при записи игры с ботом в локальное хранилище")
                },
                onSuccess = {
                    viewModel.toast("Успешная запись игры с ботом в локальное хранилище")
                }
            )
        }

        binding.startNewGameButton.setOnClickListener {
            if (startGameData.isSwitchedColor) {
                startGameData.mainColor = startGameData.mainColor.getAnotherColor()
            }
            startNewGame()
            sendStartNewGameSocketMessage()
        }

        binding.capitulateGameButton.setOnClickListener {
            onGameEnd("Противник сдался")
        }

        return binding.root
    }

    private fun sendStartNewGameSocketMessage() {
        val startGameSocketMessage = GameStartSentMessage()
        startGameSocketMessage.username.value = startGameData.username
        startGameSocketMessage.opponentUsername.value = startGameData.opponentUsername
        startGameSocketMessage.mainColor.value = parser.color(startGameData.mainColor)
        startGameSocketMessage.isSwitchedColor.value = startGameData.isSwitchedColor.toString()
        startGameSocketMessage.isPlayWithBot.value = startGameData.isPlayWithBot.toString()
        viewModel.sendSocketMessage(startGameSocketMessage)
    }

    private fun startNewGame() {
        viewUtils.clearField()
        viewUtils.clearGameResult()
        viewUtils.enableGameField()
        gameFieldHolder.get()?.endGame()
        viewModel.setLocalGameFen("") // TODO заменить на emptyFen
        viewModel.setIsPlayWithBot(startGameData.isPlayWithBot)

        gameFieldHolder.set(GameField(startGameData.mainColor))
        if (!gameFieldHolder.has()) {
            // TODO error need log
            return
        }
        if (startGameData.fen != null) {
            if (startGameData.isOpponentTurn) {
                // Fen взяли из даты оппонента, поэтому если у него стоит isOpponentTurn
                // То это значит, что сейчас наш ход. То есть последний ход был его
                // И фен там лежит его, то есть нам надо его ревеснуть, чтобы использовать
                gameFieldHolder.getStrict().startGameWithReversedFen(startGameData.fen!!)
            } else {
                gameFieldHolder.getStrict().startGameWithFen(startGameData.fen!!)
            }

            startGameData.fen = null
            startGameData.isOpponentTurn = false
        } else {
            gameFieldHolder.getStrict().startGame()
        }

        viewUtils.createGameFieldUi { position: Position ->
            onGameFieldUiCellClick(position)
        }
        viewUtils.setStartTurnColor(startGameData.mainColor)
    }

    private fun defaultMovePipeline(
        move: Move,
        needSendSocketMessage: Boolean
    ) {
        if (!gameFieldHolder.has()) {
            // TODO error need log
            return
        }

        val resultDoMove = gameFieldHolder.getStrict().tryDoMoveV2(move)
        if (resultDoMove == MoveType.MAGIC_PAWN_TRANSFORMATION) {
            doMagicPawnTransformation(move)
        } else if (resultDoMove == MoveType.NOT_MOVE) {
            viewModel.toast("Ошибка при попытке сделать ход")
        } else {
            if (needSendSocketMessage && startGameData.useSocket) {
                sendTurnMessage(move)
            }
        }
        viewUtils.changeTurnColor()
        onMoveEnd()
    }

    private fun sendTurnMessage(move: Move) {
        if (!gameFieldHolder.has()) {
            // TODO error need log
            return
        }
        val turnSocketMessage = TurnSentMessage()
        turnSocketMessage.username.value = startGameData.username
        turnSocketMessage.opponentUsername.value = startGameData.opponentUsername
        turnSocketMessage.turn.value = parser.move(move)
        turnSocketMessage.gameFen.value = gameFieldHolder.getStrict().getFen()
        viewModel.sendSocketMessage(turnSocketMessage)
        println("turnSocketMessage ${socketMessageUtils.toString(turnSocketMessage)}")
    }

    private fun defaultGameCellUiClickPipeline(position: Position) {
        if (!gameFieldHolder.has()) {
            // TODO error need log
            return
        }

        with(gameFieldHolder.getStrict()) {
            viewUtils.clearRoute()
            setCurrentActivePosition(position)
            if (hasPiece(position)) {
                val route = if (startGameData.useSocket) {
                    getPossibleMovesForPositionMultiplayer(position)
                } else {
                    getPossibleMovesForPosition(position)
                }
                viewUtils.drawRoute(position, route)
                viewUtils.setCountPieceSteps(position)
            }
        }
    }

    private fun onGameFieldUiCellClick(position: Position) {
        viewUtils.hideCountPieceSteps()
        if (viewUtils.getUiGameFieldCell(position).tag == Tag.ROUTE_CELL) {
            if (!gameFieldHolder.has()) {
                // TODO error need log
                return
            }

            defaultMovePipeline(
                Move(
                    positionFirst = gameFieldHolder.getStrict().getCurrentActivePosition(),
                    positionSecond = position
                ),
                startGameData.useSocket
            )
            return
        }

        defaultGameCellUiClickPipeline(position)
    }

    private fun doMagicPawnTransformation(move: Move) {
        if (!gameFieldHolder.has()) {
            // TODO error need log
            return
        }
        viewUtils.disableGameField()
        viewUtils.showMagicPawnTransformationUi(
            gameFieldHolder.getStrict().getPieceColor(move.positionSecond)
        )

        for (i in magicPawnTransformationTypes.indices) {
            viewUtils.getMagicPawnTransformationUiElement(i).setOnClickListener {
                onClickMagicPawnTransformationUiElement(
                    move,
                    magicPawnTransformationTypes[i]
                )
            }
        }
    }

    private fun onClickMagicPawnTransformationUiElement(
        move: Move,
        magicPawnTransformationType: PieceType
    ) {
        if (!gameFieldHolder.has()) {
            // TODO error need log
            return
        }

        viewUtils.enableGameField()
        viewUtils.hideMagicPawnTransformationUi()

        val resultDoMagicPawnTransformation =
            gameFieldHolder.getStrict().tryDoMagicPawnTransformation(
                move.positionSecond,
                magicPawnTransformationType
            )
        if (!resultDoMagicPawnTransformation) {
            viewModel.toast("Ошибка при попытке сделать превращение пешки")
        }
        move.promotion = magicPawnTransformationType

        if (startGameData.useSocket) {
            sendTurnMessage(move)
        }
        onMoveEnd()
    }

    private fun onMoveEnd() {
        if (!gameFieldHolder.has()) {
            // TODO error need log
            return
        }

        viewUtils.drawGameField()
        viewUtils.clearRoute()

        if (!startGameData.useSocket) {
            viewModel.setLocalGameFen(gameFieldHolder.getStrict().getFen())
        }

        gameStateRequestAndReaction()
    }

    private fun gameStateRequestAndReaction() {
        if (!gameFieldHolder.has()) {
            // TODO error need log
            return
        }

        when (gameFieldHolder.getStrict().getGameState()) {
            GameState.CHECK_FOR_BLACK -> viewUtils.drawCheckCell(
                gameFieldHolder.getStrict().getKingPositionByColor(GameColor.black)
            )

            GameState.CHECK_FOR_WHITE -> viewUtils.drawCheckCell(
                gameFieldHolder.getStrict().getKingPositionByColor(GameColor.white)
            )

            GameState.DRAW -> {
                viewUtils.setGameResult("Ничья")
                onGameEnd("draw")
            }

            GameState.PATE -> {
                viewUtils.setGameResult("Пат")
                onGameEnd("pate")
            }

            GameState.MATE_FOR_BLACK -> {
                viewUtils.drawCheckCell(
                    gameFieldHolder.getStrict().getKingPositionByColor(GameColor.black)
                )
                viewUtils.setGameResult("Мат черным")
                onGameEnd("mate_for_black")
            }

            GameState.MATE_FOR_WHITE -> {
                viewUtils.drawCheckCell(
                    gameFieldHolder.getStrict().getKingPositionByColor(GameColor.white)
                )
                viewUtils.setGameResult("Мат белым")
                onGameEnd("mate_for_white")
            }

            GameState.ON_GOING -> {
                viewUtils.clearField()
            }
        }
    }

    private fun onGameEnd(gameResult: String) {
        viewUtils.disableGameField()
        viewUtils.clearFieldWithoutCheckColor()

        if (startGameData.useSocket) {
            val gameEndSocketMessage = GameEndSentMessage()
            gameEndSocketMessage.username.value = startGameData.username
            gameEndSocketMessage.opponentUsername.value = startGameData.opponentUsername
            gameEndSocketMessage.gameEndReason.value = gameResult
            viewModel.sendSocketMessage(gameEndSocketMessage)
        }
    }


    // TODO унести это куда-нибудь (в GameField как вариант)
    companion object {
        val magicPawnTransformationTypes =
            listOf(PieceType.KNIGHT, PieceType.BISHOP, PieceType.ROOK, PieceType.QUEEN)
    }
}
