package com.podolyanchik.hockeyshop.presentation.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.StarHalf
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.ceil
import kotlin.math.floor

@Composable
fun RatingBar(
    rating: Float,
    maxRating: Int = 5,
    showRatingNumber: Boolean = true,
    starSize: Float = 16f,
    starColor: Color = Color(0xFFFFC107),
    modifier: Modifier = Modifier
) {
    val filledStars = floor(rating).toInt()
    val halfStar = (rating - filledStars) >= 0.5
    val emptyStars = maxRating - filledStars - (if (halfStar) 1 else 0)
    
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Заполненные звезды
        repeat(filledStars) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = starColor,
                modifier = Modifier.size(starSize.dp)
            )
        }
        
        // Полузаполненная звезда (если есть)
        if (halfStar) {
            Icon(
                imageVector = Icons.Default.StarHalf,
                contentDescription = null,
                tint = starColor,
                modifier = Modifier.size(starSize.dp)
            )
        }
        
        // Пустые звезды
        repeat(emptyStars) {
            Icon(
                imageVector = Icons.Default.StarBorder,
                contentDescription = null,
                tint = starColor,
                modifier = Modifier.size(starSize.dp)
            )
        }
        
        // Отображение числового значения рейтинга
        if (showRatingNumber) {
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = String.format("%.1f", rating),
                style = MaterialTheme.typography.bodySmall,
                fontSize = (starSize - 2).sp
            )
        }
    }
}