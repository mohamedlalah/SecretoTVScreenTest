package com.secreto.tvscreentest

import android.app.Activity
import android.hardware.display.DisplayManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.Display
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Text

@Suppress("DEPRECATION")
@Composable
fun DisplayInfoScreen(activity: Activity, onBack: () -> Unit) {
    var revision by remember { mutableIntStateOf(0) }
    var appSize by remember { mutableStateOf(IntSize.Zero) }
    val manager = remember { activity.getSystemService(DisplayManager::class.java) }
    DisposableEffect(manager) {
        val listener = object : DisplayManager.DisplayListener {
            override fun onDisplayAdded(id: Int) { revision++ }
            override fun onDisplayRemoved(id: Int) { revision++ }
            override fun onDisplayChanged(id: Int) { revision++ }
        }
        manager.registerDisplayListener(listener, Handler(Looper.getMainLooper()))
        onDispose { manager.unregisterDisplayListener(listener) }
    }
    val display = remember(revision) {
        if (Build.VERSION.SDK_INT >= 30) activity.display else activity.windowManager.defaultDisplay
    }
    val mode = display?.mode
    val metrics = activity.resources.displayMetrics
    val unavailable = stringResource(R.string.unavailable)
    val hdr = display?.hdrCapabilities?.supportedHdrTypes
    val hdrNames = hdr?.toList()?.mapNotNull {
        when(it) {
            Display.HdrCapabilities.HDR_TYPE_DOLBY_VISION -> "Dolby Vision"
            Display.HdrCapabilities.HDR_TYPE_HDR10 -> "HDR10"
            Display.HdrCapabilities.HDR_TYPE_HLG -> "HLG"
            // Value 4 was introduced in API 29; do not infer support on older APIs.
            4 -> if (Build.VERSION.SDK_INT >= 29) "HDR10+" else null
            else -> null
        }
    }
    val rows = listOf(
        R.string.window_resolution to if(appSize.width > 0) stringResource(R.string.resolution_value, appSize.width, appSize.height) else unavailable,
        R.string.display_resolution to if(mode != null) stringResource(R.string.resolution_value, mode.physicalWidth, mode.physicalHeight) else unavailable,
        R.string.refresh_rate to (display?.refreshRate?.takeIf { it > 0 && it.isFinite() }?.let { stringResource(R.string.refresh_value, it) } ?: unavailable),
        R.string.density to stringResource(R.string.density_value, metrics.density, metrics.densityDpi),
        R.string.android_version to Build.VERSION.RELEASE.ifBlank { unavailable },
        R.string.manufacturer to Build.MANUFACTURER.takeUnless { it.isBlank() || it == Build.UNKNOWN }.orEmpty().ifBlank { unavailable },
        R.string.model to Build.MODEL.takeUnless { it.isBlank() || it == Build.UNKNOWN }.orEmpty().ifBlank { unavailable },
        R.string.hdr to when {
            hdr == null -> unavailable
            hdr.isEmpty() -> stringResource(R.string.hdr_none)
            hdrNames.isNullOrEmpty() -> unavailable
            else -> hdrNames.joinToString(" · ")
        }
    )
    Box(Modifier.fillMaxSize().onSizeChanged { appSize = it }) {
        DetailPage(R.string.info, onBack) {
            rows.chunked(2).forEach { pair ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    pair.forEach { (label, value) ->
                        Column(Modifier.weight(1f).background(Color(0xFF17243C), RoundedCornerShape(12.dp)).padding(14.dp)) {
                            Text(stringResource(label), color = Cyan, fontSize = 14.sp)
                            Text(value, color = Color.White, fontSize = 20.sp)
                        }
                    }
                }
            }
            Text(stringResource(R.string.info_note), color = Color(0xFFB5C4DA), fontSize = 14.sp)
        }
    }
}
