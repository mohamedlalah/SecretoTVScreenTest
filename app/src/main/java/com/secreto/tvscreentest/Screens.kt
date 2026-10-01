package com.secreto.tvscreentest

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Text

val Navy = Color(0xFF080F20)
val Cyan = Color(0xFF65DEFF)
private val Muted = Color(0xFFB5C4DA)
data class Tool(
    val title: Int,
    val subtitle: Int,
    val icon: Int
)

val tools = listOf(
    Tool(R.string.dead, R.string.dead_sub, R.drawable.ic_dead),
    Tool(R.string.backlight, R.string.backlight_sub, R.drawable.ic_backlight),
    Tool(R.string.color, R.string.color_sub, R.drawable.ic_color),
    Tool(R.string.gradient, R.string.gradient_sub, R.drawable.ic_gradient),
    Tool(R.string.overscan, R.string.overscan_sub, R.drawable.ic_overscan),
    Tool(R.string.uniformity, R.string.uniformity_sub, R.drawable.ic_uniformity),
    Tool(R.string.info, R.string.info_sub, R.drawable.ic_info),
    Tool(R.string.support, R.string.support_sub, R.drawable.ic_support)
)

@Composable
fun Page(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    listOf(
                        Navy,
                        Color(0xFF152342),
                        Color(0xFF18132F)
                    )
                )
            )
            .padding(
                horizontal = 40.dp,
                vertical = 26.dp
            ),
        content = content
    )
}

@Composable
fun HomeScreen(
    selected: Int,
    open: (Int) -> Unit,
    openAbout: () -> Unit
) {
    val requesters = remember {
        List(8) { FocusRequester() }
    }

    val aboutRequester = remember {
        FocusRequester()
    }

    LaunchedEffect(Unit) {
        requesters[selected].requestFocus()
    }

    Page {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = stringResource(R.string.app_name),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Text(
                    text = stringResource(R.string.home_subtitle),
                    fontSize = 17.sp,
                    color = Muted
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = stringResource(R.string.brand),
                        fontSize = 13.sp,
                        color = Cyan
                    )

                    Text(
                        text = stringResource(R.string.developer),
                        fontSize = 12.sp,
                        color = Muted
                    )
                }

                SmallHeaderButton(
                    modifier = Modifier
                        .focusRequester(aboutRequester)
                        .focusProperties {
                            start = FocusRequester.Cancel
                            end = FocusRequester.Cancel
                            down = requesters[3]
                        },
                    onClick = openAbout
                )
            }
        }

        Spacer(
            modifier = Modifier.height(22.dp)
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            repeat(2) { row ->

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    repeat(4) { col ->

                        val i = row * 4 + col
                        val tool = tools[i]

                        FocusTile(
                            modifier = Modifier
                                .weight(1f)
                                .height(152.dp)
                                .focusRequester(requesters[i])
                                .focusProperties {

                                    start =
                                        if (col > 0) {
                                            requesters[i - 1]
                                        } else {
                                            FocusRequester.Cancel
                                        }

                                    end =
                                        if (col < 3) {
                                            requesters[i + 1]
                                        } else {
                                            FocusRequester.Cancel
                                        }

                                    up =
                                        if (row > 0) {
                                            requesters[i - 4]
                                        } else if (i == 3) {
                                            aboutRequester
                                        } else {
                                            FocusRequester.Cancel
                                        }

                                    down =
                                        if (row < 1) {
                                            requesters[i + 4]
                                        } else {
                                            FocusRequester.Cancel
                                        }
                                },
                            onClick = {
                                open(i)
                            }
                        ) {

                            Image(
                                painter = painterResource(tool.icon),
                                contentDescription = null,
                                modifier = Modifier.size(28.dp)
                            )

                            Spacer(
                                modifier = Modifier.height(10.dp)
                            )

                            Text(
                                text = stringResource(tool.title),
                                color = Color.White,
                                fontSize = 19.sp,
                                fontWeight = FontWeight.SemiBold
                            )

                            Spacer(
                                modifier = Modifier.height(5.dp)
                            )

                            Text(
                                text = stringResource(tool.subtitle),
                                color = Muted,
                                fontSize = 13.sp,
                                lineHeight = 17.sp
                            )
                        }
                    }
                }
            }
        }

        Spacer(
            modifier = Modifier.height(14.dp)
        )

        Text(
            text = stringResource(R.string.home_hint),
            color = Cyan,
            fontSize = 13.sp
        )

        Text(
            text = stringResource(R.string.inspection_note),
            color = Muted,
            fontSize = 12.sp
        )
    }
}

@Composable
private fun SmallHeaderButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    var focused by remember {
        mutableStateOf(false)
    }

    Row(
        modifier = modifier
            .onFocusChanged {
                focused = it.isFocused
            }
            .background(
                color = if (focused) {
                    Color(0xFF244463)
                } else {
                    Color(0xFF17243C)
                },
                shape = RoundedCornerShape(20.dp)
            )
            .border(
                width = if (focused) 2.dp else 1.dp,
                color = if (focused) {
                    Cyan
                } else {
                    Color(0xFF34425D)
                },
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick)
            .padding(
                horizontal = 14.dp,
                vertical = 8.dp
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(7.dp)
    ) {

        Text(
            text = "ⓘ",
            color = Cyan,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = stringResource(R.string.about),
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun FocusTile(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    var focused by remember {
        mutableStateOf(false)
    }

    Column(
        modifier
            .onFocusChanged {
                focused = it.isFocused
            }
            .background(
                color = if (focused) {
                    Color(0xFF244463)
                } else {
                    Color(0xFF17243C)
                },
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                width = if (focused) 3.dp else 1.dp,
                color = if (focused) {
                    Cyan
                } else {
                    Color(0xFF34425D)
                },
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .padding(16.dp),
        content = content
    )
}

@Composable
fun DetailPage(
    title: Int,
    onBack: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    val backFocus = remember {
        FocusRequester()
    }

    val scroll = rememberScrollState()

    LaunchedEffect(Unit) {
        backFocus.requestFocus()
    }

    Page {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            FocusTile(
                modifier = Modifier.focusRequester(backFocus),
                onClick = onBack
            ) {
                Text(
                    text = stringResource(R.string.back),
                    color = Color.White,
                    fontSize = 16.sp
                )
            }

            Text(
                text = stringResource(title),
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(
            modifier = Modifier.height(18.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(scroll),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            content = content
        )
    }
}

@Composable
fun SupportScreen(
    onBack: () -> Unit
) {
    DetailPage(
        title = R.string.support_title,
        onBack = onBack
    ) {
        DistributionSupportContent()
    }
}

@Composable
fun AboutScreen(
    onBack: () -> Unit
) {
    DetailPage(
        R.string.about,
        onBack
    ) {

        Image(
            painter = painterResource(R.drawable.ic_secreto),
            contentDescription = null,
            modifier = Modifier.size(68.dp)
        )

        Text(
            text = stringResource(R.string.app_name),
            color = Color.White,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = stringResource(
                R.string.version,
                "1.0.0"
            ),
            color = Cyan,
            fontSize = 18.sp
        )

        Text(
            text = stringResource(R.string.developed_by),
            color = Color.White,
            fontSize = 20.sp
        )

        Text(
            text = stringResource(R.string.brand),
            color = Cyan,
            fontSize = 18.sp
        )

        Text(
            text = stringResource(R.string.about_description),
            color = Muted,
            fontSize = 17.sp
        )

        Text(
            text = stringResource(R.string.privacy),
            color = Muted,
            fontSize = 17.sp
        )

        Text(
            text = stringResource(R.string.inspection_note),
            color = Muted,
            fontSize = 15.sp
        )
    }
}