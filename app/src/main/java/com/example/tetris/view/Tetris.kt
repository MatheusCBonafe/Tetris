package com.example.tetris.view

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tetris.ui.theme.Black80

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
    var score by remember { mutableIntStateOf(0) }
    var level by remember { mutableIntStateOf(0) }

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
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasWidth = size.width
                val canvasHeight = size.height
                val blockSize =
                    (canvasWidth / HORIZONTAL_BLOCKS).coerceAtMost(canvasHeight / VERTICAL_BLOCKS)

                val canvasBoardWidth = (canvasWidth / blockSize).toInt()
                val canvasBoardHeight = (canvasHeight / blockSize).toInt()

                println("Block Size: $blockSize, Canvas Board Width: $canvasBoardWidth, Canvas Board Height: $canvasBoardHeight")
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
            }
        }

    }
}