package com.apexsense.pro.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apexsense.pro.presentation.theme.AccentOrange

@Composable
fun PageHeader(title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Left: Title
        Text(
            text = title,
            color = Color.White,
            fontSize = 32.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = (-1).sp
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            // Middle: Credits
            Text(
                text = "By Bara444",
                color = Color.Gray,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Right: ID Pill
            // Box(
            //     modifier = Modifier
            //         .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
            //         .padding(horizontal = 12.dp, vertical = 6.dp)
            // ) {
            //     Text(
            //         text = "ID Developer",
            //         color = AccentOrange.copy(alpha = 0.8f),
            //         fontSize = 12.sp,
            //         fontWeight = FontWeight.Bold,
            //         letterSpacing = 1.sp
            //     )
            // }
        }
    }
}

@Composable
fun CopyrightFooter() {
    Column(
            modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
                text = "© 2026 APEXSENSE",
                color = Color.White.copy(alpha = 0.3f),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
        )
        Text(
                text = "CRAFTED FOR COMPETITIVE PLAYERS",
                color = AccentOrange.copy(alpha = 0.3f),
                fontSize = 8.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 1.sp
        )
    }
}
