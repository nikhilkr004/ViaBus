package com.example.viabus.Activitys

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.viabus.DataClass.UserData
import com.example.viabus.MainActivity
import com.example.viabus.R
import com.example.viabus.Utils
import com.example.viabus.databinding.ActivitySignInBinding
import com.example.viabus.databinding.ActivitySignUpBinding
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignUpActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivitySignUpBinding.inflate(layoutInflater)
    }
    private lateinit var auth:FirebaseAuth
    private lateinit var db:FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        auth= FirebaseAuth.getInstance()
        db=FirebaseFirestore.getInstance()



        binding.continuee.setOnClickListener {
            val name=binding.name.text.toString()
            val email=binding.email.text.toString()
            val password=binding.password.text.toString()

            if (name.equals("") or email.equals("") or password.equals("")){
                Toast.makeText(this, "fill all the field", Toast.LENGTH_SHORT).show()
            }
            else{
                Utils.showDialog(this,"signing up ...")
                signUpWithEmailPassword(name,email,password)
            }
        }

    }

    private fun signUpWithEmailPassword(name: String, email: String, password: String) {

        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {


            if (it.isSuccessful){
                val userData=UserData(
                    name =name,
                    email=email,
                    userId=auth.currentUser!!.uid,
                    date = Timestamp.now()
                )

                db.collection("user").document(auth.currentUser!!.uid).set(userData).addOnSuccessListener {

                    Toast.makeText(this, "sign up successful", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            }
            else{
                Toast.makeText(this, "something went error", Toast.LENGTH_SHORT).show()
                Utils.dismissDialog()
            }
        }
            .addOnFailureListener {
                Utils.dismissDialog()
                Toast.makeText(this, "${it.localizedMessage}", Toast.LENGTH_SHORT).show()
            }

    }
}