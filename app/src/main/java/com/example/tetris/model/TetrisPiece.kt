package com.example.tetris.model

import androidx.compose.ui.graphics.Color
import com.example.tetris.ui.theme.CyanBlue
import com.example.tetris.ui.theme.LimeGreen
import com.example.tetris.ui.theme.NavyBlue
import com.example.tetris.ui.theme.NeonOrange
import com.example.tetris.ui.theme.Purple40
import com.example.tetris.ui.theme.Red
import com.example.tetris.ui.theme.Yellow

data class TetrisPiece(
    val type: PieceType,
    var x: Int,
    var y: Int,
    var rotationIndex: Int = 0
)

enum class PieceType(
    val shapes: Array<Array<IntArray>>,
    val color: Color
) {
    IPiece(
        arrayOf(
            arrayOf(intArrayOf(1, 1, 1, 1)), //Horizontal position
            arrayOf(intArrayOf(1), intArrayOf(1), intArrayOf(1), intArrayOf(1)) //Vertical position
        ), CyanBlue
    ),

    JPiece(
        arrayOf(
            arrayOf(
                intArrayOf(0, 0, 1),
                intArrayOf(1, 1, 1)
            ),
            arrayOf(
                intArrayOf(1, 1, 1),
                intArrayOf(1, 0, 0)
            ),
            arrayOf(
                intArrayOf(0, 1),
                intArrayOf(0, 1),
                intArrayOf(1, 1)
            )
        ), NavyBlue
    ),

    LPiece(
        arrayOf(
            arrayOf(
                intArrayOf(1, 0, 0),
                intArrayOf(1, 1, 1)
            ),
            arrayOf(
                intArrayOf(1, 1),
                intArrayOf(1, 0),
                intArrayOf(1, 0)
            ),
            arrayOf(
                intArrayOf(1, 1, 1),
                intArrayOf(0, 0, 1)
            ),
            arrayOf(
                intArrayOf(1, 0),
                intArrayOf(1, 0),
                intArrayOf(1, 1)
            )
        ), NeonOrange
    ),

    OPiece(
        arrayOf(
            arrayOf(intArrayOf(1, 1), intArrayOf(1, 1))
        ), Yellow
    ),

    SPiece(
        arrayOf(
            arrayOf(
                intArrayOf(0, 1, 1),
                intArrayOf(1, 1, 0)
            ),
            arrayOf(
                intArrayOf(1, 0),
                intArrayOf(1, 1),
                intArrayOf(0, 1)
            )
        ), LimeGreen
    ),

    TPiece(
        arrayOf(
            arrayOf(
                intArrayOf(0, 1, 0),
                intArrayOf(1, 1, 1)
            ),
            arrayOf(
                intArrayOf(1, 0),
                intArrayOf(1, 1),
                intArrayOf(1, 0)
            ),
            arrayOf(
                intArrayOf(1, 1, 1),
                intArrayOf(0, 1, 0)
            ),
            arrayOf(
                intArrayOf(0, 1),
                intArrayOf(1, 1),
                intArrayOf(0, 1)
            )
        ), Purple40
    ),

    ZPiece(
        arrayOf(
            arrayOf(
                intArrayOf(1, 1, 0),
                intArrayOf(0, 1, 1)
            ),
            arrayOf(
                intArrayOf(0, 1),
                intArrayOf(1, 1),
                intArrayOf(1, 0)
            )
        ), Red
    )
}