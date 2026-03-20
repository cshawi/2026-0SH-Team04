package com.example.soundwave.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.soundwave.navigation.Screen
import com.example.soundwave.ui.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel
) {
    val user = viewModel.currentUser.value
    val isLoading = viewModel.isLoading.value
    var showDeleteDialog by remember { mutableStateOf(false) }
    var isEditing by remember { mutableStateOf(false) }

    var menuExpanded by remember { mutableStateOf(false) }

    var editedName by remember { mutableStateOf(user?.name ?: "") }
    var editedEmail by remember { mutableStateOf(user?.email ?: "") }

    LaunchedEffect(user) {
        editedName = user?.name ?: ""
        editedEmail = user?.email ?: ""
    }

    LaunchedEffect(user) {
        if (user == null) {
            navController.navigate("auth") {
                popUpTo(Screen.Profile.route) { inclusive = true }
            }
        }
    }

    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
        return
    }

    if (user == null) {
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        ToolbarSection(
            menuExpanded = menuExpanded,
            onMenuExpandedChange = { menuExpanded = it },
            onBackClick = {
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Profile.route) { inclusive = true }
                }
            },
            onLogoutClick = {
                menuExpanded = false
                viewModel.logout()
                navController.navigate("auth") {
                    popUpTo(Screen.Profile.route) { inclusive = true }
                }
            },
            onDeleteClick = {
                menuExpanded = false
                showDeleteDialog = true
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar avec caméra (plus haut)
            AvatarSection(
                user = user,
                modifier = Modifier.padding(top = 20.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (isEditing) {
                EditUserInfoSection(
                    name = editedName,
                    email = editedEmail,
                    onNameChange = { editedName = it },
                    onEmailChange = { editedEmail = it }
                )
            } else {
                UserInfoSection(
                    user = user,
                    onEditClick = { isEditing = true }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            StatsSection()

            Spacer(modifier = Modifier.height(24.dp))

            if (isEditing) {
                EditActions(
                    onSave = {
                        viewModel.updateUserInfo(editedName, editedEmail)
                        isEditing = false
                    },
                    onCancel = {
                        editedName = user.name
                        editedEmail = user.email ?: ""
                        isEditing = false
                    }
                )
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }

    if (showDeleteDialog) {
        DeleteConfirmationDialog(
            onConfirm = {
                showDeleteDialog = false
                viewModel.deleteAccount()
                navController.navigate("auth") {
                    popUpTo(Screen.Profile.route) { inclusive = true }
                }
            },
            onDismiss = { showDeleteDialog = false }
        )
    }
}

@Composable
fun ToolbarSection(
    menuExpanded: Boolean,
    onMenuExpandedChange: (Boolean) -> Unit,
    onBackClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(56.dp)
    ) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "Retour",
            modifier = Modifier
                .size(32.dp)
                .align(Alignment.CenterStart)
                .clickable { onBackClick() },
            tint = MaterialTheme.colorScheme.onBackground
        )

        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "Menu",
                modifier = Modifier
                    .size(32.dp)
                    .clickable { onMenuExpandedChange(true) },
                tint = MaterialTheme.colorScheme.onBackground
            )

            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { onMenuExpandedChange(false) },
                modifier = Modifier.background(MaterialTheme.colorScheme.surface)
            ) {
                DropdownMenuItem(
                    text = {
                        Text(
                            "Se déconnecter",
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Logout,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    onClick = onLogoutClick
                )

                DropdownMenuItem(
                    text = {
                        Text(
                            "Supprimer le compte",
                            color = MaterialTheme.colorScheme.error
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                    },
                    onClick = onDeleteClick
                )
            }
        }
    }
}

@Composable
fun AvatarSection(
    user: com.example.soundwave.models.User,
    modifier: Modifier = Modifier
) {
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                user.avatarUrl = uri.path
            }
        }
    )

    Box(
        modifier = modifier
            .size(130.dp)
    ) {
        Box(
            modifier = Modifier
                .size(140.dp)
                .offset(x = (-5).dp, y = (-5).dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                            Color.Transparent
                        )
                    )
                )
                .align(Alignment.Center)
        )

        Box(
            modifier = Modifier
                .size(130.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface)
                .border(3.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
                .shadow(elevation = 10.dp, shape = CircleShape)
                .clickable {
                    imagePicker.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
        ) {
            if (user.avatarUrl != null) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(user.avatarUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Photo de profil",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier
                        .size(70.dp)
                        .align(Alignment.Center),
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
            }
        }

        // Icône caméra avec style amélioré
        Box(
            modifier = Modifier
                .size(42.dp)
                .offset(x = 90.dp, y = 90.dp)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.secondary,
                            MaterialTheme.colorScheme.tertiary
                        )
                    ),
                    CircleShape
                )
                .border(2.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
                .shadow(elevation = 6.dp, shape = CircleShape)
                .clickable {
                    imagePicker.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = "Changer photo",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
fun ProfileScreen(profileViewModel: ProfileViewModel = viewModel()) {
    Text(
        text = "Profile Screen"
    )
}