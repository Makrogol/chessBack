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
import com.serebryakov.cyclechesscpp.application.model.game.gamefield.holder.GameFieldHolder
import com.serebryakov.cyclechesscpp.application.model.game.gamefield.holder.GameFieldHolderImpl
import com.serebryakov.cyclechesscpp.application.renderSimpleResult
import com.serebryakov.cyclechesscpp.application.view.gamescreen.utils.GameScreenViewUtilsImpl
import com.serebryakov.cyclechesscpp.application.view.gamescreen.utils.tagholder.Tag
import com.serebryakov.cyclechesscpp.databinding.GameScreenFragmentBinding
import com.serebryakov.cyclechesscpp.foundation.views.BaseFragment
import com.serebryakov.cyclechesscpp.foundation.views.BaseScreen
import com.serebryakov.cyclechesscpp.foundation.views.screenViewModel

typealias OnGameFieldUiCellClick = (position: Position, gameField: GameField) -> Unit

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


        viewModel.startGameData.observe(viewLifecycleOwner) { result ->
            renderSimpleResult(
                root = binding.root,
                result = result,
                onError = { viewModel.toast("Ошибка при получении данных об оппоненте") },
                onSuccess = {
                    startGameData = it
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

                            gameFieldHolder.get().currentActivePiecePosition = newPositions.first
                            binding.magicPawnTransformationLinearLayout.visibility = View.GONE
                            defaultMovePipeline(newPositions, gameFieldHolder.get(), false)
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
        viewModel.endGame()
        viewModel.startGame(mainColor)
        gameFieldHolder.set(GameField(mainColor))
        viewUtils.createGameFieldUi(startGameData) { position: Position, gameField: GameField ->
            onGameFieldUiCellClick(position, gameField)
        }
        viewUtils.enableGameField()
    }

    private fun defaultMovePipeline(
        positions: Pair<Position, Position>,
        gameField: GameField,
        needSocketMessageSend: Boolean
    ) {
        when (viewModel.tryDoMove(positions)) {
            MoveType.NOT_SPECIAL -> {
                gameField.movePiece(positions.second)
            }

            MoveType.MAGIC_PAWN_TRANSFORMATION -> {
                gameField.movePiece(positions.second)
                doMagicPawnTransformation(gameField, positions.second)
            }

            MoveType.PASSANT -> {
                gameField.doPassant(positions.second)
            }

            MoveType.CASTLING -> {
                gameField.doCastling(positions.second)
            }

            MoveType.NOT_MOVE -> {
                viewModel.toast("Ошибка при попытке сделать ход")
            }
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

    private fun defaultGameCellUiClickPipeline(position: Position, gameField: GameField) {
        viewUtils.clearRoute()
        gameField.currentActivePiecePosition = position
        if (gameField.hasPiece(position)) {
            val route = viewModel.getPossibleMovesForPosition(position)
            viewUtils.drawRoute(position, route)
        }
    }

    private fun onGameFieldUiCellClick(
        position: Position,
        gameField: GameField,
    ) {
        if (viewUtils.getUiGameFieldCell(position).tag == Tag.ROUTE_CELL) {
            defaultMovePipeline(
                Pair(
                    gameField.currentActivePiecePosition,
                    position
                ),
                gameField,
                startGameData.useSocket
            )
            return
        }

        defaultGameCellUiClickPipeline(position, gameField)
    }

    private fun doMagicPawnTransformation(
        gameField: GameField,
        position: Position,
    ) {
        viewUtils.disableGameField()
        viewUtils.showMagicPawnTransformationUi()

        for (i in magicPawnTransformationTypes.indices) {
            with(viewUtils.getMagicPawnTransformationUiElement(i)) {
                setOnClickListener {
                    onClickMagicPawnTransformationUiElement(
                        position,
                        gameField,
                        magicPawnTransformationTypes[i]
                    )
                }
            }
        }
    }

    private fun onClickMagicPawnTransformationUiElement(
        position: Position,
        gameField: GameField,
        magicPawnTransformationType: PieceType
    ) {
        viewUtils.enableGameField()
        viewUtils.hideMagicPawnTransformationUi()

        val resultDoMagicPawnTransformation = viewModel.tryDoMagicPawnTransformation(
            position,
            magicPawnTransformationType
        )
        if (!resultDoMagicPawnTransformation || !gameField.magicPawnTransformation(
                position,
                magicPawnTransformationType
            )
        ) {
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
        when (viewModel.getGameState()) {
            GameState.CHECK_FOR_BLACK -> viewUtils.drawCheckCell(
                viewModel.getKingPositionByColor(GameColor.black)
            )

            GameState.CHECK_FOR_WHITE -> viewUtils.drawCheckCell(
                viewModel.getKingPositionByColor(GameColor.white)
            )

            GameState.DRAW -> {
                viewModel.toast("Ничья")
                onGameEnd("draw")
            }

            GameState.PATE -> {
                viewModel.toast("Пат")
                onGameEnd("pate")
            }

            GameState.MATE_FOR_BLACK -> {
                viewModel.toast("Мат черным. Победа белых")
                onGameEnd("mate_for_black")
            }

            GameState.MATE_FOR_WHITE -> {
                viewModel.toast("Мат белым. Победа черных")
                onGameEnd("mate_for_white")
            }

            GameState.ON_GOING -> {
                viewUtils.clearAllField()
            }
        }
    }

    private fun onGameEnd(gameResult: String) {
        viewUtils.disableGameField()
        viewUtils.clearAllField()

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