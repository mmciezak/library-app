package com.example.projekt

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.projekt.databinding.ActivityAdminPageBinding
import com.example.projekt.databinding.ActivityShowUserProfilesBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ShowUserProfilesActivity : AppCompatActivity() {
    private lateinit var binding : ActivityShowUserProfilesBinding
    private lateinit var firebase: FirebaseAuth
    private lateinit var usersArrayList: ArrayList<ModelUser>
    private lateinit var adapterUsers: AdapterUsers
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowUserProfilesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebase = FirebaseAuth.getInstance()
        val user = firebase.currentUser
        if(user==null)
        {
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }

        binding.back.setOnClickListener {
            onBackPressed()
        }



        loadUserProfiles()
    }

    private fun loadUserProfiles() {
        usersArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                usersArrayList.clear()
                for(ds in snapshot.children)
                {
                    val model = ds.getValue(ModelUser::class.java)
                    usersArrayList.add(model!!)

                }
                adapterUsers = AdapterUsers(this@ShowUserProfilesActivity, usersArrayList)
                binding.userList.adapter = adapterUsers
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
}