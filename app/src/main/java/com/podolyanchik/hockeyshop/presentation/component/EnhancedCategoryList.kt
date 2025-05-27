package com.podolyanchik.hockeyshop.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.podolyanchik.hockeyshop.R
import com.podolyanchik.hockeyshop.domain.model.Category

@Composable
fun EnhancedCategoryList(
    categories: List<Category>,
    onCategorySelected: (Int?) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(R.string.all_categories),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        
        // Фон для списка категорий
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                .padding(12.dp)
        ) {
            LazyRow(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Категория "Все"
                item {
                    CategoryItem(
                        categoryName = stringResource(R.string.all_category_item),
                        onClick = { onCategorySelected(null) }
                    )
                }
                
                // Остальные категории
                items(categories) { category ->
                    CategoryItem(
                        categoryName = category.name,
                        onClick = { onCategorySelected(category.id) }
                    )
                }
            }
        }
    }
} 