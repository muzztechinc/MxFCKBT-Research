package com.mxfckbt.research2025

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.mxfckbt.research2025.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var bluetoothManager: BluetoothManager

    companion object {
        const val TAG = "MxFckBT"
        const val APP_NAME = "MxFckBT 2025"
        const val APP_VERSION = "2025.1.0"
        
        private const val REQUEST_ENABLE_BT = 1
        private const val REQUEST_ALL_PERMISSIONS = 2
        
        private val PERMISSIONS = arrayOf(
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.i(TAG, "$APP_NAME v$APP_VERSION Initializing...")
        
        setupUI()
        initializeBluetooth()
        showInstallationWarning()
    }

    private fun setupUI() {
        binding.appTitle.text = "MxFckBT 2025 - Research Platform"
        binding.appVersionText.text = "MxFckBT v$APP_VERSION"
        binding.welcomeText.text = "Welcome to MxFckBT\nAdvanced Bluetooth Research"
        binding.copyrightText.text = "© 2025 MX Research Labs - Authorized Testing Only"
        
        binding.scanButton.text = "MX Scan"
        binding.exploitButton.text = "MX Analyze"
    }

    private fun showInstallationWarning() {
        AlertDialog.Builder(this)
            .setTitle("MxFckBT 2025 - Research Platform")
            .setMessage("Use only on devices you own or have explicit permission to test. Unauthorized access is illegal.")
            .setPositiveButton("I Understand") { dialog, _ ->
                dialog.dismiss()
                showResearchDisclaimer()
            }
            .setCancelable(false)
            .show()
    }

    private fun showResearchDisclaimer() {
        AlertDialog.Builder(this)
            .setTitle("Bluetooth Research Warning")
            .setMessage("MxFckBT is designed for authorized security research only. You must have explicit written permission before testing any devices you do not own.")
            .setPositiveButton("Accept") { dialog, _ ->
                dialog.dismiss()
                Log.i(TAG, "User accepted research disclaimer")
                checkPermissions()
            }
            .setNegativeButton("Exit") { dialog, _ ->
                dialog.dismiss()
                finish()
            }
            .setCancelable(false)
            .show()
    }

    private fun initializeBluetooth() {
        bluetoothManager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter
        
        if (bluetoothAdapter == null) {
            binding.statusText.text = "$APP_NAME Error: Bluetooth not supported"
            return
        }

        if (!bluetoothAdapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        } else {
            checkPermissions()
        }
    }

    private fun checkPermissions(): Boolean {
        if (PERMISSIONS.any { ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED }) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_ALL_PERMISSIONS)
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        
        when (requestCode) {
            REQUEST_ALL_PERMISSIONS -> {
                if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                    binding.statusText.text = "$APP_NAME Ready for Research"
                    binding.scanButton.isEnabled = true
                    binding.exploitButton.isEnabled = true
                } else {
                    binding.statusText.text = "$APP_NAME Permissions denied"
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        
        when (requestCode) {
            REQUEST_ENABLE_BT -> {
                if (resultCode == RESULT_OK) {
                    checkPermissions()
                } else {
                    binding.statusText.text = "$APP_NAME Bluetooth not enabled"
                }
            }
        }
    }
}
