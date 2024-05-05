package com.example.projekt

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.ProgressBar
import android.widget.Toast
import com.example.projekt.databinding.ActivityAddCategoryBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class AddCategory : AppCompatActivity() {

    private lateinit var binding: ActivityAddCategoryBinding
    private lateinit var firebase: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebase = FirebaseAuth.getInstance()
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Loading")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.back.setOnClickListener {
            onBackPressed()
        }

        binding.add.setOnClickListener {
            submitData()
        }
    }

    var name=""
    private fun submitData()
    {
        name = binding.categoryInput.text.toString().trim()
        if(name.isEmpty())
        {
            Toast.makeText(this,"Invalid input", Toast.LENGTH_SHORT).show()
        }
        else
        {
            progressDialog.show()
            val timestamp = System.currentTimeMillis()
            val hashMap = HashMap<String, Any>()
            hashMap["id"] = "$timestamp"
            hashMap["category"] = name
            hashMap["timestamp"] = timestamp
            hashMap["uid"] = "${firebase.uid}"

            val ref = FirebaseDatabase.getInstance().getReference("Categories")
            ref.child("$timestamp")
                .setValue(hashMap)
                .addOnSuccessListener {
                    progressDialog.dismiss()
                    Toast.makeText(this,"Added $name", Toast.LENGTH_SHORT).show()

                }
                .addOnFailureListener{
                    progressDialog.dismiss()
                    Toast.makeText(this,"Error", Toast.LENGTH_SHORT).show()

                }
        }






    }
}