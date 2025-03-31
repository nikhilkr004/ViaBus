package com.example.viabus.Activitys

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.viabus.Adapters.AllTicketsAdapter
import com.example.viabus.DataClass.Ticket
import com.example.viabus.R
import com.example.viabus.databinding.ActivityAllTicketsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AllTicketsActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityAllTicketsBinding.inflate(layoutInflater)
    }
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private val tickets = mutableListOf<Ticket>()
    private lateinit var ticketsAdapter: AllTicketsAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        ticketsAdapter = AllTicketsAdapter(tickets)
        val recyclerview = binding.ticketsRecyclerview
        recyclerview.layoutManager = LinearLayoutManager(this)
        recyclerview.adapter = ticketsAdapter
        fetchTickets()

        ///back button background

        binding.imageButton.setOnClickListener {
            finish()
        }
    }

    private fun fetchTickets() {
        db.collection("user").document(auth.currentUser!!.uid)
            .collection("Tickets")
            .addSnapshotListener { snapshot, _ ->
                tickets.clear()

                if (snapshot != null) {
                    val ticketsList = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(Ticket::class.java)?.copy(ticketId = doc.id)
                    }

                    ///sort by time
                    val sortedTickets = ticketsList.sortedByDescending { it.timestamp }

                    tickets.addAll(sortedTickets)
                    ticketsAdapter.notifyDataSetChanged()
                }

            }
    }
}