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
import androidx.compose.ui.text.input.PasswordVisualTransformation
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
    val passwordChangeState by viewModel.passwordChangeState.collectAsStateWithLifecycle()
    
    var isEditing by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var paymentMethod by remember { mutableStateOf("") }
    var selectedPaymentMethod by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }
    
    // Поля для смены пароля
    var showPasswordChangeForm by remember { mutableStateOf(false) }
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmNewPassword by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf<String?>(null) }
    
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
            selectedPaymentMethod = user.paymentMethod
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
    
    // Обработка состояния изменения пароля
    LaunchedEffect(passwordChangeState) {
        when (passwordChangeState) {
            is Resource.Success -> {
                showPasswordChangeForm = false
                currentPassword = ""
                newPassword = ""
                confirmNewPassword = ""
                passwordError = null
                viewModel.resetPasswordChangeState()
            }
            is Resource.Error -> {
                passwordError = (passwordChangeState as Resource.Error).message
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
                                // Для администраторов сохраняем существующие значения адреса и метода оплаты
                                val addressToSave = if (currentUser?.role?.name == "ADMIN") currentUser?.address ?: "" else address
                                val paymentMethodToSave = if (currentUser?.role?.name == "ADMIN") currentUser?.paymentMethod ?: "" else paymentMethod
                                
                                viewModel.updateProfile(
                                    name = name,
                                    email = email,
                                    phone = phone,
                                    address = addressToSave,
                                    paymentMethod = paymentMethodToSave
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
            // User Role with better formatting
            currentUser?.let { user ->
                val roleText = if (user.role.name == "ADMIN") {
                    stringResource(R.string.admin)
                } else {
                    stringResource(R.string.user)
                }
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (user.role.name == "ADMIN") 
                            MaterialTheme.colorScheme.primaryContainer 
                        else 
                            MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = stringResource(R.string.user_type),
                            style = MaterialTheme.typography.labelLarge
                        )
                        Text(
                            text = roleText,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
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
                onValueChange = { 
                    // Валидация: допускаем только цифры и "+"
                    if (it.all { char -> char.isDigit() || char == '+' }) {
                        phone = it
                        
                        // Проверяем формат телефона
                        if (phone.isNotEmpty()) {
                            if (phone.length < 7) {
                                phoneError = "Телефон должен содержать не менее 7 цифр"
                            } else {
                                phoneError = null
                            }
                        } else {
                            phoneError = null
                        }
                    }
                },
                label = stringResource(R.string.phone),
                enabled = isEditing,
                modifier = Modifier.fillMaxWidth(),
                isError = phoneError != null,
                errorMessage = phoneError
            )
            
            // Address Field - только для обычных пользователей
            if (currentUser?.role?.name != "ADMIN") {
                HockeyShopTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = stringResource(R.string.address),
                    enabled = isEditing,
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Payment Method Selection - только для обычных пользователей
                Text(
                    text = stringResource(R.string.payment_method),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 16.dp)
                )
                
                if (isEditing) {
                    // Radio buttons for payment method selection
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            RadioButton(
                                selected = selectedPaymentMethod == "Кредитная карта",
                                onClick = { 
                                    selectedPaymentMethod = "Кредитная карта"
                                    paymentMethod = "Кредитная карта"
                                },
                                enabled = isEditing
                            )
                            Text(
                                text = stringResource(R.string.credit_card),
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            RadioButton(
                                selected = selectedPaymentMethod == "Наличные курьеру",
                                onClick = { 
                                    selectedPaymentMethod = "Наличные курьеру"
                                    paymentMethod = "Наличные курьеру" 
                                },
                                enabled = isEditing
                            )
                            Text(
                                text = stringResource(R.string.cash_on_delivery),
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                } else {
                    // Show selected payment method when not editing
                    Text(
                        text = paymentMethod.ifEmpty { "Не указан" },
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
            
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            
            // Кнопка смены пароля
            if (!showPasswordChangeForm) {
                HockeyShopButton(
                    text = stringResource(R.string.change_password),
                    onClick = { showPasswordChangeForm = true },
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            } else {
                // Форма смены пароля
                Text(
                    text = stringResource(R.string.change_password),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                // Текущий пароль
                HockeyShopTextField(
                    value = currentPassword,
                    onValueChange = { currentPassword = it },
                    label = stringResource(R.string.old_password),
                    isPassword = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Новый пароль
                HockeyShopTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = stringResource(R.string.new_password),
                    isPassword = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Подтверждение нового пароля
                HockeyShopTextField(
                    value = confirmNewPassword,
                    onValueChange = { confirmNewPassword = it },
                    label = stringResource(R.string.confirm_password),
                    isPassword = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Сообщение об ошибке
                passwordError?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                
                // Кнопки действий
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { 
                            showPasswordChangeForm = false
                            currentPassword = ""
                            newPassword = ""
                            confirmNewPassword = ""
                            passwordError = null
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(stringResource(R.string.cancel))
                    }
                    
                    Button(
                        onClick = {
                            if (newPassword.length < 6) {
                                passwordError = "Пароль должен содержать не менее 6 символов"
                                return@Button
                            }
                            
                            if (newPassword != confirmNewPassword) {
                                passwordError = "Пароли не совпадают"
                                return@Button
                            }
                            
                            viewModel.changePassword(currentPassword, newPassword)
                        },
                        modifier = Modifier.weight(1f),
                        enabled = currentPassword.isNotBlank() && 
                                  newPassword.isNotBlank() && 
                                  confirmNewPassword.isNotBlank()
                    ) {
                        Text(stringResource(R.string.save))
                    }
                }
            }
            
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