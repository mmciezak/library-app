package com.example.projekt

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private lateinit var firebase: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_splash)
        firebase = FirebaseAuth.getInstance()

        Handler().postDelayed(Runnable {
            //startActivity(Intent(this,MainActivity::class.java))
            val user = firebase.currentUser
            if(user==null)
            {
                startActivity(Intent(this,MainActivity::class.java))
                finish()
            }
            else
            {
                //startActivity(Intent(this,UserPage::class.java))
                val firebaseUser = firebase.currentUser!!
                val reference = FirebaseDatabase.getInstance().getReference("Users")
                reference.child(firebaseUser.uid)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(error: DatabaseError) {

                        }

                        override fun onDataChange(snapshot: DataSnapshot) {
                            val userType = snapshot.child("admin").value
                            startActivity(Intent(this@SplashActivity, AdminPage::class.java))

                            //if(userType="user)...
                            /*if (userType.toString() == "1")
                            {
                                startActivity(Intent(this@SplashActivity, AdminPage::class.java))
                            }
                            else
                            {
                                startActivity(Intent(this@SplashActivity, UserPage::class.java))
                            }*/
                            finish()
                        }
                    })
            }
        }, 2000)
    }
}

