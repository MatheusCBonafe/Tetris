package com.example.tetris.view

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tetris.model.PieceType
import com.example.tetris.model.TetrisPiece
import com.example.tetris.ui.theme.Black80
import kotlin.random.Random

const val BOARD_BORDER = 2f
const val BOARD_WIDTH = 300
const val BOARD_HEIGHT = 600
const val BLOCK_SIZE = 30
const val HORIZONTAL_BLOCKS = BOARD_WIDTH / BLOCK_SIZE
const val VERTICAL_BLOCKS = BOARD_HEIGHT / BLOCK_SIZE

@Composable
fun Tetris() {

    var gameBoard by remember {
        mutableStateOf(Array(300) {
            Array(600) {
                Color.Black
            }
        })
    }
    var piece by remember { mutableStateOf(renderNewPiece()) }
    var score by remember { mutableIntStateOf(0) }
    var level by remember { mutableIntStateOf(0) }
    var gameOver by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Black80),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Level: $level",
            fontSize = 18.sp,
            color = Color.White,
            modifier = Modifier.padding(bottom = 20.dp, start = 20.dp, end = 20.dp)
        )

        Text(
            text = buildAnnotatedString {
                append("Score: ")
                withStyle(
                    style = SpanStyle(
                        color = Color.White
                    )
                ) {
                    append("$score")
                }
            }, fontSize = 18.sp, color = Color.Green, modifier = Modifier.padding(10.dp)
        )

        Box(
            modifier = Modifier
                .width(300.dp)
                .height(600.dp)
                .border(2.dp, Color.White)
                .background(Color.Gray)
                .pointerInput(Unit) {
                    detectDragGestures { _, dragAmount ->
                        val xDirection = (dragAmount.x / BLOCK_SIZE).toInt().coerceIn(-1, 1)
                        val yDirection = (dragAmount.y / BLOCK_SIZE).toInt().coerceIn(0, 1)

                        if (!gameOver) {
                            if (xDirection != 0) {
                                if (movePiece(gameBoard, piece, xDirection, 0)) {
                                    piece = piece.copy(x = piece.x + xDirection)
                                }
                            }
                            if (yDirection > 0) {
                                if (movePiece(gameBoard, piece, xDirection = 0, yDirection)) {
                                    piece = piece.copy(y = piece.y + yDirection)
                                }
                            }
                        }
                    }
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasWidth = size.width
                val canvasHeight = size.height
                val blockSize =
                    (canvasWidth / HORIZONTAL_BLOCKS).coerceAtMost(canvasHeight / VERTICAL_BLOCKS)

                val canvasBoardWidth = (canvasWidth / blockSize).toInt()
                val canvasBoardHeight = (canvasHeight / blockSize).toInt()

                drawRect(color = Color.Black, size = Size(canvasWidth, canvasHeight))

                for (x in 0 until canvasBoardWidth) {
                    drawLine(
                        color = Color.White,
                        start = Offset(x * blockSize, 0f),
                        end = Offset(x * blockSize, canvasHeight)
                    )
                }

                for (y in 0 until canvasBoardHeight) {
                    drawLine(
                        color = Color.White,
                        start = Offset(0f, y * blockSize),
                        end = Offset(canvasWidth, y * blockSize)
                    )
                }

                val pieceForm = piece.type.shapes[piece.rotationIndex]
                pieceForm.forEachIndexed { i, row ->
                    row.forEachIndexed { j, block ->
                        if (block == 1) {
                            drawRect(
                                color = piece.type.color, topLeft = Offset(
                                    (piece.x + j) * blockSize + BOARD_BORDER / 2,
                                    (piece.y + i) * blockSize + BOARD_BORDER / 2
                                ), size = Size(blockSize - BOARD_BORDER, blockSize - BOARD_BORDER)
                            )
                        }
                    }
                }
            }
        }

    }
}

private fun renderNewPiece(): TetrisPiece {
    val pieceType = PieceType.entries[Random.nextInt(PieceType.entries.size)]
    return TetrisPiece(type = pieceType, HORIZONTAL_BLOCKS / 2 - pieceType.shapes[0][0].size / 2, 0)
}

private fun movePiece(
    board: Array<Array<Color>>, piece: TetrisPiece, xDirection: Int, yDirection: Int
): Boolean {
    piece.type.shapes[piece.rotationIndex].forEachIndexed { i, row ->
        row.forEachIndexed { j, block ->
            if (block == 1) {
                val newXDirection = piece.x + j + xDirection
                val newYDirection = piece.y + i + yDirection
                if (
                    newXDirection < 0 ||
                    newXDirection >= HORIZONTAL_BLOCKS ||
                    newYDirection >= VERTICAL_BLOCKS ||
                    board[newXDirection][newYDirection] != Color.Black
                ) {
                    return false
                }
            }
        }
    }
    return true
}