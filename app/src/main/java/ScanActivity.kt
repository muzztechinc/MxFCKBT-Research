package com.mxfckbt.research2025

// ... [previous imports] ...
import com.mxfckbt.research2025.databinding.ActivityScanBinding

class ScanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScanBinding
    // ... [other variables] ...

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set MXFCKBT 2025 title
        binding.scanTitle.text = "MxFckBT 2025 - Device Reconnaissance"
        binding.scanStatusText.text = "MxFckBT 2025 Active Scan: Initializing..."
        
        Log.i(TAG, "MxFckBT 2025 Device Scan initiated")

        setupRecyclerView()
        startScan()
    }

    private fun startScan() {
        if (scanning) return
        
        devices.clear()
        deviceAdapter.notifyDataSetChanged()
        
        binding.scanStatusText.text = "MxFckBT 2025 Active Scan: Searching for Targets..."
        Log.i(TAG, "MxFckBT 2025 starting Bluetooth scan...")
        
        // ... [rest of scan logic] ...
    }

    private fun stopScan() {
        if (!scanning) return
        
        try {
            bluetoothAdapter.bluetoothLeScanner.stopScan(scanCallback)
            scanning = false
            binding.scanStatusText.text = "MxFckBT 2025 Scan complete. Found ${devices.size} devices."
            Log.i(TAG, "MxFckBT 2025 scan completed. Devices found: ${devices.size}")
        } catch (e: Exception) {
            Log.e(TAG, "MxFckBT 2025 Error stopping scan", e)
        }
    }
}
