package com.example.soundwave.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.PlaylistPlay
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.soundwave.models.User
import com.example.soundwave.navigation.Screen
import com.example.soundwave.ui.LocalActivity
import com.example.soundwave.viewModels.LibraryViewModel
import com.example.soundwave.viewModels.ProfileViewModel
import com.example.soundwave.data.remote.TokenProvider
import kotlinx.coroutines.launch

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
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(user) {
        editedName = user?.name ?: ""
        editedEmail = user?.email ?: ""
    }

    LaunchedEffect(user) {
        if (user == null) {
            val token = TokenProvider.getToken()
            if (token == null) {
                navController.navigate("auth") {
                    popUpTo(Screen.Profile.route) { inclusive = true }
                }
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
                coroutineScope.launch {
                    viewModel.logout()
                    navController.navigate("auth") {
                        popUpTo(Screen.Profile.route) { inclusive = true }
                    }
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

            StatsSection(user.id)

            Spacer(modifier = Modifier.height(24.dp))

            if (isEditing) {
                EditActions(
                    onSave = {
                        viewModel.updateUserInfo(editedName, editedEmail)
                        isEditing = false
                    },
                    onCancel = {
                        editedName = user.name
                        editedEmail = user.email
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
                coroutineScope.launch {
                    viewModel.deleteAccount()
                    navController.navigate("auth") {
                        popUpTo(Screen.Profile.route) { inclusive = true }
                    }
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
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
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
                            Icons.AutoMirrored.Filled.Logout,
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
fun UserInfoSection(
    user: User,
    onEditClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Text(
                text = user.name,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Modifier le profil",
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onEditClick() },
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Text(
            text = user.email,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )

        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text(
                text = "Membre depuis " + user.createdAt.take(4),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
fun EditUserInfoSection(
    name: String,
    email: String,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Modifier vos informations",
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                label = { Text("Nom") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = email,
                onValueChange = onEmailChange,
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            )
        }
    }
}

@Composable
fun StatsSection(userId: String?) {

    val vm: LibraryViewModel = viewModel(LocalActivity.current)

    val likedCount = if(userId != null) vm.likedCountForUser() else 0
    val playlistsCount = if(userId != null) vm.playlistsForUser().count() else 0

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        StatCard(playlistsCount, "Playlists", Icons.AutoMirrored.Filled.PlaylistPlay)
        StatCard(likedCount, "Favoris", Icons.Default.Favorite)
    }
}

@Composable
fun EditActions(
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
    Column {
        Button(
            onClick = onSave,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .height(50.dp),
            shape = RoundedCornerShape(25.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF00B894)
            )
        ) {
            Icon(
                imageVector = Icons.Default.Save,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Sauvegarder",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onCancel,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .height(50.dp),
            shape = RoundedCornerShape(25.dp)
        ) {
            Text(
                text = "Annuler",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun DeleteConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Supprimer le compte ?",
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Text(
                text = "Cette action est irréversible. Toutes vos données seront définitivement supprimées.",
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Supprimer")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        },
        containerColor = MaterialTheme.colorScheme.surface
    )
}

@Composable
fun StatCard(value: Int, label: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Card(
        modifier = Modifier
            .width(100.dp)
            .height(80.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = value.toString(),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = label,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }

}

@Composable
fun AvatarSection(
    user: User,
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