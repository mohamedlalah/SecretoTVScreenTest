package com.secreto.tvscreentest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.secreto.tvscreentest.ui.theme.SecretoTVScreenTestTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_SecretoTVScreenTest)
        super.onCreate(savedInstanceState)

        setContent {
            SecretoTVScreenTestTheme {

                var showSplash by rememberSaveable {
                    mutableStateOf(savedInstanceState == null)
                }

                if (showSplash) {

                    LaunchedEffect(Unit) {
                        delay(1500L)
                        showSplash = false
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFF080F20)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(
                                id = R.drawable.splash_artwork
                            ),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    }

                } else {

                    var screen by rememberSaveable {
                        mutableIntStateOf(-1)
                    }

                    var lastTool by rememberSaveable {
                        mutableIntStateOf(0)
                    }

                    val home = {
                        screen = -1
                    }

                    BackHandler(
                        enabled = screen != -1,
                        onBack = home
                    )

                    when (screen) {

                        -1 -> HomeScreen(
                            selected = lastTool,
                            open = {
                                lastTool = it
                                screen = it
                            },
                            openAbout = {
                                screen = 8
                            }
                        )

                        in 0..5 -> TestViewer(
                            screen,
                            window
                        )

                        6 -> DisplayInfoScreen(
                            this,
                            home
                        )

                        7 -> SupportScreen(
                            home
                        )

                        8 -> AboutScreen(
                            home
                        )

                        else -> {
                            screen = -1
                        }
                    }
                }
            }
        }
    }
}