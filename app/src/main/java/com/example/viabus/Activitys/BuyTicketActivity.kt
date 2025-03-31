package com.example.viabus.Activitys

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.audiofx.BassBoost
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.viabus.Adapters.BusRouteAdapter
import com.example.viabus.DataClass.Bus
import com.example.viabus.databinding.ActivityBuyTicketBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.CompoundBarcodeView

class BuyTicketActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityBuyTicketBinding.inflate(layoutInflater)
    }

    companion object {
        private const val CAMERA_PERMISSION_CODE = 101
    }

    private lateinit var db:FirebaseFirestore
    private lateinit var barcodeScanner: CompoundBarcodeView
    private lateinit var adapter: BusRouteAdapter
    private val busRouteList = mutableListOf<Bus>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        db=FirebaseFirestore.getInstance()
        // Initialize barcode scanner
        barcodeScanner = binding.cameraPreview // Make sure the ID exists in XML

        // Initialize RecyclerView
        adapter = BusRouteAdapter(busRouteList)
        binding.recyclerview.layoutManager = LinearLayoutManager(this)
        binding.recyclerview.adapter = adapter


        // Check Camera Permission
        checkCameraPermission()
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // Check if we should show a rationale
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.CAMERA
                )
            ) {
                // Show an explanation to the user
                Toast.makeText(
                    this,
                    "Camera permission is required to scan QR codes. Please grant the permission.",
                    Toast.LENGTH_LONG
                ).show()
            }
            // Request the permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_CODE
            )
        } else {
            // Permission already granted
            startScanning()
        }
    }

    private fun startScanning() {
        barcodeScanner.decodeSingle(object : BarcodeCallback {
            override fun barcodeResult(result: BarcodeResult?) {
                result?.text?.let { scannedText ->

                    val ref = db.collection("Buses").document(scannedText).collection("busRoutes")
                    ref.get().addOnSuccessListener { querySnapshot ->
                        if (!querySnapshot.isEmpty) {  // Check if routes exist
                            for (document in querySnapshot.documents) {
                                val data = document.toObject(Bus::class.java)
                                data?.let {
                                    busRouteList.add(it)
                                    binding.scanTicketLayout.visibility=View.GONE
                                    binding.busRouteLayout.visibility=View.VISIBLE
                                    Log.d("DEBUG", "Bus Route: ${it.busRoute}, Type: ${it.busType}")
                                }

                                adapter.notifyDataSetChanged()
                            }
                        } else {
                            Log.d("DEBUG", "No routes found for this bus")
                        }
                    }.addOnFailureListener { e ->
                        Log.e("DEBUG", "Error fetching bus routes", e)
                    }

                    Log.d("DEBUG", "Bus Number: $scannedText")
                }
            }
        })
        barcodeScanner.resume()
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("DEBUG", "✅ Camera permission granted")
                startScanning()
            } else {
                Log.e("DEBUG", "❌ Camera permission denied")

                // Check if the user selected "Don't ask again"
                if (!ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.CAMERA
                    )
                ) {
                    // User denied permanently, redirect to app settings
                    Toast.makeText(
                        this,
                        "Camera permission is required. Please enable it in settings.",
                        Toast.LENGTH_LONG
                    ).show()

                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } else {
                    // User denied but didn't select "Don't ask again," show rationale and re-request
                    Toast.makeText(
                        this,
                        "Camera permission is required to scan QR codes.",
                        Toast.LENGTH_LONG
                    ).show()
                    checkCameraPermission() // Re-request permission
                }
            }
        }
    }
}

