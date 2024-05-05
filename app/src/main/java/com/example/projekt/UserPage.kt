package com.example.projekt

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.projekt.databinding.ActivityAdminPageBinding
import com.example.projekt.databinding.ActivityUserPageBinding
import com.google.firebase.auth.FirebaseAuth

class UserPage : AppCompatActivity() {

    private lateinit var binding : ActivityUserPageBinding
    private lateinit var firebase: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebase = FirebaseAuth.getInstance()
        //check user
        val user = firebase.currentUser
        if(user==null)
        {
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }
        else
        {
            //val email = user.email
            binding.userInfo.text = "Welcome, ${user.email}"

        }

        binding.logout.setOnClickListener {
            firebase.signOut()
            startActivity(Intent(this,MainActivity::class.java))
        }
    }
}