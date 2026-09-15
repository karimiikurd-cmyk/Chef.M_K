package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp
import com.example.ui.theme.KurdishCrimson
import com.example.ui.theme.SaffronGold

/**
 * Modern geometric Kurdish motif border representing sun rays and textile diamond motifs.
 */
@Composable
fun KurdishMotifBorder(
    modifier: Modifier = Modifier
) {
    val gold = SaffronGold
    val crimson = KurdishCrimson
    val bg = MaterialTheme.colorScheme.surfaceVariant

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(10.dp)
            .background(bg)
    ) {
        val width = size.width
        val height = size.height
        val step = 20.dp.toPx()
        val numSteps = (width / step).toInt() + 1

        for (i in 0 until numSteps) {
            val startX = i * step
            val path = Path().apply {
                moveTo(startX, height)
                lineTo(startX + step / 2f, 0f)
                lineTo(startX + step, height)
                close()
            }
            drawPath(
                path = path,
                color = if (i % 2 == 0) gold else crimson
            )
        }

        // Top line
        drawLine(
            color = gold,
            start = Offset(0f, 0f),
            end = Offset(width, 0f),
            strokeWidth = 2.dp.toPx()
        )
    }
}
