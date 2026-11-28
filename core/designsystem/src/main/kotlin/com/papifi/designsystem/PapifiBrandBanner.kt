package com.papifi.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PapifiBrandBanner(
    modifier: Modifier = Modifier,
    backgroundColor: Color = PapifiPrimary,
    contentColor: Color = Color.White
) {
    // Exact proportion from Figma / XML specs (193.71dp x 134dp -> ratio ~1.445)
    Box(
        modifier = modifier
            .width(193.71.dp)
            .height(134.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "P",
                    color = contentColor,
                    fontWeight = FontWeight.Black,
                    fontSize = 32.sp
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "PAPIFI",
                color = contentColor,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp,
                letterSpacing = 3.sp
            )
        }
    }
}
