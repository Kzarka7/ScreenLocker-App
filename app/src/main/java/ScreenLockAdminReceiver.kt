package com.example.screenlock

import android.app.admin.DeviceAdminReceiver

/**
 * Required BroadcastReceiver for a Device Administrator app.
 *
 * We don't override any callbacks here — this app never reacts to
 * admin lifecycle events (enabled/disabled/password-changed/etc). Its
 * only reason to exist is that Android requires a registered
 * DeviceAdminReceiver before it will let the app become a device
 * administrator, which is in turn required to call
 * DevicePolicyManager.lockNow().
 */
class ScreenLockAdminReceiver : DeviceAdminReceiver()
