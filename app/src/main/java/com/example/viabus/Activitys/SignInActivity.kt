package com.example.viabus.Activitys

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.viabus.MainActivity
import com.example.viabus.R
import com.example.viabus.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignInActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivitySignInBinding.inflate(layoutInflater)
    }
    private lateinit var db:FirebaseFirestore
    private lateinit var auth :FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth=FirebaseAuth.getInstance()
        db=FirebaseFirestore.getInstance()
        binding.continuee.setOnClickListener {
            val email=binding.email.text.toString()
            val password=binding.password.text.toString()

            if (email.equals("") or password.equals("")){
                Toast.makeText(this, "Fill the all fields", Toast.LENGTH_SHORT).show()
            }
            else{
                auth.signInWithEmailAndPassword(email,password).addOnCompleteListener {
                    if (it.isSuccessful){
                        startActivity(Intent(this, MainActivity::class.java))
                        Toast.makeText(this, "Sign in Successful", Toast.LENGTH_SHORT).show()
                    }
                    else{
                        Toast.makeText(this, "something went error", Toast.LENGTH_SHORT).show()
                    }
                }
                    .addOnFailureListener {
                        Toast.makeText(this, "${it.localizedMessage}", Toast.LENGTH_SHORT).show()
                    }
            }
        }

    }
}