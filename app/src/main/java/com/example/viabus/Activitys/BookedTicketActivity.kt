package com.example.viabus.Activitys

import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.viabus.R
import com.example.viabus.databinding.ActivityBookedTicketBinding
import com.google.firebase.Timestamp
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import com.journeyapps.barcodescanner.BarcodeEncoder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class BookedTicketActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityBookedTicketBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val ticketId = intent.getStringExtra("ticketId").toString()
        val busNumber = intent.getStringExtra("busNumber").toString()
        val busRoutes = intent.getStringExtra("busRoutes").toString()
        val fromStop = intent.getStringExtra("fromStop").toString()
        val toStop = intent.getStringExtra("toStop").toString()
        val ticketCount = intent.getStringExtra("ticketCount").toString()
        val totalPrice = intent.getStringExtra("totalPrice").toString()
        val timestampMiles = intent.getLongExtra("timestamp", 0)
        val timestamp = Timestamp(Date(timestampMiles))
        /// readble format
        val timeFormat =
            SimpleDateFormat("dd MMM yyyy,hh:mm a", Locale.getDefault()).format(timestamp.toDate())

        binding.busNuber.text = busNumber
        binding.busRoute.text = busRoutes
        binding.date.text = timeFormat
        binding.fair.text = totalPrice
        binding.startingStop.text = fromStop
        binding.endingStop.text = toStop
        binding.ticket.text = ticketCount

        /// generate Qr
        generateQR(ticketId)
    }


    private fun generateQR(ticketId: String) {
        val writer = QRCodeWriter()
        try {
            val bitMatrix: BitMatrix = writer.encode(ticketId, BarcodeFormat.QR_CODE, 400, 400)
            val barcodeEncoder = BarcodeEncoder()
            val bitmap: Bitmap = barcodeEncoder.createBitmap(bitMatrix)
            binding.ticketQr.setImageBitmap(bitmap)
        } catch (e: WriterException) {
            e.printStackTrace()
        }
    }


}