package com.secreto.tvscreentest

import android.view.Window
import android.view.WindowManager
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.key.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.tv.material3.Text
import kotlinx.coroutines.delay

private data class Pattern(val label: Int, val color: Color = Color.Black, val kind: Int = 0)
private val primary = listOf(Pattern(R.string.red, Color.Red), Pattern(R.string.green, Color.Green), Pattern(R.string.blue, Color.Blue), Pattern(R.string.cyan, Color.Cyan), Pattern(R.string.magenta, Color.Magenta), Pattern(R.string.yellow, Color.Yellow))
private fun patterns(tool: Int): List<Pattern> = when(tool) {
    0 -> listOf(Pattern(R.string.black), Pattern(R.string.white, Color.White)) + primary
    1 -> listOf(Pattern(R.string.black), Pattern(R.string.very_dark, Color(0xFF080808)), Pattern(R.string.dark_gray, Color(0xFF202020)), Pattern(R.string.medium_gray, Color(0xFF808080)))
    2 -> primary + listOf(Pattern(R.string.white, Color.White), Pattern(R.string.bars, kind = 1))
    3 -> listOf(Pattern(R.string.grad_white, Color.White, 2), Pattern(R.string.grad_red, Color.Red, 2), Pattern(R.string.grad_green, Color.Green, 2), Pattern(R.string.grad_blue, Color.Blue, 2))
    4 -> listOf(Pattern(R.string.overscan, kind = 3))
    else -> listOf(Pattern(R.string.white, Color.White), Pattern(R.string.light_gray, Color(0xFFCCCCCC)), Pattern(R.string.gray50, Color(0xFF808080)), Pattern(R.string.dark_gray, Color(0xFF202020)))
}

@Composable
fun TestViewer(tool: Int, window: Window) {
    val entries = remember(tool) { patterns(tool) }
    var index by rememberSaveable(tool) { mutableIntStateOf(0) }
    var help by rememberSaveable(tool) { mutableStateOf(true) }
    val focus = remember { FocusRequester() }
    LaunchedEffect(Unit) { focus.requestFocus() }
    LaunchedEffect(help, index) { if (help) { delay(5000); help = false } }
    DisposableEffect(window) {
        val previousFlags = window.attributes.flags
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        val previousBehavior = controller.systemBarsBehavior
        val insets = androidx.core.view.ViewCompat.getRootWindowInsets(window.decorView)
        val statusVisible = insets?.isVisible(WindowInsetsCompat.Type.statusBars()) ?: true
        val navVisible = insets?.isVisible(WindowInsetsCompat.Type.navigationBars()) ?: true
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        controller.hide(WindowInsetsCompat.Type.systemBars())
        onDispose {
            if (previousFlags and WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON == 0) window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            controller.systemBarsBehavior = previousBehavior
            if (statusVisible) controller.show(WindowInsetsCompat.Type.statusBars()) else controller.hide(WindowInsetsCompat.Type.statusBars())
            if (navVisible) controller.show(WindowInsetsCompat.Type.navigationBars()) else controller.hide(WindowInsetsCompat.Type.navigationBars())
            WindowCompat.setDecorFitsSystemWindows(window, true)
        }
    }
    val pattern = entries[index]
    Box(Modifier.fillMaxSize().focusRequester(focus).onPreviewKeyEvent { event ->
        when(event.key) {
            Key.DirectionLeft, Key.DirectionRight -> {
                if (event.type == KeyEventType.KeyDown) index = (index + if(event.key == Key.DirectionRight) 1 else entries.size - 1) % entries.size
                true
            }
            Key.DirectionCenter, Key.Enter, Key.NumPadEnter -> {
                if (event.type == KeyEventType.KeyUp) help = !help
                true
            }
            else -> false
        }
    }.focusable()) {
        Canvas(Modifier.fillMaxSize()) {
            when(pattern.kind) {
                0 -> drawRect(pattern.color)
                1 -> {
                    val colors = listOf(Color.White, Color.Yellow, Color.Cyan, Color.Green, Color.Magenta, Color.Red, Color.Blue, Color.Black)
                    colors.forEachIndexed { i, color -> drawRect(color, Offset(size.width * i / 8, 0f), Size(size.width / 8 + 1f, size.height)) }
                }
                2 -> drawRect(Brush.horizontalGradient(listOf(Color.Black, pattern.color)))
                3 -> {
                    drawRect(Color(0xFF101010))
                    drawRect(Color.White, Offset(1f, 1f), Size(size.width - 2, size.height - 2), style = Stroke(2f))
                    drawRect(Cyan, Offset(size.width * .05f, size.height * .05f), Size(size.width * .9f, size.height * .9f), style = Stroke(2f))
                    drawLine(Color.Gray, Offset(0f, size.height / 2), Offset(size.width, size.height / 2), 1f)
                    drawLine(Color.Gray, Offset(size.width / 2, 0f), Offset(size.width / 2, size.height), 1f)
                    drawCircle(Cyan, size.minDimension * .04f, style = Stroke(2f))
                    val length = size.minDimension * .07f
                    for(x in listOf(0f, size.width)) for(y in listOf(0f, size.height)) {
                        val dx = if (x == 0f) 1 else -1
                        val dy = if (y == 0f) 1 else -1
                        drawLine(Color.Yellow, Offset(x + dx * 5f, y + dy * length), Offset(x + dx * 5f, y + dy * 5f), 4f)
                        drawLine(Color.Yellow, Offset(x + dx * 5f, y + dy * 5f), Offset(x + dx * length, y + dy * 5f), 4f)
                    }
                }
            }
        }
        if (help) Column(Modifier.align(Alignment.BottomCenter).padding(28.dp).background(Color(0xEC101B2D), RoundedCornerShape(12.dp)).padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(stringResource(tools[tool].title) + " · " + stringResource(pattern.label) + " · " + stringResource(R.string.pattern_count, index + 1, entries.size), color = Color.White, fontSize = 18.sp)
            Text(stringResource(R.string.help), color = Cyan, fontSize = 15.sp)
            if (tool == 4) Text(stringResource(R.string.safe_area), color = Color.White, fontSize = 14.sp)
        }
    }
}
