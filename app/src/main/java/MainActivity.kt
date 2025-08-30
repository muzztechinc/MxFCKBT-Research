// File: MainActivity.kt
package au.com.proximitybreach.lab

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.IOException
import java.io.OutputStream
import java.util.UUID

class MainActivity : AppCompatActivity() {

    private lateinit var bluetoothAdapter: BluetoothAdapter
    private var targetDevice: BluetoothDevice? = null
    private var socket: BluetoothSocket? = null
    private var outputStream: OutputStream? = null

    // SPP UUID (Serial Port Profile) - Common for many devices
    private val SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    companion object {
        private const val TAG = "ProximityBreach"
        private const val REQUEST_ALL_PERMISSIONS = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        checkPermissions()
    }

    private fun checkPermissions() {
        val permissions = arrayOf(
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT
        )
        if (permissions.any { ActivityCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED }) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_ALL_PERMISSIONS)
        } else {
            startBluetoothOperations()
        }
    }

    private fun startBluetoothOperations() {
        if (!bluetoothAdapter.isEnabled) {
            // Handle Bluetooth not enabled
            return
        }
        startDiscovery()
        connectToTargetDevice()
    }

    private fun startDiscovery() {
        // Start device discovery - looking for vulnerable targets
        val bondedDevices: Set<BluetoothDevice> = bluetoothAdapter.bondedDevices
        bondedDevices.forEach { device ->
            Log.i(TAG, "Bonded device: ${device.name} - ${device.address}")
            // Heuristic: Look for Windows devices (often contain "DESKTOP" or specific OEM names)
            if (device.name?.contains("DESKTOP", ignoreCase = true) == true) {
                targetDevice = device
            }
        }
    }

    private fun connectToTargetDevice() {
        GlobalScope.launch(Dispatchers.IO) {
            targetDevice?.let { device ->
                try {
                    // Method 1: Try creating insecure RFCOMM socket
                    socket = device.createInsecureRfcommSocketToServiceRecord(SPP_UUID)
                    socket?.connect()
                    outputStream = socket?.outputStream
                    Log.i(TAG, "Connected to ${device.name}")

                    // Send test payload
                    sendPayload("TEST_PAYLOAD\n")

                } catch (e: IOException) {
                    Log.e(TAG, "Connection failed: ${e.message}")
                    try {
                        // Method 2: Fallback to standard method
                        socket = device.createRfcommSocketToServiceRecord(SPP_UUID)
                        socket?.connect()
                        outputStream = socket?.outputStream
                        Log.i(TAG, "Connected via fallback method")
                    } catch (e2: IOException) {
                        Log.e(TAG, "Fallback connection also failed: ${e2.message}")
                    }
                }
            }
        }
    }

    private fun sendPayload(payload: String) {
        try {
            outputStream?.write(payload.toByteArray())
            outputStream?.flush()
            Log.i(TAG, "Payload sent: $payload")
        } catch (e: IOException) {
            Log.e(TAG, "Failed to send payload: ${e.message}")
        }
    }

    // ... Additional methods for brute force, spoofing, etc.
}
