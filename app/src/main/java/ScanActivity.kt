package au.com.proximitybreach.lab

import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import au.com.proximitybreach.lab.databinding.ActivityScanBinding

class ScanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScanBinding
    private lateinit var deviceRecyclerView: RecyclerView
    private lateinit var deviceAdapter: DeviceAdapter
    private val devices = mutableListOf<BluetoothDevice>()
    private var scanning = false

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            result.device?.let { device ->
                if (devices.none { it.address == device.address }) {
                    devices.add(device)
                    deviceAdapter.notifyItemInserted(devices.size - 1)
                    Log.d("ScanActivity", "Found device: ${device.name ?: "Unknown"} - ${device.address}")
                }
            }
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            Log.e("ScanActivity", "Scan failed with error: $errorCode")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        startScan()
    }

    private fun setupRecyclerView() {
        deviceRecyclerView = binding.deviceRecyclerView
        deviceAdapter = DeviceAdapter(devices) { device ->
            // Device clicked - pass to exploit activity
            val intent = Intent(this, ExploitActivity::class.java).apply {
                putExtra("DEVICE_ADDRESS", device.address)
                putExtra("DEVICE_NAME", device.name)
            }
            startActivity(intent)
        }
        deviceRecyclerView.layoutManager = LinearLayoutManager(this)
        deviceRecyclerView.adapter = deviceAdapter
    }

    private fun startScan() {
        if (scanning) return
        
        devices.clear()
        deviceAdapter.notifyDataSetChanged()
        
        binding.scanStatusText.text = "Scanning for Bluetooth devices..."
        
        // Start Bluetooth LE scan
        try {
            bluetoothAdapter.bluetoothLeScanner.startScan(scanCallback)
            scanning = true
            
            // Stop scan after 10 seconds
            Handler(Looper.getMainLooper()).postDelayed({
                stopScan()
            }, 10000)
            
        } catch (e: SecurityException) {
            Log.e("ScanActivity", "Bluetooth scan permission denied", e)
            binding.scanStatusText.text = "Error: Bluetooth permissions denied"
        } catch (e: Exception) {
            Log.e("ScanActivity", "Bluetooth scan failed", e)
            binding.scanStatusText.text = "Error: Scan failed"
        }
    }

    private fun stopScan() {
        if (!scanning) return
        
        try {
            bluetoothAdapter.bluetoothLeScanner.stopScan(scanCallback)
            scanning = false
            binding.scanStatusText.text = "Scan complete. Found ${devices.size} devices."
        } catch (e: Exception) {
            Log.e("ScanActivity", "Error stopping scan", e)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopScan()
    }
}
