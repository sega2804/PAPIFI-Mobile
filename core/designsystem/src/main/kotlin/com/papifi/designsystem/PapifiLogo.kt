package com.papifi.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PapifiLogo(
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    backgroundColor: Color = PapifiPrimary,
    contentColor: Color = Color.White
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "P",
            color = contentColor,
            fontWeight = FontWeight.ExtraBold,
            fontSize = (size.value * 0.52).sp
        )
    }
}

@Composable
fun PapifiWordmark(
    modifier: Modifier = Modifier,
    size: Dp = 36.dp,
    textColor: Color = PapifiPrimary
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        PapifiLogo(size = size)
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = "PAPIFI",
            fontSize = (size.value * 0.5).sp,
            fontWeight = FontWeight.Black,
            color = textColor,
            letterSpacing = 2.sp
        )
    }
}
