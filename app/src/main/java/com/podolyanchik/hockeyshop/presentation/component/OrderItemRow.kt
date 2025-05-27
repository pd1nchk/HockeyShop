package com.podolyanchik.hockeyshop.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.podolyanchik.hockeyshop.R
import com.podolyanchik.hockeyshop.domain.model.OrderItem

@Composable
fun OrderItemRow(
    orderItem: OrderItem,
    modifier: Modifier = Modifier
) {
    val product = orderItem.product
    val quantity = orderItem.quantity
    val price = orderItem.pricePerItem
    
    // Форматируем цену безопасно
    val formattedPriceWithDiscount = remember(product.price, product.discount, price) {
        try {
            if (product.discount > 0) {
                "$${String.format("%.2f", product.price)} (-${String.format("%.0f", product.discount)}%) $${String.format("%.2f", price)}"
            } else {
                "$${String.format("%.2f", price)}"
            }
        } catch (e: Exception) {
            "$${String.format("%.2f", price)}"
        }
    }
    
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Product image with fallback
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                if (product.imageUrl.isNullOrBlank()) {
                    // Fallback if no image available
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = product.name.firstOrNull()?.toString() ?: "?",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    AsyncImage(
                        model = product.imageUrl,
                        contentDescription = product.name,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Product details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = formattedPriceWithDiscount,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Quantity
                Text(
                    text = stringResource(R.string.quantity_value, quantity),
                    style = MaterialTheme.typography.bodySmall
                )
            }
            
            // Total price for this item
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = stringResource(R.string.subtotal),
                    style = MaterialTheme.typography.bodySmall
                )
                
                Text(
                    text = "$${String.format("%.2f", price * quantity)}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
} 