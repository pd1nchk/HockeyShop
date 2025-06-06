package com.podolyanchik.hockeyshop.presentation.screen.admin

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.podolyanchik.hockeyshop.R
import com.podolyanchik.hockeyshop.domain.model.Category
import com.podolyanchik.hockeyshop.domain.model.Product
import androidx.compose.foundation.layout.Row
import com.podolyanchik.hockeyshop.util.ImageUtil
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditProductDialog(
    product: Product? = null,
    categories: List<Category>,
    onDismiss: () -> Unit,
    onConfirm: (
        id: String,
        name: String,
        description: String,
        price: Double,
        imageUrl: String,
        categoryId: String,
        quantity: Int,
        discount: Float,
        isPopular: Boolean,
        isNew: Boolean
    ) -> Unit
) {
    val isEditing = product != null
    val title = stringResource(if (isEditing) R.string.edit_product else R.string.add_product)
    
    var productName by remember { mutableStateOf(product?.name ?: "") }
    var productDescription by remember { mutableStateOf(product?.description ?: "") }
    var productPrice by remember { mutableStateOf(product?.price?.toString() ?: "") }
    var productImageUrl by remember { mutableStateOf(product?.imageUrl ?: "") }
    var productQuantity by remember { mutableStateOf(product?.quantity?.toString() ?: "") }
    var productDiscount by remember { mutableStateOf(product?.discount?.toString() ?: "0") }
    var isPopular by remember { mutableStateOf(product?.isPopular ?: false) }
    var isNew by remember { mutableStateOf(product?.isNew ?: true) }
    
    // Category dropdown
    var expanded by remember { mutableStateOf(false) }
    var selectedCategory by remember { 
        mutableStateOf(
            categories.find { it.id == product?.category?.id } ?: 
            categories.firstOrNull()
        ) 
    }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 8.dp)
            ) {
                OutlinedTextField(
                    value = productName,
                    onValueChange = { productName = it },
                    label = { Text(stringResource(R.string.product_name)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = productDescription,
                    onValueChange = { productDescription = it },
                    label = { Text(stringResource(R.string.product_description)) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = productPrice,
                    onValueChange = { productPrice = it },
                    label = { Text(stringResource(R.string.product_price)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = productQuantity,
                    onValueChange = { productQuantity = it },
                    label = { Text(stringResource(R.string.product_quantity)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = productDiscount,
                    onValueChange = { productDiscount = it },
                    label = { Text(stringResource(R.string.product_discount)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = productImageUrl,
                    onValueChange = { 
                        // Убираем префикс @ если пользователь его добавил
                        val cleanUrl = if (it.startsWith("@")) it.substring(1) else it
                        productImageUrl = cleanUrl
                    },
                    label = { Text(stringResource(R.string.product_image_url)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    supportingText = { Text(stringResource(R.string.image_url_hint)) }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Category dropdown
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        readOnly = true,
                        value = selectedCategory?.name ?: stringResource(R.string.select_category),
                        onValueChange = { },
                        label = { Text(stringResource(R.string.product_category)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        categories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category.name) },
                                onClick = {
                                    selectedCategory = category
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Checkboxes for isPopular and isNew
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = isPopular,
                        onCheckedChange = { isPopular = it }
                    )
                    Text(stringResource(R.string.product_popular))
                }
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = isNew,
                        onCheckedChange = { isNew = it }
                    )
                    Text(stringResource(R.string.product_new))
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    // Обрабатываем URL перед сохранением
                    val processedImageUrl = ImageUtil.normalizeImageUrl(productImageUrl)
                    
                    onConfirm(
                        product?.id ?: "",
                        productName,
                        productDescription,
                        productPrice.toDoubleOrNull() ?: 0.0,
                        processedImageUrl,
                        selectedCategory?.id.toString(),
                        productQuantity.toIntOrNull() ?: 0,
                        productDiscount.toFloatOrNull() ?: 0f,
                        isPopular,
                        isNew
                    )
                },
                enabled = productName.isNotBlank() && 
                         productDescription.isNotBlank() && 
                         productPrice.toDoubleOrNull() != null && 
                         productQuantity.toIntOrNull() != null &&
                         categories.isNotEmpty()
            ) {
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(stringResource(R.string.cancel))
            }
        },
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
    )
} 