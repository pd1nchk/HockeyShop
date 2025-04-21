package com.podolyanchik.hockeyshop.presentation.screen.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.podolyanchik.hockeyshop.R
import com.podolyanchik.hockeyshop.presentation.component.ErrorDialog
import com.podolyanchik.hockeyshop.presentation.component.HockeyShopButton
import com.podolyanchik.hockeyshop.presentation.component.HockeyShopTextField
import com.podolyanchik.hockeyshop.presentation.viewmodel.AuthViewModel
import com.podolyanchik.hockeyshop.util.Resource

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val loginState by viewModel.loginState.collectAsStateWithLifecycle()
    
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    
    val focusManager = LocalFocusManager.current
    
    LaunchedEffect(loginState) {
        when (loginState) {
            is Resource.Success -> {
                onNavigateToHome()
                viewModel.resetState()
            }
            is Resource.Error -> {
                errorMessage = loginState.message ?: ""
                showErrorDialog = true
            }
            else -> {}
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.login),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        HockeyShopTextField(
            value = email,
            onValueChange = { email = it },
            label = stringResource(R.string.email),
            leadingIcon = Icons.Default.Email,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            )
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        HockeyShopTextField(
            value = password,
            onValueChange = { password = it },
            label = stringResource(R.string.password),
            leadingIcon = Icons.Default.Lock,
            isPassword = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() }
            )
        )
        
        TextButton(
            onClick = onNavigateToForgotPassword,
            modifier = Modifier
                .align(Alignment.End)
                .padding(top = 4.dp)
        ) {
            Text(text = stringResource(R.string.forgot_password))
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        HockeyShopButton(
            text = stringResource(R.string.login),
            onClick = {
                focusManager.clearFocus()
                viewModel.login(email, password)
            },
            isLoading = loginState is Resource.Loading
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = stringResource(R.string.no_account),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        
        TextButton(
            onClick = {
                viewModel.resetState()
                onNavigateToRegister()
            }
        ) {
            Text(text = stringResource(R.string.register))
        }
    }
    
    if (showErrorDialog) {
        ErrorDialog(
            message = errorMessage,
            onDismiss = { showErrorDialog = false }
        )
    }
} 