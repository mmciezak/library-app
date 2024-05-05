package com.example.projekt

import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.widget.PopupMenu
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.example.projekt.databinding.ActivityProfileEditBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class ProfileEditActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileEditBinding
    private lateinit var firebase: FirebaseAuth
    private var imageUri: Uri?=null
    private lateinit var progressDialog: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Loading")
        progressDialog.setCanceledOnTouchOutside(false)

        firebase = FirebaseAuth.getInstance()
        loadUserInfo()

        binding.back.setOnClickListener {
            onBackPressed()
        }

        binding.profileIv.setOnClickListener {
            showImageAttachMenu()
        }

        binding.update.setOnClickListener {
            validateData()
        }
    }

    private var name = ""
    private fun validateData() {
        name = binding.nameEt.text.toString().trim()
        if(name.isEmpty())
        {
            Toast.makeText(this,"Invalid input", Toast.LENGTH_SHORT).show()
        }
        else
        {
            if(imageUri == null)
            {
                updateProfile("")
            }
            else
            {
                uploadImage()
            }
        }
    }

    private fun uploadImage() {
        progressDialog.setMessage("Uploading")
        progressDialog.show()

        val filePathAndName = "ProfileImages/"+firebase.uid
        val reference = FirebaseStorage.getInstance().getReference(filePathAndName)
        reference.putFile(imageUri!!)
            .addOnSuccessListener {taskSnapshot->
                val uriTask: Task<Uri> = taskSnapshot.storage.downloadUrl
                while(!uriTask.isSuccessful);
                val uploadedImageUrl = uriTask.result.toString()
                updateProfile(uploadedImageUrl)

                progressDialog.dismiss()
                Toast.makeText(this,"Image uploaded", Toast.LENGTH_SHORT).show()

            }
            .addOnFailureListener{e->
                progressDialog.dismiss()
                //Toast.makeText(this,"Error", Toast.LENGTH_SHORT).show()
                Log.v("profile edit activity","${e.message}")

            }
    }

    private fun updateProfile(imageUrl: String) {
        progressDialog.setMessage("Updating profile")
        val hashMap: HashMap<String, Any> = HashMap()
        hashMap["username"] = "$name"
        if(imageUri !=null)
        {
            hashMap["profileImage"] = imageUrl
        }

        val reference = FirebaseDatabase.getInstance().getReference("Users")
        reference.child(firebase.uid!!)
            .updateChildren(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this,"Profile updated", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {e->
                progressDialog.dismiss()
                Toast.makeText(this,"Error", Toast.LENGTH_SHORT).show()
                Log.v("profile edit activity","${e.message}")

            }
    }

    private fun loadUserInfo() {
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebase.uid!!)
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val username = snapshot.child("username").value.toString()
                    val profileImage = snapshot.child("profileImage").value.toString()
                    //val timestamp = snapshot.child("timestamp").value.toString()


                    //val date = MyApplication.formatTimeStamp(timestamp.toLong())
                    binding.nameEt.setText(username)

                    try{
                        Glide.with(this@ProfileEditActivity)
                            .load(profileImage).placeholder(R.drawable.user_icon)
                            .into(binding.profileIv)
                    }
                    catch (e:Exception)
                    {
                        Log.v("load user info","${e.message}")
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }

    private fun showImageAttachMenu(){
        val popupMenu = PopupMenu(this,binding.profileIv)
        popupMenu.menu.add(Menu.NONE,0,0,"Camera")
        popupMenu.menu.add(Menu.NONE,1,1,"Gallery")
        popupMenu.show()

        popupMenu.setOnMenuItemClickListener { item->
            val id = item.itemId
            if(id==0)
            {
                pickImageCamera()
            }
            else if(id==1)
            {
                pickImageGallery()
            }
            true
        }
    }

    private fun pickImageGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        galleryActivityResultLauncher.launch(intent)
    }

    private fun pickImageCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "Temp_Title")
        values.put(MediaStore.Images.Media.DESCRIPTION, "Temp_Description")
        imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        cameraActivityResultLauncher.launch(intent)
    }

    private val cameraActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback <ActivityResult>{result ->
            if(result.resultCode == Activity.RESULT_OK)
            {
                val data = result.data
                //imageUri = data!!.data
                binding.profileIv.setImageURI(imageUri)
            }
            else
            {
                Toast.makeText(this,"error", Toast.LENGTH_SHORT).show()
            }

        }
    )

    private val galleryActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback <ActivityResult>{result ->
            if(result.resultCode == Activity.RESULT_OK)
            {
                val data = result.data
                imageUri = data!!.data
                binding.profileIv.setImageURI(imageUri)
            }
            else
            {
                Toast.makeText(this,"error", Toast.LENGTH_SHORT).show()
            }

        }
    )
}