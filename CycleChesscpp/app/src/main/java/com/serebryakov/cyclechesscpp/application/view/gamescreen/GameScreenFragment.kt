package com.serebryakov.cyclechesscpp.application.view.gamescreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.serebryakov.cyclechesscpp.application.model.back.socket.messages.DefaultSocketMessage
import com.serebryakov.cyclechesscpp.application.model.back.socket.messages.EndGameSocketMessage
import com.serebryakov.cyclechesscpp.application.model.back.socket.messages.TurnSocketMessage
import com.serebryakov.cyclechesscpp.application.model.user.StartGameData
import com.serebryakov.cyclechesscpp.application.model.game.gamefield.GameField
import com.serebryakov.cyclechesscpp.application.model.game.GameColor
import com.serebryakov.cyclechesscpp.application.model.game.GameState
import com.serebryakov.cyclechesscpp.application.model.game.MoveType
import com.serebryakov.cyclechesscpp.application.model.game.PieceType
import com.serebryakov.cyclechesscpp.application.model.game.Position
import com.serebryakov.cyclechesscpp.application.model.game.getAnotherColor
import com.serebryakov.cyclechesscpp.application.model.cppapi.cpptools.Parser
import com.serebryakov.cyclechesscpp.application.model.cppapi.cpptools.Unparser
import com.serebryakov.cyclechesscpp.application.model.game.gamefield.holder.GameFieldHolderImpl
import com.serebryakov.cyclechesscpp.application.renderSimpleResult
import com.serebryakov.cyclechesscpp.application.view.findopponentsscreen.FindOpponentsScreenFragment
import com.serebryakov.cyclechesscpp.application.view.findopponentsscreen.FindOpponentScreenParams
import com.serebryakov.cyclechesscpp.application.view.gamescreen.utils.GameScreenViewUtilsImpl
import com.serebryakov.cyclechesscpp.application.view.gamescreen.utils.tagholder.Tag
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
    private lateinit var viewUtils: GameScreenViewUtilsImpl
    private lateinit var startGameData: StartGameData

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = GameScreenFragmentBinding.inflate(inflater, container, false)
        var mainColor = GameColor.noColor
        viewUtils = GameScreenViewUtilsImpl(binding, context, gameFieldHolder)

        binding.closeButton.setOnClickListener {
            val params = FindOpponentScreenParams(
                username = startGameData.username,
                needCreateSocket = false
            )
            viewModel.launch(FindOpponentsScreenFragment.Screen(params))
        }


        viewModel.startGameData.observe(viewLifecycleOwner) { result ->
            renderSimpleResult(
                root = binding.root,
                result = result,
                onError = { viewModel.toast("Ошибка при получении данных об оппоненте") },
                onSuccess = {_startGameData ->
                    startGameData = _startGameData
                    if (startGameData.useSocket) {
                        startGameData.webSocketListener.setViewModel(viewModel)
                        viewUtils.setUsernames(
                            startGameData.username,
                            startGameData.opponentUsername
                        )
                    }
                    mainColor = startGameData.color.getAnotherColor()
                    startNewGame(mainColor, startGameData)
                }
            )
        }

        viewModel.socketMessage.observe(viewLifecycleOwner) { result ->
            renderSimpleResult(
                root = binding.root,
                result = result,
                onError = { viewModel.toast("Ошибка при получении сообщения через вебсокет") },
                onSuccess = { message ->
                    val defaultSocketMessage = DefaultSocketMessage()
                    defaultSocketMessage.tryFillFromString(message)
                    if (defaultSocketMessage.allFieldFill() && defaultSocketMessage.username == startGameData.username) {

                        val endGameSocketMessage = EndGameSocketMessage()
                        endGameSocketMessage.fillFromStringAndDefaultMessage(
                            message,
                            defaultSocketMessage
                        )
                        if (endGameSocketMessage.allFieldFill()) {
                            println("message from opponent game end ${endGameSocketMessage.gameEnd}")
                            viewUtils.disableGameField()
                        }

                        val turnSocketMessage = TurnSocketMessage()
                        turnSocketMessage.fillFromStringAndDefaultMessage(
                            message,
                            defaultSocketMessage
                        )
                        if (turnSocketMessage.allFieldFill()) {
                            viewUtils.enableGameField()
                            val positions = unparser.getPositionToMove(turnSocketMessage.turn!!)

                            val newPositions = Pair(
                                Position(7 - positions.first.i, 7 - positions.first.j),
                                Position(7 - positions.second.i, 7 - positions.second.j)
                            )

                            gameFieldHolder.get()?.setCurrentActivePosition(newPositions.first)
                            binding.magicPawnTransformationLinearLayout.visibility = View.GONE
                            defaultMovePipeline(newPositions, false)
                        }
                    }
                }
            )
        }

        binding.startNewGameButton.setOnClickListener {
            startNewGame(mainColor, startGameData)
        }

        return binding.root
    }

    private fun startNewGame(mainColor: GameColor, startGameData: StartGameData) {
        viewUtils.clearField()
        viewUtils.clearGameResult()
        gameFieldHolder.get()?.endGame()

        gameFieldHolder.set(GameField(mainColor))
        gameFieldHolder.get()?.startGame(mainColor)
        viewUtils.createGameFieldUi(startGameData) { position: Position ->
            onGameFieldUiCellClick(position)
        }
        viewUtils.enableGameField()
    }

    private fun defaultMovePipeline(
        positions: Pair<Position, Position>,
        needSocketMessageSend: Boolean
    ) {
        if (!gameFieldHolder.has()) {
            // TODO error need log
            return
        }

        val resultDoMove = gameFieldHolder.getStrict().tryDoMove(positions)
        if (resultDoMove == MoveType.MAGIC_PAWN_TRANSFORMATION) {
            doMagicPawnTransformation(positions.second)
        } else if (resultDoMove == MoveType.NOT_MOVE) {
            viewModel.toast("Ошибка при попытке сделать ход")
        }

        if (needSocketMessageSend && startGameData.useSocket) {
            val turnSocketMessage = TurnSocketMessage()
            turnSocketMessage.fillFromStartGameData(startGameData)
            turnSocketMessage.turn = parser.positionsToMove(positions)
            viewModel.sendSocketMessage(turnSocketMessage)
            viewUtils.disableGameField()
        }

        onMoveEnd()
    }

    private fun defaultGameCellUiClickPipeline(position: Position) {
        if (!gameFieldHolder.has()) {
            // TODO error need log
            return
        }

        viewUtils.clearRoute()
        gameFieldHolder.getStrict().setCurrentActivePosition(position)
        if (gameFieldHolder.getStrict().hasPiece(position)) {
            val route = gameFieldHolder.getStrict().getPossibleMovesForPosition(position)
            viewUtils.drawRoute(position, route)
        }
    }

    private fun onGameFieldUiCellClick(position: Position) {
        if (viewUtils.getUiGameFieldCell(position).tag == Tag.ROUTE_CELL) {
            if (!gameFieldHolder.has()) {
                // TODO error need log
                return
            }

            defaultMovePipeline(
                Pair(
                    gameFieldHolder.getStrict().getCurrentActivePosition(),
                    position
                ),
                startGameData.useSocket
            )
            return
        }

        defaultGameCellUiClickPipeline(position)
    }

    private fun doMagicPawnTransformation(position: Position) {
        viewUtils.disableGameField()
        viewUtils.showMagicPawnTransformationUi()

        for (i in magicPawnTransformationTypes.indices) {
            with(viewUtils.getMagicPawnTransformationUiElement(i)) {
                setOnClickListener {
                    onClickMagicPawnTransformationUiElement(
                        position,
                        magicPawnTransformationTypes[i]
                    )
                }
            }
        }
    }

    private fun onClickMagicPawnTransformationUiElement(
        position: Position,
        magicPawnTransformationType: PieceType
    ) {
        if (!gameFieldHolder.has()) {
            // TODO error need log
            return
        }

        viewUtils.enableGameField()
        viewUtils.hideMagicPawnTransformationUi()

        val resultDoMagicPawnTransformation = gameFieldHolder.getStrict().tryDoMagicPawnTransformation(
            position,
            magicPawnTransformationType
        )
        if (!resultDoMagicPawnTransformation) {
            viewModel.toast("Ошибка при попытке сделать превращение пешки")
        }
        onMoveEnd()
    }

    private fun onMoveEnd() {
        viewUtils.drawGameField()
        viewUtils.clearRoute()
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
            val endGameSocketMessage = EndGameSocketMessage()
            endGameSocketMessage.fillFromStartGameData(startGameData)
            endGameSocketMessage.gameEnd = gameResult
            viewModel.sendSocketMessage(endGameSocketMessage)
        }
    }


    companion object {
        val magicPawnTransformationTypes =
            listOf(PieceType.KNIGHT, PieceType.BISHOP, PieceType.ROOK, PieceType.QUEEN)
    }


    // фактически нам от GameField нужно только, чтобы оно умело двигать фигуры как ему скажут и все
    // и умело делать рокировку, взятие на проходе и трансформацию пешек
    // фигуры в данном случае это могут быть просто их типы, больше ничего не нужно

}