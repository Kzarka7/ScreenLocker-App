package com.example.screenlock

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

class MainActivity : ComponentActivity() {

    private lateinit var devicePolicyManager: DevicePolicyManager
    private lateinit var adminComponent: ComponentName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        devicePolicyManager =
            getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        adminComponent = ComponentName(this, ScreenLockAdminReceiver::class.java)

        val currentTime = System.currentTimeMillis()

        if (devicePolicyManager.isAdminActive(adminComponent)) {
            // Increase window to 1000ms for better reliability
            if (currentTime - lastTapTime < 1000) {
                lastTapTime = 0 
                triggerHapticFeedback()
                devicePolicyManager.lockNow()
            } else {
                lastTapTime = currentTime
            }
            finish()
            return
        }

        // Apply normal theme for setup screen
        setTheme(R.style.Theme_ScreenLock)
        setContent {
            ScreenLockApp(
                devicePolicyManager = devicePolicyManager,
                adminComponent = adminComponent
            )
        }
    }

    private fun triggerHapticFeedback() {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(50)
        }
    }

    companion object {
        private var lastTapTime: Long = 0
    }
}

@Composable
fun ScreenLockApp(
    devicePolicyManager: DevicePolicyManager,
    adminComponent: ComponentName
) {
    var isAdminActive by remember {
        mutableStateOf(devicePolicyManager.isAdminActive(adminComponent))
    }

    val context = LocalContext.current

    // Automatically lock and finish if permission is granted while the setup screen is open
    LaunchedEffect(isAdminActive) {
        if (isAdminActive) {
            devicePolicyManager.lockNow()
            (context as? ComponentActivity)?.finish()
        }
    }

    // Re-check the real admin state every time the app comes back to the
    // foreground. This covers three cases: returning from the "Activate
    // device admin" screen, returning after the user revokes the
    // permission in system Settings, and a normal app resume.
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                isAdminActive = devicePolicyManager.isAdminActive(adminComponent)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val enableAdminLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        // Don't trust the result code — ask the system directly whether
        // admin is now active, since the user could have tapped
        // "Cancel" on that screen.
        isAdminActive = devicePolicyManager.isAdminActive(adminComponent)
    }

    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            if (!isAdminActive) {
                PermissionSetupScreen(
                    onEnableClick = {
                        val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
                            putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminComponent)
                            putExtra(
                                DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                                "This lets the app lock your screen, replacing your broken power button."
                            )
                        }
                        enableAdminLauncher.launch(intent)
                    }
                )
            }
        }
    }
}

@Composable
fun PermissionSetupScreen(onEnableClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Screen Lock Permission",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "This app needs permission to lock your phone when you press the button.",
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "This does not shut down your phone.",
            fontSize = 14.sp,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onEnableClick) {
            Text("ENABLE SCREEN LOCK")
        }
    }
}
