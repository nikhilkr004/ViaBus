package com.example.viabus.Adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.viabus.Activitys.ConfirmTicketActivity
import com.example.viabus.DataClass.Bus
import com.example.viabus.databinding.BusRoutesItemBinding

class BusRouteAdapter(val data:List<Bus>):RecyclerView.Adapter<BusRouteAdapter.ViewHolder>(){
    class ViewHolder(val binding: BusRoutesItemBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(data: Bus) {
            val context=binding.root.context
            binding.busRouteItem.text=data.busRoute

            binding.root.setOnClickListener {
                val intent=Intent(context,ConfirmTicketActivity::class.java)
                intent.putExtra("route",data.busRoute)
                intent.putExtra("busNo",data.busNumber)
                intent.putExtra("busType",data.busType)
                context.startActivity(intent)
            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater=LayoutInflater.from(parent.context)
        val binding =BusRoutesItemBinding.inflate(inflater,parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data=data[position]
        holder.bind(data)
    }

}

