package com.secreto.tvscreentest

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.tv.material3.Text

private const val SUPPORT_URL =
    "https://secretotools.com/products/secreto-tv-screen-test/support/"

private const val PRIVACY_URL =
    "https://secretotools.com/products/secreto-tv-screen-test/privacy/"

@Composable
fun DistributionSupportContent() {

    val context = LocalContext.current

    val supportFocusRequester = remember {
        FocusRequester()
    }

    val privacyFocusRequester = remember {
        FocusRequester()
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        Text(
            text = stringResource(R.string.support_message),
            color = Color(0xFFB5C4DA),
            fontSize = 17.sp,
            lineHeight = 23.sp
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        SupportWebsiteButton(
            modifier = Modifier
                .focusRequester(supportFocusRequester)
                .focusProperties {
                    down = privacyFocusRequester
                },
            label = stringResource(R.string.open_support),
            icon = "?"
        ) {
            openExternalUrl(
                context = context,
                url = SUPPORT_URL
            )
        }

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        SupportWebsiteButton(
            modifier = Modifier
                .focusRequester(privacyFocusRequester)
                .focusProperties {
                    up = supportFocusRequester
                    down = FocusRequester.Cancel
                },
            label = stringResource(R.string.privacy_policy),
            icon = "i"
        ) {
            openExternalUrl(
                context = context,
                url = PRIVACY_URL
            )
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text = "SecretoTools • Mohamed LALAH",
            color = Cyan,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun SupportWebsiteButton(
    modifier: Modifier = Modifier,
    label: String,
    icon: String,
    onClick: () -> Unit
) {

    var focused by remember {
        mutableStateOf(false)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .onFocusChanged {
                focused = it.isFocused
            }
            .background(
                color = if (focused) {
                    Color(0xFF244463)
                } else {
                    Color(0xFF17243C)
                },
                shape = RoundedCornerShape(14.dp)
            )
            .border(
                width = if (focused) 3.dp else 1.dp,
                color = if (focused) {
                    Cyan
                } else {
                    Color(0xFF34425D)
                },
                shape = RoundedCornerShape(14.dp)
            )
            .clickable(onClick = onClick)
            .focusable()
            .padding(
                horizontal = 24.dp,
                vertical = 16.dp
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {

        Text(
            text = icon,
            color = Cyan,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = label,
            color = Color.White,
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

private fun openExternalUrl(
    context: Context,
    url: String
) {
    try {
        val intent = Intent(
            Intent.ACTION_VIEW,
            url.toUri()
        ).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        context.startActivity(intent)

    } catch (_: ActivityNotFoundException) {
        // No compatible browser or external URL handler is installed.
    }
}