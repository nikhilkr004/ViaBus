package com.example.viabus

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.WindowManager
import com.example.viabus.databinding.CustomlodingdialogBinding

object Utils {
    private var dialog: AlertDialog? = null  // Store reference for dismissing

    fun showDialog(context: Context, message: String) {
        val binding = CustomlodingdialogBinding.inflate(LayoutInflater.from(context))

        binding.title.text = message  // Set message dynamically

        val builder = AlertDialog.Builder(context).apply {
            setView(binding.root)
            setCancelable(false) // Prevent user from dismissing by clicking outside
        }

        dialog = builder.create().apply {
            window?.setBackgroundDrawableResource(android.R.color.transparent)  // Transparent background
            window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND) // Optional: Remove dim effect
            show()
        }
    }

    fun dismissDialog() {
        dialog?.dismiss()
        dialog = null
    }
}