package com.example.projekt

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.projekt.databinding.ActivityLoginBinding
import com.example.projekt.databinding.ActivityRegisterBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LoginActivity : AppCompatActivity() {


    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebase: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebase = FirebaseAuth.getInstance()
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.signUp.setOnClickListener{
            //go to register page
            startActivity(Intent(this,RegisterActivity::class.java))

        }

        binding.loginButton.setOnClickListener{

            validateData()
            login()
        }
    }

    var email=""
    var password=""
    private fun validateData()
    {

        email = binding.emailInput.text.toString().trim()
        password = binding.passwordInput.text.toString().trim()
    }

    fun login()
    {
        if(email.isNotEmpty()|| password.isNotEmpty())
        {
            progressDialog.setMessage("Logging in")
            progressDialog.show()

            firebase.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    //Toast.makeText(this,"Login successful",Toast.LENGTH_SHORT).show()
                    progressDialog.setMessage("Logging in")
                    val firebaseUser = firebase.currentUser!!
                    val reference = FirebaseDatabase.getInstance().getReference("Users")
                    reference.child(firebaseUser.uid)
                        .addListenerForSingleValueEvent(object : ValueEventListener{
                            override fun onCancelled(error: DatabaseError) {

                            }

                            override fun onDataChange(snapshot: DataSnapshot) {
                                progressDialog.dismiss()
                                val userType = snapshot.child("admin").value
                                //Log.v("userType", userType.toString())
                                //if(userType="user)...
                                val intent = Intent(this@LoginActivity, AdminPage::class.java)
                                //intent.putExtra("uid", uid)
                                intent.putExtra("userType", userType.toString())
                                startActivity(intent)

                                startActivity(Intent(this@LoginActivity, AdminPage::class.java))
                                /*if (userType.toString() == "1")
                                {
                                    startActivity(Intent(this@LoginActivity, AdminPage::class.java))
                                }
                                else
                                {
                                    startActivity(Intent(this@LoginActivity, UserPage::class.java))
                                }*/
                                finish()
                            }
                        })
                }
                .addOnCanceledListener {
                    Toast.makeText(this,"Login cancelled",Toast.LENGTH_SHORT).show()
                    progressDialog.dismiss()
                }
                .addOnFailureListener {
                    Toast.makeText(this,"Login failure",Toast.LENGTH_SHORT).show()
                    progressDialog.dismiss()
                }
        }
        else
        {
            Toast.makeText(this,"Invalid input",Toast.LENGTH_SHORT).show()
        }

    }
}