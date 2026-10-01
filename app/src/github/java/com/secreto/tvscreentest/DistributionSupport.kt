package com.secreto.tvscreentest

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Text

private val YouTubeRed = Color(0xFFFF0033)

@Composable
fun DistributionSupportContent() {

    Text(
        text = stringResource(R.string.support_message),
        color = Color(0xFFB5C4DA),
        fontSize = 17.sp,
        lineHeight = 23.sp
    )

    Spacer(
        modifier = Modifier.height(4.dp)
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(30.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(22.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Image(
                painter = painterResource(
                    R.drawable.secretofnet_channel
                ),
                contentDescription = null,
                modifier = Modifier
                    .size(112.dp)
                    .border(
                        width = 2.dp,
                        color = Cyan,
                        shape = CircleShape
                    ),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(9.dp)
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {

                    Box(
                        modifier = Modifier
                            .background(
                                color = YouTubeRed,
                                shape = RoundedCornerShape(7.dp)
                            )
                            .padding(
                                horizontal = 10.dp,
                                vertical = 5.dp
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "▶",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = stringResource(
                            R.string.youtube_membership
                        ),
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = stringResource(
                        R.string.youtube_membership_sub
                    ),
                    color = Cyan,
                    fontSize = 17.sp
                )

                Text(
                    text = stringResource(
                        R.string.scan_qr
                    ),
                    color = Color(0xFFB5C4DA),
                    fontSize = 15.sp,
                    lineHeight = 20.sp
                )

                Spacer(
                    modifier = Modifier.height(3.dp)
                )

                Text(
                    text = stringResource(
                        R.string.support_optional
                    ),
                    color = Color(0xFFB5C4DA),
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }
        }

        Box(
            modifier = Modifier
                .size(230.dp)
                .background(
                    Color.White,
                    RoundedCornerShape(14.dp)
                )
                .padding(10.dp),
            contentAlignment = Alignment.Center
        ) {

            Image(
                painter = painterResource(
                    R.drawable.youtube_membership_qr
                ),
                contentDescription = stringResource(
                    R.string.youtube_membership
                ),
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        }
    }
}