package com.mxfckbt.research2025

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.mxfckbt.research2025.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var statusText: TextView
    private lateinit var scanButton: Button
    private lateinit var exploitButton: Button

    companion object {
        private const val TAG = "MXFCKBT2025"
        private const val REQUEST_ENABLE_BT = 1
        private const val REQUEST_ALL_PERMISSIONS = 2
        
        // MXFCKBT 2025 Application Constants
        const val APP_NAME = "MxFckBT 2025"
        const val APP_VERSION = "2025.1.0"
        const val APP_CODENAME = "Project Nightshade"
        
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

        Log.i(TAG, "$APP_NAME v$APP_VERSION ($APP_CODENAME) Initializing...")
        
        // Set version info in UI
        binding.appVersionText.text = "MxFckBT 2025 v$APP_VERSION"
        binding.codenameText.text = APP_CODENAME

        statusText = binding.statusText
        scanButton = binding.scanButton
        exploitButton = binding.exploitButton

        initializeBluetooth()
        setupButtons()
        displayAppInfo()
    }

    private fun displayAppInfo() {
        binding.appTitle.text = APP_NAME
        binding.welcomeText.text = "Welcome to $APP_NAME\nElite Bluetooth Research Platform"
        binding.copyrightText.text = "© 2025 MX Research Labs - Authorized Testing Only"
        
        // Display build information
        val buildInfo = """
            MXFCKBT 2025 Build Information:
            Version: ${BuildConfig.APP_VERSION}
            Codename: ${BuildConfig.APP_CODENAME}
            Build Type: ${BuildConfig.BUILD_TYPE}
            Package: ${packageName}
        """.trimIndent()
        
        Log.d(TAG, buildInfo)
    }

    private fun initializeBluetooth() {
        val bluetoothManager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter
        
        if (bluetoothAdapter == null) {
            statusText.text = "$APP_NAME Error: Bluetooth not supported"
            scanButton.isEnabled = false
            exploitButton.isEnabled = false
            return
        }

        if (!bluetoothAdapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        } else {
            checkPermissions()
        }
    }

    private fun setupButtons() {
        scanButton.text = "MX Scan"
        scanButton.setOnClickListener {
            if (checkPermissions()) {
                Log.i(TAG, "$APP_NAME initiating device scan...")
                startActivity(Intent(this, ScanActivity::class.java))
            }
        }

        exploitButton.text = "MX Exploit Console"
        exploitButton.setOnClickListener {
            if (checkPermissions()) {
                Log.i(TAG, "$APP_NAME opening exploit console...")
                startActivity(Intent(this, ExploitActivity::class.java))
            }
        }
        
        // Add MXFCKBT 2025 info button
        binding.infoButton.setOnClickListener {
            Toast.makeText(this, 
                "MxFckBT 2025 v$APP_VERSION\n$APP_CODENAME\nAuthorized Research Only", 
                Toast.LENGTH_LONG).show()
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
                    statusText.text = "$APP_NAME Permissions granted. Ready for operation."
                    scanButton.isEnabled = true
                    exploitButton.isEnabled = true
                    Log.i(TAG, "$APP_NAME all permissions granted")
                } else {
                    statusText.text = "$APP_NAME Permissions denied. Features limited."
                    Toast.makeText(this, "MxFckBT 2025 requires all permissions for full functionality", Toast.LENGTH_LONG).show()
                    Log.w(TAG, "$APP_NAME permissions partially denied")
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        
        when (requestCode) {
            REQUEST_ENABLE_BT -> {
                if (resultCode == RESULT_OK) {
                    statusText.text = "$APP_NAME Bluetooth enabled. Initializing..."
                    checkPermissions()
                    Log.i(TAG, "$APP_NAME Bluetooth enabled by user")
                } else {
                    statusText.text = "$APP_NAME Bluetooth not enabled. Operation limited."
                    Log.w(TAG, "$APP_NAME Bluetooth not enabled by user")
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Log.i(TAG, "$APP_NAME main console resumed")
    }

    override fun onPause() {
        super.onPause()
        Log.i(TAG, "$APP_NAME main console paused")
    }
}
