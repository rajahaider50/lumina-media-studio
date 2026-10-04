package com.example.premiumapp.feature.permissions

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.OpenInNew
import androidx.compose.material.icons.rounded.PhotoLibrary
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.VideoLibrary
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.premiumapp.core.permissions.PermissionManager
import com.example.premiumapp.design.components.LuminaCard
import com.example.premiumapp.design.components.LuminaOutlinedButton
import com.example.premiumapp.design.components.LuminaPrimaryButton
import com.example.premiumapp.design.components.LuminaTopBar
import com.example.premiumapp.design.dimensions.LuminaIconSize
import com.example.premiumapp.design.dimensions.LuminaSpacing
import com.example.premiumapp.design.theme.LuminaCyan
import com.example.premiumapp.design.theme.LuminaIndigo
import com.example.premiumapp.design.theme.LuminaThemeTokens
import com.example.premiumapp.design.theme.StatusError
import com.example.premiumapp.design.theme.StatusSuccess
import com.example.premiumapp.domain.model.PermissionStatus
import com.example.premiumapp.domain.model.PermissionType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PermissionsUiState(
    val statuses: Map<PermissionType, PermissionStatus> = emptyMap()
)

class PermissionsViewModel(
    private val permissionManager: PermissionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(PermissionsUiState())
    val uiState: StateFlow<PermissionsUiState> = _uiState.asStateFlow()

    init {
        refreshStatuses()
    }

    fun refreshStatuses() {
        val map = PermissionType.entries.associateWith { type ->
            permissionManager.checkPermissionStatus(type)
        }
        _uiState.value = PermissionsUiState(statuses = map)
    }

    fun getRequiredPermissions(type: PermissionType): List<String> {
        return permissionManager.getRequiredPermissions(type)
    }

    fun openAppSettings() {
        val intent = permissionManager.createAppSettingsIntent()
        permissionManager.createAppSettingsIntent()
    }
}

@Composable
fun PermissionsScreen(
    viewModel: PermissionsViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val extended = LuminaThemeTokens.extended
    val context = LocalContext.current

    var pendingPermissionType by remember { mutableStateOf<PermissionType?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) {
        viewModel.refreshStatuses()
    }

    LaunchedEffect(Unit) {
        viewModel.refreshStatuses()
    }

    Scaffold(
        topBar = {
            LuminaTopBar(
                title = "Permissions Center",
                onBackClick = onNavigateBack
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(LuminaSpacing.large)
        ) {
            // Privacy banner
            LuminaCard(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Security,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(LuminaIconSize.standard)
                        )
                    }
                    Spacer(modifier = Modifier.width(LuminaSpacing.medium))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Granular Access",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Lumina requests permissions only when initiating relevant actions. Your data is processed entirely on-device.",
                            style = MaterialTheme.typography.bodySmall,
                            color = extended.textSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(LuminaSpacing.large))

            // Permission Rows
            PermissionType.entries.forEach { type ->
                val status = uiState.statuses[type] ?: PermissionStatus.DENIED
                val isGranted = status == PermissionStatus.GRANTED
                val icon = when (type) {
                    PermissionType.CAMERA -> Icons.Rounded.CameraAlt
                    PermissionType.PHOTOS -> Icons.Rounded.PhotoLibrary
                    PermissionType.VIDEOS -> Icons.Rounded.VideoLibrary
                    PermissionType.NOTIFICATIONS -> Icons.Rounded.Notifications
                }

                PermissionRowCard(
                    title = type.title,
                    description = type.rationale,
                    icon = icon,
                    isGranted = isGranted,
                    onRequestPermission = {
                        val required = viewModel.getRequiredPermissions(type)
                        if (required.isNotEmpty()) {
                            permissionLauncher.launch(required.toTypedArray())
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = LuminaSpacing.medium)
                )
            }

            Spacer(modifier = Modifier.height(LuminaSpacing.medium))

            // System App Settings Button
            LuminaOutlinedButton(
                text = "Open Android System Settings",
                icon = Icons.Rounded.OpenInNew,
                onClick = {
                    val intent = android.content.Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = android.net.Uri.fromParts("package", context.packageName, null)
                        flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(intent)
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun PermissionRowCard(
    title: String,
    description: String,
    icon: ImageVector,
    isGranted: Boolean,
    onRequestPermission: () -> Unit,
    modifier: Modifier = Modifier
) {
    val extended = LuminaThemeTokens.extended

    LuminaCard(modifier = modifier) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            if (isGranted) StatusSuccess.copy(alpha = 0.15f) else extended.borderSubtle,
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isGranted) StatusSuccess else extended.textSecondary,
                        modifier = Modifier.size(LuminaIconSize.standard)
                    )
                }

                Spacer(modifier = Modifier.width(LuminaSpacing.medium))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = if (isGranted) "Granted" else "Not Granted",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isGranted) StatusSuccess else StatusError
                    )
                }

                if (!isGranted) {
                    LuminaPrimaryButton(
                        text = "Allow",
                        onClick = onRequestPermission
                    )
                } else {
                    Icon(
                        imageVector = Icons.Rounded.CheckCircle,
                        contentDescription = "Granted",
                        tint = StatusSuccess,
                        modifier = Modifier.size(LuminaIconSize.standard)
                    )
                }
            }

            Spacer(modifier = Modifier.height(LuminaSpacing.small))

            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = extended.textSecondary
            )
        }
    }
}
