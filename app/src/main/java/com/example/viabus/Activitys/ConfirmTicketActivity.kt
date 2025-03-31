package com.example.viabus.Activitys

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.viabus.Utils
import com.example.viabus.databinding.ActivityConfirmTicketBinding
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import org.json.JSONObject
import java.util.UUID

class ConfirmTicketActivity : AppCompatActivity(), PaymentResultListener {

    private val binding by lazy {
        ActivityConfirmTicketBinding.inflate(layoutInflater)
    }

    private var busStopsList = mutableListOf<String>()
    private lateinit var db: FirebaseFirestore
    private lateinit var busRoutes:String
    private lateinit var busNumber:String
    private lateinit var busType:String
    private var count = 1 // Initial count

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        db = FirebaseFirestore.getInstance()

         busRoutes = intent.getStringExtra("route").toString()
         busNumber = intent.getStringExtra("busNo").toString()
         busType = intent.getStringExtra("busType").toString()
        /// Set bus information
        binding.busRoute.text = busRoutes
        binding.busNumber.text = busNumber

        /// Fetch bus stops and set in AutoCompleteTextView
        getBusStops(busNumber, busRoutes, busType)


    }

    private fun getBusStops(busNumber: String, busRoutes: String, busType: String) {
        val ref =
            db.collection("Buses").document(busNumber).collection("busRoutes").document(busRoutes)

        ref.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                // Firestore document exists, now fetch the bus stops list
                val busStops = snapshot.get("busStops") as? List<String>
                if (busStops != null) {
                    busStopsList.clear()
                    busStopsList.addAll(busStops)

                }
            }
            setUpAutoText(busType)
        }.addOnFailureListener {
            it.printStackTrace()
        }
    }

    private fun getBusStopsCount(busStops: MutableList<String>, busType: String) {
        val fromStop = binding.etFrom.text.toString()
        val toStop = binding.etTo.text.toString()

        if (fromStop.isNotEmpty() && toStop.isNotEmpty()) {
            val fromIndex = busStops.indexOf(fromStop)
            val toIndex = busStops.indexOf(toStop)

            if (fromIndex != -1 && toIndex != -1 && fromIndex < toIndex) {
                val stopsCount = (toIndex - fromIndex)
                //calculate fair according to  bus type like Ac/Non-Ac
                if (busType == "Non-AC") {

                } else {
                    calculateBusFareForNonAc(stopsCount)
                }


                Log.d("DEBUG", "Number of stops is $stopsCount")
            } else {
                Log.d("DEBUG", "unable to get bus stops")
            }
        } else {
            Log.d("DEBUG", "SELECT BUS STOPS")
        }
    }

    private fun calculateBusFareForNonAc(stopsCount: Int) {
        val totalStops = busStopsList.size
        val percentage = (stopsCount.toDouble() / totalStops) * 100


        if (percentage <= 40) {
            calculateNumberOfTicketCount(5)
        } else if (percentage < 40.1 && percentage <= 60) {
            calculateNumberOfTicketCount(10)
        } else {
            calculateNumberOfTicketCount(15)
        }
    }

    private fun finalFair(fair: Int, count: Int) {

        val totalAmount = fair * this.count

        binding.payButton.text = "Pay ₹$totalAmount"

        binding.payButton.setOnClickListener {
            startPayment(totalAmount)
        }

    }

    private fun setUpAutoText(busType: String) {
        val adapter = ArrayAdapter(
            this, android.R.layout.simple_dropdown_item_1line,
            this.busStopsList
        )
        binding.etTo.setAdapter(adapter)
        binding.etFrom.setAdapter(adapter)


        // Show suggestions after typing 1 character
        binding.etTo.threshold = 1
        binding.etFrom.threshold = 1


        // Call getBusStopsCount() only when both stops are selected
        binding.etFrom.setOnItemClickListener { _, _, _, _ ->
            getBusStopsCount(
                busStopsList,
                busType
            )
        }
        binding.etTo.setOnItemClickListener { _, _, _, _ ->
            getBusStopsCount(
                busStopsList,
                busType
            )
        }
    }

    private fun calculateNumberOfTicketCount(fair: Int) {
        /// Set initial value to count

        binding.numberOfPersonTxt.text = count.toString()

        finalFair(fair, count)
        /// Decrease the count
        binding.minus.setOnClickListener {
            if (count > 1) {
                count--
                binding.numberOfPersonTxt.text = count.toString()
                finalFair(fair, count)
            }
        }

        /// Increase count
        binding.plus.setOnClickListener {
            count++
            binding.numberOfPersonTxt.text = count.toString()
            finalFair(fair, count)
        }
    }


    private fun startPayment(amount: Int) {

        Utils.showDialog(this,"Wait...")
        val checkout = Checkout()
        checkout.setKeyID("rzp_test_N9hgXP1L6tCGPm")

        try {
            val options = JSONObject()
            options.put("name", "ViaBus") // 🔹 App ka naam
            options.put("description", "DTC Bus Ticket Payment") // 🔹 Payment Description
            options.put("currency", "INR") // 🔹 Currency
            options.put("amount", amount * 100) // 🔹 Amount paisa me hota hai (₹10 = 1000)

            val prefill = JSONObject()
            prefill.put("email", "nikhilabc860@gmail.com") // 🔹 User ka email (optional)
            prefill.put("contact", "7292921732") // 🔹 User ka phone number (optional)

            options.put("prefill", prefill)
            checkout.open(this, options) // 🔹 Payment Open Karega

        } catch (e: Exception) {
            Utils.dismissDialog()
            Toast.makeText(this, "Error in payment: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }

    }

    override fun onPaymentSuccess(razorpayPaymentID: String?) {

        Toast.makeText(this, "Payment Successful! ID: $razorpayPaymentID", Toast.LENGTH_LONG).show()
        Log.d("PAYMENT", "Success: $razorpayPaymentID")
        // 🔹 Payment details store in Firestore
        savePaymentToFirestore(razorpayPaymentID)
    }

    override fun onPaymentError(code: Int, response: String?) {
        Toast.makeText(this, "Payment Failed! Error: $response", Toast.LENGTH_LONG).show()
        Log.d("PAYMENT", "Error: $response")

        Utils.dismissDialog()
    }


    private fun savePaymentToFirestore(paymentID: String?) {

        val db = FirebaseFirestore.getInstance()

        // 🔹 Get user-selected data

        val fromStop = binding.etFrom.text.toString()
        val toStop = binding.etTo.text.toString()
        val ticketCount = count
        val totalPrice = binding.payButton.text.toString().replace("Pay ₹", "").toInt()

        val time=Timestamp.now()
        val ticketId=generateTicketCode()
        if (busNumber.isNotEmpty() && busRoutes.isNotEmpty() && fromStop.isNotEmpty() && toStop.isNotEmpty()){
            // 🔹 Prepare data to store
            val paymentData = hashMapOf(
                "paymentID" to paymentID,
                "busNumber" to busNumber,
                "busRoutes" to busRoutes,
                "fromStop" to fromStop,
                "toStop" to toStop,
                "ticketCount" to ticketCount,
                "totalPrice" to totalPrice,
                "timestamp" to time,
                "ticketId" to ticketId,
                "status" to "active" ,// Initially active
                "ticketTime" to System.currentTimeMillis()

            )

            // 🔹 Store in Firestore under "Payments" collection
            db.collection("user").document(FirebaseAuth.getInstance().currentUser!!.uid).collection("Tickets").document(ticketId)
                .set(paymentData)
                .addOnSuccessListener {
                    Utils.dismissDialog()
                    Toast.makeText(this, "Payment Stored Successfully!", Toast.LENGTH_SHORT).show()
                    Log.d("PAYMENT", "Stored Successfully!")

                    val intent=Intent(this,BookedTicketActivity::class.java)
                    intent.putExtra("paymentID",paymentID)
                    intent.putExtra("busNumber",busNumber)
                    intent.putExtra("busRoutes",busRoutes)
                    intent.putExtra("fromStop",fromStop)
                    intent.putExtra("toStop",toStop)
                    intent.putExtra("ticketCount",ticketCount.toString())
                    intent.putExtra("totalPrice",totalPrice.toString())
                    intent.putExtra("timestamp", time.toDate().time)
                    intent.putExtra("ticketId",ticketId)

                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener { e ->
                    Log.e("PAYMENT", "Error saving payment: ${e.message}")
                }
        }
        else{
            Log.d("DEBUG","ERROR TO FETCH DATA")
        }


    }

    private fun generateTicketCode():String {
        return "TKT-" + UUID.randomUUID().toString().substring(0, 8).uppercase()
    }

}
