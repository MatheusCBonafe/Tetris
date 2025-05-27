package com.example.tetris.view

import android.widget.Button
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tetris.model.PieceType
import com.example.tetris.model.TetrisPiece
import com.example.tetris.ui.theme.Black80
import kotlinx.coroutines.delay
import kotlin.random.Random

const val BOARD_BORDER = 2f
const val BOARD_WIDTH = 300
const val BOARD_HEIGHT = 600
const val BLOCK_SIZE = 30
const val HORIZONTAL_BLOCKS = BOARD_WIDTH / BLOCK_SIZE
const val VERTICAL_BLOCKS = BOARD_HEIGHT / BLOCK_SIZE
const val TIMER_DELAY = 700

@Composable
fun Tetris() {

    var gameBoard by remember {
        mutableStateOf(Array(VERTICAL_BLOCKS) {
            Array(HORIZONTAL_BLOCKS) {
                Color.Black
            }
        })
    }
    var piece by remember { mutableStateOf(renderNewPiece()) }
    var score by remember { mutableIntStateOf(0) }
    var level by remember { mutableIntStateOf(0) }
    var gameOver by remember { mutableStateOf(false) }
    val context = LocalContext.current

    fun getTimerDelay(score: Int): Int {
        return when {
            score >= 2500 -> 100
            score >= 1500 -> 200
            score >= 1000 -> 300
            score >= 500 -> 500
            else -> TIMER_DELAY
        }
    }

    fun restartGame() {
        gameBoard = Array(VERTICAL_BLOCKS) { Array(HORIZONTAL_BLOCKS) { Color.Black } }
        piece = renderNewPiece()
        score = 0
        level = 1
        gameOver = false
    }

    when (score) {
        500 -> level = 2
        1000 -> level = 3
        1500 -> level = 4
        2500 -> level = 5
    }

    LaunchedEffect(Unit) {
        while (!gameOver) {
            val delayTime = getTimerDelay(score).toLong()
            delay(delayTime)
            if (movePiece(gameBoard, piece, 0, 1)) {
                piece = piece.copy(y = piece.y + 1)
            } else {
                gameBoard = lockPieceOnBoard(gameBoard, piece)
                val removeCompletedLines = removeCompletedLines(gameBoard)
                score += removeCompletedLines * 100
                piece = renderNewPiece()

                if (!movePiece(gameBoard, piece, 0, 0)) {
                    gameOver = true
                    Toast.makeText(context, "Game Over! Score: $score", Toast.LENGTH_LONG).show()
                    delay(2000)
                    restartGame()
                }
            }
        }
    }

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
                .clickable {
                    piece =
                        piece.copy(rotationIndex = (piece.rotationIndex + 1) % piece.type.shapes.size)
                    if (!movePiece(gameBoard, piece, 0, 0)) {
                        piece =
                            piece.copy(rotationIndex = (piece.rotationIndex - 1) % piece.type.shapes.size)
                    } //clickable implemented to facilitate testing using emulated devices
                }
                .pointerInput(Unit) {
                    detectDragGestures { _, dragAmount ->
                        val xDirection = (dragAmount.x / BLOCK_SIZE)
                            .toInt()
                            .coerceIn(-1, 1)
                        val yDirection = (dragAmount.y / BLOCK_SIZE)
                            .toInt()
                            .coerceIn(0, 1)

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

                    detectTapGestures {
                        piece =
                            piece.copy(rotationIndex = (piece.rotationIndex + 1) % piece.type.shapes.size)
                        if (!movePiece(gameBoard, piece, 0, 0)) {
                            piece =
                                piece.copy(rotationIndex = (piece.rotationIndex - 1) % piece.type.shapes.size)
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

                for (y in gameBoard.indices) {
                    for (x in gameBoard[y].indices) {
                        if (gameBoard[y][x] != Color.Black) {
                            drawRect(
                                color = gameBoard[y][x],
                                topLeft = Offset(
                                    x * blockSize + BOARD_BORDER / 2,
                                    y * blockSize + BOARD_BORDER / 2
                                ),
                                size = Size(blockSize - BOARD_BORDER, blockSize - BOARD_BORDER)
                            )
                        }
                    }
                }
            }
        }

        TetrisControls(
            onLeft = {
                if (movePiece(gameBoard, piece, xDirection = -1, yDirection = 0)) {
                    piece = piece.copy(x = piece.x - 1)
                }
            },
            onRight = {
                if (movePiece(gameBoard, piece, xDirection = 1, yDirection = 0)) {
                    piece = piece.copy(x = piece.x + 1)
                }
            },
            onDown = {
                if (movePiece(gameBoard, piece, xDirection = 0, yDirection = 1)) {
                    piece = piece.copy(y = piece.y + 1)
                }
            },
            onRotate = {
                val newRotation = (piece.rotationIndex + 1) % piece.type.shapes.size
                val rotated = piece.copy(rotationIndex = newRotation)
                if (movePiece(gameBoard, rotated, 0, 0)) {
                    piece = rotated
                }
            })
    }
}

@Composable
fun TetrisControls(
    onLeft: () -> Unit,
    onRight: () -> Unit,
    onDown: () -> Unit,
    onRotate: () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(onClick = onLeft) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Left")
        }
        Button(onClick = onRotate) {
            Icon(Icons.Default.Refresh, contentDescription = "Rotate")
        }
        Button(onClick = onDown) {
            Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Down")
        }
        Button(onClick = onRight) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Right")
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
                    board[newYDirection][newXDirection] != Color.Black
                ) {
                    return false
                }
            }
        }
    }
    return true
}

private fun lockPieceOnBoard(
    gameBoard: Array<Array<Color>>,
    piece: TetrisPiece
): Array<Array<Color>> {
    piece.type.shapes[piece.rotationIndex].forEachIndexed { i, row ->
        row.forEachIndexed { j, block ->
            if (block == 1)
                gameBoard[piece.y + i][piece.x + j] = piece.type.color
        }
    }
    return gameBoard
}

private fun removeCompletedLines(gameBoard: Array<Array<Color>>): Int {
    var eraseLines = 0
    for (i in gameBoard.indices.reversed()) {
        if (gameBoard[i].all { it != Color.Black }) {
            for (k in i downTo 1) {
                gameBoard[k] = gameBoard[k - 1]
            }
            gameBoard[0] = Array(HORIZONTAL_BLOCKS) { Color.Black }
            eraseLines++
        }
    }
    return eraseLines
}