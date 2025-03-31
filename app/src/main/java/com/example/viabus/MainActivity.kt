package com.example.viabus

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.viabus.Activitys.AllTicketsActivity
import com.example.viabus.Activitys.BuyTicketActivity
import com.example.viabus.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
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

        /// all data binding is defined here
        initBinding()
    }

    private fun initBinding() {
        binding.bookTicketBtn.setOnClickListener {
            startActivity(Intent(this, BuyTicketActivity::class.java))
        }

        binding.viewTicketsActivity.setOnClickListener {
            startActivity(Intent(this, AllTicketsActivity::class.java))
        }
    }

}