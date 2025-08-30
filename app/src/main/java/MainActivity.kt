package au.com.proximitybreach.lab

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
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
import au.com.proximitybreach.lab.databinding.ActivityMainBinding
import java.util.ArrayList

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var statusText: TextView
    private lateinit var scanButton: Button
    private lateinit var exploitButton: Button

    companion object {
        private const val TAG = "ProximityBreach"
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

        statusText = binding.statusText
        scanButton = binding.scanButton
        exploitButton = binding.exploitButton

        initializeBluetooth()
        setupButtons()
    }

    private fun initializeBluetooth() {
        val bluetoothManager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter
        
        if (bluetoothAdapter == null) {
            statusText.text = "Bluetooth not supported on this device"
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
        scanButton.setOnClickListener {
            if (checkPermissions()) {
                startActivity(Intent(this, ScanActivity::class.java))
            }
        }

        exploitButton.setOnClickListener {
            if (checkPermissions()) {
                startActivity(Intent(this, ExploitActivity::class.java))
            }
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
                    statusText.text = "Permissions granted. Ready to scan."
                    scanButton.isEnabled = true
                    exploitButton.isEnabled = true
                } else {
                    statusText.text = "Permissions denied. Some features disabled."
                    Toast.makeText(this, "All permissions are required for full functionality", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        
        when (requestCode) {
            REQUEST_ENABLE_BT -> {
                if (resultCode == RESULT_OK) {
                    statusText.text = "Bluetooth enabled. Checking permissions..."
                    checkPermissions()
                } else {
                    statusText.text = "Bluetooth not enabled. App functionality limited."
                }
            }
        }
    }
}
