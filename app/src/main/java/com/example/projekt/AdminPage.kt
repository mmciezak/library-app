package com.example.projekt

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.core.widget.addTextChangedListener
import com.example.projekt.databinding.ActivityAdminPageBinding
import com.example.projekt.databinding.ActivityMainBinding
import com.example.projekt.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.lang.Exception

class AdminPage : AppCompatActivity() {

    private lateinit var binding : ActivityAdminPageBinding
    private lateinit var firebase: FirebaseAuth
    private lateinit var categoryArrayList: ArrayList<ModelCategory>
    private lateinit var adapterCategory: AdapterCategory
    var admin=""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminPageBinding.inflate(layoutInflater)
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
            val firebaseUser = firebase.currentUser!!
            val reference = FirebaseDatabase.getInstance().getReference("Users")
            reference.child(firebaseUser.uid)
                .addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onCancelled(error: DatabaseError) {

                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        val userType = snapshot.child("admin").value
                        admin=userType.toString()
                        //Log.v("userType", userType.toString())
                        //if(userType="user)...
                        //tartActivity(Intent(this@LoginActivity, AdminPage::class.java))
                        if (userType.toString() != "1")
                        {
                            binding.addcategory.visibility = View.GONE
                            binding.addpdf.visibility = View.GONE
                        }


                    }
                })

        }

        binding.logout.setOnClickListener {
            firebase.signOut()
            startActivity(Intent(this,MainActivity::class.java))
        }

        binding.addcategory.setOnClickListener {
            startActivity(Intent(this,AddCategory::class.java))
        }

        binding.addpdf.setOnClickListener {
            startActivity(Intent(this,PdfAddActivity::class.java))
        }

        binding.profile.setOnClickListener {
            startActivity(Intent(this,ProfileActivity::class.java))

        }

        loadCategories()

        //search
        binding.searchEt.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                try{
                    adapterCategory.filter.filter(s)
                }catch (e: Exception){

                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

    }

    private fun loadCategories() {
        categoryArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Categories")
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                categoryArrayList.clear()
                for(ds in snapshot.children)
                {
                    val model = ds.getValue(ModelCategory::class.java)
                    categoryArrayList.add(model!!)

                }
                adapterCategory = AdapterCategory(this@AdminPage, categoryArrayList,admin)
                binding.categoriesRv.adapter = adapterCategory
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
}