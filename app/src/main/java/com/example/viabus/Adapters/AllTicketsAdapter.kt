package com.example.viabus.Adapters

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.viabus.DataClass.Ticket
import com.example.viabus.R
import com.example.viabus.databinding.TicketsItemBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AllTicketsAdapter(private val tickets: List<Ticket>) :
    RecyclerView.Adapter<AllTicketsAdapter.ViewHolder>() {

    class ViewHolder(private val binding: TicketsItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(ticket: Ticket) {
            val auth = FirebaseAuth.getInstance().currentUser?.uid ?: return

            binding.busNuber.text = ticket.busNumber.toString()
            binding.busRoute.text = ticket.busRoutes.toString()
            binding.fair.text = ticket.totalPrice.toString()
            binding.ticketCount.text = ticket.ticketCount.toString()
            binding.endingStop.text = ticket.toStop.toString()
            binding.startingStop.text = ticket.fromStop.toString()
            binding.ticketStatus.text=ticket.status.toString()

            if (ticket.status=="active"){
                binding.ticketStatus.setBackgroundResource(R.drawable.bus_route_back)
            }
            else{
                binding.ticketStatus.setBackgroundResource(R.drawable.expired_ticket)
            }
            // Check if ticket is expired
            checkTicketExpiration(auth, ticket.ticketId ?: return)
        }

        private fun checkTicketExpiration(userId: String, ticketId: String) {
            val db = FirebaseFirestore.getInstance()
            val ticketRef = db.collection("user").document(userId)
                .collection("Tickets").document(ticketId)

            ticketRef.get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val ticketTime = document.getLong("ticketTime") ?: 0L
                        val currentTime = System.currentTimeMillis()
                        val ticketExpirationTime = 1 * 60 * 1000 // 1 minute in milliseconds

                        if (currentTime - ticketTime >= ticketExpirationTime) {
                            // 🔹 Ticket Expired, Update Status
                            ticketRef.update("status", "expired")
                                .addOnSuccessListener {
                                    Log.d("Firestore", "Ticket expired successfully")
                                }
                                .addOnFailureListener { e ->
                                    Log.e("Firestore", "Error updating ticket status", e)
                                }
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("Firestore", "Error fetching ticket", e)
                }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = TicketsItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return tickets.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ticket = tickets[position]
        holder.bind(ticket)
    }
}
