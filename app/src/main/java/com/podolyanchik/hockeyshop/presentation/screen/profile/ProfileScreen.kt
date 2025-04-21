package com.podolyanchik.hockeyshop.presentation.screen.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.podolyanchik.hockeyshop.R
import com.podolyanchik.hockeyshop.presentation.component.ErrorDialog
import com.podolyanchik.hockeyshop.presentation.component.HockeyShopButton
import com.podolyanchik.hockeyshop.presentation.component.HockeyShopTextField
import com.podolyanchik.hockeyshop.presentation.viewmodel.ProfileViewModel
import com.podolyanchik.hockeyshop.util.Resource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val updateState by viewModel.updateState.collectAsStateWithLifecycle()
    
    var isEditing by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var paymentMethod by remember { mutableStateOf("") }
    
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    
    // Initialize form fields when currentUser changes
    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            name = user.name
            email = user.email
            phone = user.phone ?: ""
            address = user.address ?: ""
            paymentMethod = user.paymentMethod ?: ""
        }
    }
    
    // Handle update state changes
    LaunchedEffect(updateState) {
        when (updateState) {
            is Resource.Success -> {
                isEditing = false
                viewModel.resetState()
            }
            is Resource.Error -> {
                errorMessage = (updateState as Resource.Error).message ?: "Failed to update profile"
                showErrorDialog = true
            }
            else -> {}
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.profile)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                actions = {
                    if (!isEditing) {
                        IconButton(onClick = { isEditing = true }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = stringResource(R.string.edit_profile)
                            )
                        }
                    } else {
                        IconButton(
                            onClick = {
                                viewModel.updateProfile(
                                    name = name,
                                    email = email,
                                    phone = phone,
                                    address = address,
                                    paymentMethod = paymentMethod
                                )
                            },
                            enabled = name.isNotBlank() && email.isNotBlank()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Save,
                                contentDescription = stringResource(R.string.save)
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // User Role
            currentUser?.let { user ->
                Text(
                    text = "Role: ${if (user.role.name == "ADMIN") "Administrator" else "User"}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            // Name Field
            HockeyShopTextField(
                value = name,
                onValueChange = { name = it },
                label = stringResource(R.string.name),
                enabled = isEditing,
                modifier = Modifier.fillMaxWidth()
            )
            
            // Email Field
            HockeyShopTextField(
                value = email,
                onValueChange = { email = it },
                label = stringResource(R.string.email),
                enabled = isEditing,
                modifier = Modifier.fillMaxWidth()
            )
            
            // Phone Field
            HockeyShopTextField(
                value = phone,
                onValueChange = { phone = it },
                label = stringResource(R.string.phone),
                enabled = isEditing,
                modifier = Modifier.fillMaxWidth()
            )
            
            // Address Field
            HockeyShopTextField(
                value = address,
                onValueChange = { address = it },
                label = stringResource(R.string.address),
                enabled = isEditing,
                modifier = Modifier.fillMaxWidth()
            )
            
            // Payment Method Field
            HockeyShopTextField(
                value = paymentMethod,
                onValueChange = { paymentMethod = it },
                label = stringResource(R.string.payment_method),
                enabled = isEditing,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Logout Button
            HockeyShopButton(
                text = stringResource(R.string.logout),
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth(),
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError
            )
        }
    }
    
    // Error Dialog
    if (showErrorDialog) {
        ErrorDialog(
            message = errorMessage,
            onDismiss = {
                showErrorDialog = false
                errorMessage = ""
            }
        )
    }
} 