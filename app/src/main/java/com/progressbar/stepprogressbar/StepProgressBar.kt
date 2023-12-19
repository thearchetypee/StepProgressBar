package com.progressbar.stepprogressbar

import android.content.Context
import android.graphics.Matrix
import android.graphics.RectF
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

/**
 * Calculates the progress and completed steps based on the provided list of steps and the current step.
 *
 * @param list The list of steps.
 * @param current The current step.
 * @return A Pair where the first element is the number of completed steps, and the second element is the total progress.
 */
private fun getProgressAndCompletedSteps(list: List<Int>, current: Int): Pair<Int, Float> {
    // Initialize completedStep to 0
    var completedStep = 0

    // Iterate through the list of steps
    for (i in list.indices) {
        // If the current step is less than the current list element, break the loop
        if (current < list[i]) {
            break
        }
        // Update completedStep to the index + 1
        completedStep = i + 1
    }

    // Initialize totalProgress to 0
    var totalProgress = 0f

    // Calculate the progress for each step
    val stepProgress = 100F / list.size
    totalProgress = stepProgress * completedStep

    // Calculate the base of the current step
    val currentBase = if (completedStep == 0) {
        0
    } else {
        list[completedStep - 1]
    }

    // Calculate the progress within the current step
    val levelProgress = if (completedStep == list.size) {
        0
    } else {
        (current - currentBase).toFloat() / (list[completedStep] - currentBase)
    }

    // If levelProgress is not 0, update totalProgress
    if (levelProgress != 0) {
        totalProgress += (stepProgress * levelProgress.toFloat())
    }

    // Return the Pair of completed steps and total progress
    return Pair(first = completedStep, second = totalProgress)
}

@Composable
fun StepProgressIndicator(
    modifier: Modifier,
    numberOfSteps: Int,
    progress: Float,
    completedStep: Int,
    stepperSize: Int
) {
    val context = LocalContext.current

    var stepWidth by remember {
        mutableStateOf(0f)
    }

    var progressWidth by remember {
        mutableStateOf(0f)
    }

    var totalWidth by remember {
        mutableStateOf(0)
    }

    Box(
        modifier = modifier
            .onGloballyPositioned {
                val size = it.size
                stepWidth = (size.width / numberOfSteps).toFloat() - stepperSize
                progressWidth = (size.width).toFloat()
                totalWidth = size.width - (stepperSize + 4)
            }
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .fillMaxWidth()
                .height((stepperSize / 2).dp)
                .background(color = Color(0xFFDEDDD9))
                .align(alignment = Alignment.Center)
        )

        Box(
            modifier = Modifier
                .background(
                    brush = Brush.horizontalGradient(
                        listOf(
                            Color(0xFFFFC122), Color
                                (0xFFFF9213)
                        )
                    ),
                    shape = RoundedCornerShape(100.dp)
                )
                .height(((stepperSize / 2) - 4).dp)
                .width(
                    (context
                        .convertPixelToDp(progressWidth * (progress / 100))
                        .coerceAtMost(
                            context.convertPixelToDp
                                (totalWidth.toFloat())
                        )).dp
                )
                .padding(end = 16.dp)
                .align(alignment = Alignment.CenterStart),
        )

        for (i in 0..(numberOfSteps + 1)) {
            Box(
                modifier = Modifier
                    .graphicsLayer {
                        translationX = stepWidth * i
                    }
                    .clip(shape = CircleShape)
                    .size(12.dp)
                    .background(color = Color(0xFFDEDDD9))
                    .padding(2.dp)
                    .clip(CircleShape)
                    .background(
                        brush = if (i <= completedStep) Brush.horizontalGradient(
                            listOf(
                                Color(0xFFFFC122), Color
                                    (0xFFFF9213)
                            )
                        ) else Brush.horizontalGradient(
                            listOf(
                                Color(0xFFE6E5E1), Color
                                    (0xFFE6E5E1)
                            )
                        ),
                    )
            ) {

            }
        }
    }
}

fun Context.convertPixelToDp(px: Float): Float {
    val resources = this.resources
    val metrics = resources.displayMetrics
    return px * 160f / metrics.densityDpi
}