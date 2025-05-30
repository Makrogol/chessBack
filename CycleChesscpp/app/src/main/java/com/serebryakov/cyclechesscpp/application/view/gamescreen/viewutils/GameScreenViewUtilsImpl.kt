package com.serebryakov.cyclechesscpp.application.view.gamescreen.viewutils

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.widget.ImageView
import com.serebryakov.cyclechesscpp.R
import com.serebryakov.cyclechesscpp.application.model.game.PieceColor
import com.serebryakov.cyclechesscpp.application.model.game.PieceType
import com.serebryakov.cyclechesscpp.application.model.game.Position
import com.serebryakov.cyclechesscpp.application.model.game.Route
import com.serebryakov.cyclechesscpp.application.model.game.gamefield.holder.GameFieldHolder
import com.serebryakov.cyclechesscpp.application.model.game.size
import com.serebryakov.cyclechesscpp.application.model.game.GameColor
import com.serebryakov.cyclechesscpp.application.view.gamescreen.GameScreenFragment.Companion.magicPawnTransformationTypes
import com.serebryakov.cyclechesscpp.application.view.gamescreen.OnGameFieldUiCellClick
import com.serebryakov.cyclechesscpp.application.view.gamescreen.viewutils.tagholder.GameScreenTagHolder
import com.serebryakov.cyclechesscpp.application.view.gamescreen.viewutils.tagholder.GameScreenTagHolderImpl
import com.serebryakov.cyclechesscpp.application.view.gamescreen.viewutils.tagholder.Tag
import com.serebryakov.cyclechesscpp.databinding.GameScreenFragmentBinding
import com.serebryakov.cyclechesscpp.foundation.tools.ScreenUtils
import com.serebryakov.cyclechesscpp.foundation.tools.convertDpToPixel

typealias OnUiGameFieldCellIterate = (position: Position) -> Unit
typealias UiGameFieldCell = ImageView
typealias UiMagicPawnTransformationElement = ImageView


// TODO убрать !! у gameFieldHolder
@SuppressLint("UseCompatLoadingForDrawables")
class GameScreenViewUtilsImpl(
    private val binding: GameScreenFragmentBinding,
    private val context: Context?,
    private val gameFieldHolder: GameFieldHolder
) : GameScreenViewUtils {
    private val tagHolder: GameScreenTagHolder = GameScreenTagHolderImpl()

    override fun clearField() {
        iterateUiGameField { position ->
            if (!gameFieldHolder.has()) {
                // TODO error need log
                return@iterateUiGameField
            }

            val color = gameFieldHolder.getStrict().getCellColorByPosition(position).toScreenColor()
            setBackgroundColor(getUiGameFieldCell(position), color)
        }
    }

    override fun clearFieldWithoutCheckColor() {
        iterateUiGameField { position ->
            if (!gameFieldHolder.has()) {
                // TODO error need log
                return@iterateUiGameField
            }

            val uiGameFieldCell = getUiGameFieldCell(position)
            if ((uiGameFieldCell.tag as Tag) != Tag.CHECK_CELL) {
                val color =
                    gameFieldHolder.getStrict().getCellColorByPosition(position).toScreenColor()
                setBackgroundColor(uiGameFieldCell, color)
            }
        }
    }

    override fun clearRoute() {
        iterateUiGameField { position ->
            if (!gameFieldHolder.has()) {
                // TODO error need log
                return@iterateUiGameField
            }

            val uiGameFieldCell = getUiGameFieldCell(position)
            if (tagHolder.isTagInRouteValues(uiGameFieldCell.tag as Tag)) {
                val color =
                    gameFieldHolder.getStrict().getCellColorByPosition(position).toScreenColor()
                setBackgroundColor(uiGameFieldCell, color)
            }
        }
    }

    override fun drawGameField() {
        iterateUiGameField { position ->
            with(getUiGameFieldCell(position)) {
                if (!gameFieldHolder.has()) {
                    // TODO error need log
                    return@iterateUiGameField
                }
                val piece = gameFieldHolder.getStrict().getPieceByPosition(position)
                setImageResource(
                    getPieceDrawable(
                        piece?.type,
                        piece?.color,
                        piece?.canDoStepsOverBoard()
                    )
                )
            }
        }
    }

    override fun createGameFieldUi(onGameFieldUiCellClick: OnGameFieldUiCellClick) {
        val sizeView = getElementSize()
        iterateUiGameField { position ->
            with(getUiGameFieldCell(position)) {
                if (!gameFieldHolder.has()) {
                    // TODO error need log
                    return@iterateUiGameField
                }

                val piece = gameFieldHolder.getStrict().getPieceByPosition(position)
                val color =
                    gameFieldHolder.getStrict().getCellColorByPosition(position).toScreenColor()
                println("$position ${piece?.type} ${piece?.color}")

                setImageResource(
                    getPieceDrawable(
                        piece?.type,
                        piece?.color,
                        piece?.canDoStepsOverBoard()
                    )
                )
                setBackgroundColor(this@with, color)
                visibility = View.VISIBLE
                layoutParams.width = sizeView
                layoutParams.height = sizeView

                setOnClickListener {
                    onGameFieldUiCellClick(position)
                }
            }
        }

        createMagicPawnTransformationUi()
    }

    override fun disableGameField() {
        setGameFieldEnabledValue(false)
    }

    override fun enableGameField() {
        setGameFieldEnabledValue(true)
    }

    override fun drawCheckCell(position: Position) {
        val color = R.color.check_cell_color
        setBackgroundColor(getUiGameFieldCell(position), color)
    }

    override fun drawRoute(position: Position, route: Route) {
        if (route.size == 0) {
            drawPieceEmptyRoute(position)
            return
        }
        for (el in route) {
            val color = R.color.piece_route_cell_color
            setBackgroundColor(getUiGameFieldCell(el), color)
        }
    }

    override fun getUiGameFieldCell(position: Position) = getUiGameFieldCell(position.i, position.j)

    override fun getUiGameFieldCell(i: Int, j: Int) =
        binding.chessFieldGridLayout.getChildAt(i * size + j) as UiGameFieldCell

    override fun getMagicPawnTransformationUiElement(i: Int) =
        binding.magicPawnTransformationLinearLayout.getChildAt(i) as UiMagicPawnTransformationElement

    override fun showMagicPawnTransformationUi(pieceColor: PieceColor) {
        binding.userNameTextview.visibility = View.GONE
        binding.magicPawnTransformationLinearLayout.visibility = View.VISIBLE

        for (i in magicPawnTransformationTypes.indices) {
            with(getMagicPawnTransformationUiElement(i)) {
                setImageResource(
                    getPieceDrawable(
                        magicPawnTransformationTypes[i],
                        pieceColor,
                        false,
                    )
                )
            }
        }
    }

    override fun hideMagicPawnTransformationUi() {
        binding.userNameTextview.visibility = View.VISIBLE
        binding.magicPawnTransformationLinearLayout.visibility = View.GONE
    }

    override fun setUsernames(username: String, opponentUsername: String) {
        binding.opponentNameTextview.text = opponentUsername
        binding.userNameTextview.text = username
    }

    override fun setGameResult(gameResult: String) {
        binding.gameResultTextview.text = gameResult
    }

    override fun clearGameResult() {
        setGameResult("")
    }

    @SuppressLint("ResourceAsColor")
    override fun setStartTurnColor(mainColor: GameColor) {
        if (!gameFieldHolder.has()) {
            // TODO error need log
            return
        }

        val turnColor = gameFieldHolder.getStrict().getCurrentTurnColor()
        with(binding) {
            markUsernameTextViewIsNoCurrentTurn(opponentNameTextview)
            markUsernameTextViewIsNoCurrentTurn(userNameTextview)

            if (turnColor == GameColor.white) {
                // Текущий ход - белых
                if (mainColor == GameColor.white) {
                    // Играем за белых
                    markUsernameTextViewIsCurrentTurn(userNameTextview)
                } else {
                    // Играем за черных
                    markUsernameTextViewIsCurrentTurn(opponentNameTextview)
                }
            } else {
                // Текущий ход - черных
                if (mainColor == GameColor.white) {
                    // Играем за белых
                    markUsernameTextViewIsCurrentTurn(opponentNameTextview)
                } else {
                    // Играем за черных
                    markUsernameTextViewIsCurrentTurn(userNameTextview)
                }
            }
        }
    }

    @SuppressLint("ResourceAsColor")
    override fun changeTurnColor() {
        with(binding) {
            if (opponentNameTextview.tag == Tag.CURRENT_TURN) {
                markUsernameTextViewIsCurrentTurn(userNameTextview)
                markUsernameTextViewIsNoCurrentTurn(opponentNameTextview)
            } else {
                markUsernameTextViewIsCurrentTurn(opponentNameTextview)
                markUsernameTextViewIsNoCurrentTurn(userNameTextview)
            }
        }
    }

    override fun setCountPieceSteps(position: Position) {
        if (!gameFieldHolder.has()) {
            return
        }

        with(binding.countPieceStepsTextview) {
            visibility = View.VISIBLE
            println("setCountPieceSteps")
            text = "Фигура сделала ${
                gameFieldHolder.getStrict().getCountPieceStepsByPosition(position)
            } ходов"
        }
    }

    override fun hideCountPieceSteps() {
        with(binding.countPieceStepsTextview) {
            visibility = View.GONE
            text = ""
        }
    }

    override fun isUserTurn(mainColor: GameColor, turnColor: GameColor): Boolean {
        return if (turnColor == GameColor.white) {
            // Текущий ход - белых
            mainColor != GameColor.white
        } else {
            // Текущий ход - черных
            mainColor == GameColor.white
        }
    }

    private fun markUsernameTextViewIsNoCurrentTurn(textView: View) {
        textView.setBackgroundResource(R.color.user_no_current_turn_color)
        textView.tag = tagHolder.getTagByScreenColor(R.color.user_no_current_turn_color)
    }

    private fun markUsernameTextViewIsCurrentTurn(textView: View) {
        textView.setBackgroundResource(R.color.user_current_turn_color)
        textView.tag = tagHolder.getTagByScreenColor(R.color.user_current_turn_color)
    }

    private fun createMagicPawnTransformationUi() {
        val sizeView = getElementSize()
        val color = R.color.magic_pawn_transformation_color
        hideMagicPawnTransformationUi()
        for (i in magicPawnTransformationTypes.indices) {
            with(getMagicPawnTransformationUiElement(i)) {
                if (!gameFieldHolder.has()) {
                    // TODO error need log
                    return
                }
                layoutParams.width = sizeView
                layoutParams.height = sizeView
                background = context.getDrawable(color)
            }
        }
    }

    private fun setBackgroundColor(uiGameFieldCell: UiGameFieldCell, color: Int) {
        with(uiGameFieldCell) {
            background = context.getDrawable(color)
            tag = tagHolder.getTagByScreenColor(color)
        }
    }

    private fun iterateUiGameField(onUiGameFieldCellIterate: OnUiGameFieldCellIterate) {
        var position: Position
        for (i in 0 until size) {
            for (j in 0 until size) {
                position = Position(i, j)
                onUiGameFieldCellIterate(position)
            }
        }
    }

    private fun setGameFieldEnabledValue(enabled: Boolean) {
        iterateUiGameField { position ->
            with(getUiGameFieldCell(position)) {
                isEnabled = enabled
            }
        }
    }

    override fun drawPieceEmptyRoute(position: Position) {
        val color = R.color.empty_route_color
        setBackgroundColor(getUiGameFieldCell(position), color)
    }

    override fun isEmptyGameResult(): Boolean {
        return binding.gameResultTextview.text == ""
    }

    private fun getPieceDrawable(
        pieceType: PieceType?,
        color: PieceColor?,
        canDoMovesOverBoard: Boolean?
    ): Int {
        if (color == null || pieceType == null || canDoMovesOverBoard == null) {
            // TODO error need log
            return R.drawable.empty_cell
        }
        // TODO можно подумать, как вынести свитч по цветам фигуры например в отдельную функцию
        //  (чтобы передавать туда колбеки, которые будут вызываться по определенным значениям свича)


        return if (canDoMovesOverBoard) {
            if (color == PieceColor.white) {
                when (pieceType) {
                    PieceType.QUEEN -> R.drawable.queen_piece_white_moves_over_board
                    PieceType.ROOK -> R.drawable.rook_piece_white_moves_over_board
                    PieceType.KNIGHT -> R.drawable.knight_piece_white_moves_over_board
                    PieceType.PAWN -> R.drawable.pawn_piece_white_moves_over_board
                    PieceType.KING -> R.drawable.king_piece_white_moves_over_board
                    PieceType.BISHOP -> R.drawable.bishop_piece_white_moves_over_board
                    else -> R.drawable.empty_cell
                }
            } else {
                when (pieceType) {
                    PieceType.QUEEN -> R.drawable.queen_piece_black_moves_over_board
                    PieceType.ROOK -> R.drawable.rook_piece_black_moves_over_board
                    PieceType.KNIGHT -> R.drawable.knight_piece_black_moves_over_board
                    PieceType.PAWN -> R.drawable.pawn_piece_black_moves_over_board
                    PieceType.KING -> R.drawable.king_piece_black_moves_over_board
                    PieceType.BISHOP -> R.drawable.bishop_piece_black_moves_over_board
                    else -> R.drawable.empty_cell
                }
            }
        } else {
            if (color == PieceColor.white) {
                when (pieceType) {
                    PieceType.QUEEN -> R.drawable.queen_piece_white
                    PieceType.ROOK -> R.drawable.rook_piece_white
                    PieceType.KNIGHT -> R.drawable.knight_piece_white
                    PieceType.PAWN -> R.drawable.pawn_piece_white
                    PieceType.KING -> R.drawable.king_piece_white
                    PieceType.BISHOP -> R.drawable.bishop_piece_white
                    else -> R.drawable.empty_cell
                }
            } else {
                when (pieceType) {
                    PieceType.QUEEN -> R.drawable.queen_piece_black
                    PieceType.ROOK -> R.drawable.rook_piece_black
                    PieceType.KNIGHT -> R.drawable.knight_piece_black
                    PieceType.PAWN -> R.drawable.pawn_piece_black
                    PieceType.KING -> R.drawable.king_piece_black
                    PieceType.BISHOP -> R.drawable.bishop_piece_black
                    else -> R.drawable.empty_cell
                }
            }
        }
    }

    private fun getElementSize(): Int =
        // TODO по красивее сделать
        context?.let {
            ((ScreenUtils.getScreenWidth(it) - convertDpToPixel(
                36f,
                it
            ).toInt()) / size)
        }!!
}