package ua.anastasiia.finesapp.util

import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionRequired
import com.google.accompanist.permissions.rememberPermissionState
import ua.anastasiia.finesapp.R

@ExperimentalPermissionsApi
@Composable
fun Permission(
    permission: String = android.Manifest.permission.CAMERA,
    rationale: String = stringResource(R.string.important_permission),
    permissionNotAvailableContent: @Composable () -> Unit = { },
    content: @Composable () -> Unit = { }
) {
    val permissionState = rememberPermissionState(permission)
    PermissionRequired(
        permissionState = permissionState, permissionNotGrantedContent = {
            Rationale(text = rationale,
                onRequestPermission = { permissionState.launchPermissionRequest() })
        }, permissionNotAvailableContent = permissionNotAvailableContent, content = content
    )
}

@Composable
private fun Rationale(
    text: String, onRequestPermission: () -> Unit
) {
    AlertDialog(onDismissRequest = { /* Don't */ }, title = {
        Text(text = stringResource(R.string.permission_request))
    }, text = {
        Text(text)
    }, confirmButton = {
        Button(onClick = onRequestPermission) {
            Text(stringResource(R.string.ok))
        }
    })
}
