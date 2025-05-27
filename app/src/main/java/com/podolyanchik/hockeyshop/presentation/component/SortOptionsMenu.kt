package com.podolyanchik.hockeyshop.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.podolyanchik.hockeyshop.R
import com.podolyanchik.hockeyshop.presentation.viewmodel.SortType

@Composable
fun SortOptionsMenu(
    currentSortType: SortType,
    onSortTypeSelected: (SortType) -> Unit,
    modifier: Modifier = Modifier
) {
    var isMenuExpanded by remember { mutableStateOf(false) }
    
    // Use Surface instead of raw IconButton for better styling
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp)),
        color = MaterialTheme.colorScheme.primary,
    ) {
        IconButton(
            onClick = { isMenuExpanded = true }
        ) {
            Icon(
                imageVector = Icons.Default.Sort,
                contentDescription = stringResource(R.string.sort_by),
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
    
    DropdownMenu(
        expanded = isMenuExpanded,
        onDismissRequest = { isMenuExpanded = false },
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surface)
            .width(220.dp)
    ) {
        // Сортировка по имени (А-Я)
        DropdownMenuItem(
            text = {
                Text(
                    text = stringResource(R.string.name_a_z),
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (currentSortType == SortType.NAME_ASC) 
                        MaterialTheme.colorScheme.primary 
                    else 
                        MaterialTheme.colorScheme.onSurface
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.ArrowUpward,
                    contentDescription = null,
                    tint = if (currentSortType == SortType.NAME_ASC) 
                        MaterialTheme.colorScheme.primary 
                    else 
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            onClick = {
                onSortTypeSelected(SortType.NAME_ASC)
                isMenuExpanded = false
            }
        )
        
        // Сортировка по имени (Я-А)
        DropdownMenuItem(
            text = {
                Text(
                    text = stringResource(R.string.name_z_a),
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (currentSortType == SortType.NAME_DESC) 
                        MaterialTheme.colorScheme.primary 
                    else 
                        MaterialTheme.colorScheme.onSurface
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.ArrowDownward,
                    contentDescription = null,
                    tint = if (currentSortType == SortType.NAME_DESC) 
                        MaterialTheme.colorScheme.primary 
                    else 
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            onClick = {
                onSortTypeSelected(SortType.NAME_DESC)
                isMenuExpanded = false
            }
        )
        
        // Сортировка по цене (низкая-высокая)
        DropdownMenuItem(
            text = {
                Text(
                    text = stringResource(R.string.price_low_to_high),
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (currentSortType == SortType.PRICE_ASC) 
                        MaterialTheme.colorScheme.primary 
                    else 
                        MaterialTheme.colorScheme.onSurface
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.ArrowUpward,
                    contentDescription = null,
                    tint = if (currentSortType == SortType.PRICE_ASC) 
                        MaterialTheme.colorScheme.primary 
                    else 
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            onClick = {
                onSortTypeSelected(SortType.PRICE_ASC)
                isMenuExpanded = false
            }
        )
        
        // Сортировка по цене (высокая-низкая)
        DropdownMenuItem(
            text = {
                Text(
                    text = stringResource(R.string.price_high_to_low),
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (currentSortType == SortType.PRICE_DESC) 
                        MaterialTheme.colorScheme.primary 
                    else 
                        MaterialTheme.colorScheme.onSurface
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.ArrowDownward,
                    contentDescription = null,
                    tint = if (currentSortType == SortType.PRICE_DESC) 
                        MaterialTheme.colorScheme.primary 
                    else 
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            onClick = {
                onSortTypeSelected(SortType.PRICE_DESC)
                isMenuExpanded = false
            }
        )
        
        // Сортировка по скидке (низкая-высокая)
        DropdownMenuItem(
            text = {
                Text(
                    text = stringResource(R.string.discount_low_to_high),
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (currentSortType == SortType.DISCOUNT_ASC) 
                        MaterialTheme.colorScheme.primary 
                    else 
                        MaterialTheme.colorScheme.onSurface
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.ArrowUpward,
                    contentDescription = null,
                    tint = if (currentSortType == SortType.DISCOUNT_ASC) 
                        MaterialTheme.colorScheme.primary 
                    else 
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            onClick = {
                onSortTypeSelected(SortType.DISCOUNT_ASC)
                isMenuExpanded = false
            }
        )
        
        // Сортировка по скидке (высокая-низкая)
        DropdownMenuItem(
            text = {
                Text(
                    text = stringResource(R.string.discount_high_to_low),
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (currentSortType == SortType.DISCOUNT_DESC) 
                        MaterialTheme.colorScheme.primary 
                    else 
                        MaterialTheme.colorScheme.onSurface
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.ArrowDownward,
                    contentDescription = null,
                    tint = if (currentSortType == SortType.DISCOUNT_DESC) 
                        MaterialTheme.colorScheme.primary 
                    else 
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            onClick = {
                onSortTypeSelected(SortType.DISCOUNT_DESC)
                isMenuExpanded = false
            }
        )
        
        // Сброс сортировки
        DropdownMenuItem(
            text = {
                Text(
                    text = stringResource(R.string.no_sorting),
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (currentSortType == SortType.NONE) 
                        MaterialTheme.colorScheme.primary 
                    else 
                        MaterialTheme.colorScheme.onSurface
                )
            },
            onClick = {
                onSortTypeSelected(SortType.NONE)
                isMenuExpanded = false
            },
            modifier = Modifier.padding(top = 8.dp)
        )
    }
} 