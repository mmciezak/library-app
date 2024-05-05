package com.example.projekt

import android.app.AlertDialog
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.projekt.databinding.ActivityPdfAddBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.ActivityResult
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FirebaseStorage

class PdfAddActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPdfAddBinding
    private lateinit var firebase: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog
    private lateinit var categoryArrayList: ArrayList<ModelCategory>
    private var pdfUri: Uri? = null
    private val TAG = "PDF_ADD_TAG"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPdfAddBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebase = FirebaseAuth.getInstance()
        loadPdfCategories()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Loading")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.selectCategory.setOnClickListener{
            categoryPickDialog()
        }

        binding.attachment.setOnClickListener {
            pdfPickIntent()
        }

        binding.submitButton.setOnClickListener {
            validateData()
        }

        binding.back.setOnClickListener {
            onBackPressed()
        }
    }

    private var title=""
    private var description=""
    private var category=""

    private fun validateData() {
        Log.v(TAG,"validate data")
        title = binding.booktitleInput.text.toString().trim()
        description = binding.descriptionInput.text.toString().trim()
        category = binding.selectCategory.text.toString().trim()

        if(title.isEmpty() || description.isEmpty() || category.isEmpty() || pdfUri==null)
        {
            Toast.makeText(this, "Invalid input", Toast.LENGTH_SHORT).show()
        }
        else
        {
            //upload pdf
            Log.v(TAG,"uploading pdf start")
            progressDialog.setMessage("Uploading pdf")
            progressDialog.show()
            val timestamp = System.currentTimeMillis()
            val filePathAndName = "Books/$timestamp"
            val storageReference = FirebaseStorage.getInstance().getReference(filePathAndName)
            storageReference.putFile(pdfUri!!)
                .addOnSuccessListener {taskSnapshot ->
                    Toast.makeText(this, "File uploaded", Toast.LENGTH_SHORT).show()
                    //progressDialog.dismiss()
                    val uriTask: Task<Uri> = taskSnapshot.storage.downloadUrl
                    while(!uriTask.isSuccessful);
                    val uploadedPdfUrl = "${uriTask.result}"

                    //upload info to db
                    Log.v(TAG,"uploading info to db")
                    progressDialog.setMessage("Uploading")
                    val uid = firebase.uid//current user
                    val hashMap: HashMap<String, Any> = HashMap()
                    hashMap["uid"] = "$uid"
                    hashMap["id"] = "$timestamp"
                    hashMap["title"] = "$title"
                    hashMap["description"] = "$description"
                    hashMap["categoryId"] = "$selectedCategoryId"
                    hashMap["url"] = "$uploadedPdfUrl"
                    hashMap["timestamp"] = timestamp
                    hashMap["views"] = 0
                    hashMap["downloads"] = 0

                    val ref = FirebaseDatabase.getInstance().getReference("Books")
                    ref.child("$timestamp")
                        .setValue(hashMap)
                        .addOnSuccessListener {
                            Log.v(TAG,"uploaded info to db")
                            progressDialog.dismiss()
                            Toast.makeText(this, "Uploading complete", Toast.LENGTH_SHORT).show()
                            pdfUri = null
                        }
                        .addOnFailureListener {e->
                            Toast.makeText(this, "Error - ${e}", Toast.LENGTH_SHORT).show()
                            progressDialog.dismiss()
                        }

                }
                .addOnFailureListener{e ->
                    Toast.makeText(this, "Error - ${e}", Toast.LENGTH_SHORT).show()
                    progressDialog.dismiss()
                }
        }

    }

    private fun loadPdfCategories() {
        Log.v(TAG,"Loading categories")
        categoryArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Categories")
        ref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                categoryArrayList.clear()
                for(ds in snapshot.children)
                {
                    val model = ds.getValue(ModelCategory::class.java)
                    categoryArrayList.add(model!!)
                    Log.v(TAG,"onDataChange: ${model.category}")
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })


    }

    private var selectedCategoryId = ""
    private var selectedCategoryTitle = ""

    private fun categoryPickDialog(){
        Log.v(TAG,"Showing category pick dialog")
        val categoriesArray = arrayOfNulls<String>(categoryArrayList.size)
        for(i in categoryArrayList.indices)
        {
            categoriesArray[i] = categoryArrayList[i].category
        }

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Pick category")
            .setItems(categoriesArray){
                dialog, which->
                selectedCategoryTitle = categoryArrayList[which].category
                selectedCategoryId = categoryArrayList[which].id

                binding.selectCategory.text = selectedCategoryTitle
                Log.v(TAG,"Selected category: $selectedCategoryId")


            }.show()
    }

    private fun pdfPickIntent(){
        Log.v(TAG,"starting pdf picking")
        val intent = Intent()
        intent.type = "application/pdf"
        intent.action = Intent.ACTION_GET_CONTENT
        pdfActivityResultLauncher.launch(intent)
    }

    val pdfActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback<ActivityResult>{
            result ->
            if(result.resultCode == RESULT_OK)
            {
                Log.v(TAG,"RESULT OK")
                pdfUri = result.data!!.data
            }
            else
            {
                Log.v(TAG,"CANCELLED")
                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    )

}