package com.podolyanchik.hockeyshop.presentation.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun PasswordVisibilityToggle(
    passwordVisible: Boolean,
    onToggleClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onToggleClick,
        modifier = modifier
    ) {
        Icon(
            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
            contentDescription = if (passwordVisible) "Скрыть пароль" else "Показать пароль"
        )
    }
} 