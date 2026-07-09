package com.ciyato.launcher.ui.screens

import android.net.Uri
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.ciyato.launcher.ui.theme.CiyatoGold
import com.ciyato.launcher.viewmodel.LauncherViewModel

@Composable
fun FilesScreen(viewModel: LauncherViewModel, onBack: () -> Unit) {
    val storedRoot by viewModel.filesRootUri.collectAsState()
    val initialUri = remember(storedRoot) {
        storedRoot.takeIf { it.isNotBlank() }?.let(Uri::parse)
    }

    FileCollectionDetailScreen(
        collectionTitle = "Internal Storage",
        collectionIcon = Icons.Default.FolderOpen,
        collectionColor = CiyatoGold,
        initialFolderUri = initialUri,
        onFolderSelected = { uri -> viewModel.setFilesRootUri(uri.toString()) },
        onForgetFolder = viewModel::clearFilesRootUri,
        onBack = onBack,
    )
}
