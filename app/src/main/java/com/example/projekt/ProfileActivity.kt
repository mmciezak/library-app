package com.example.projekt

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Log
import com.bumptech.glide.Glide
import com.example.projekt.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var firebase: FirebaseAuth
    private lateinit var pdfArrayList: ArrayList<ModelPdf>
    private lateinit var adapterPdfFavourite: AdapterPdfFavourite
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebase = FirebaseAuth.getInstance()
        loadUserInfo()
        binding.back.setOnClickListener {
            onBackPressed()
        }

        binding.profileEdit.setOnClickListener {
            startActivity(Intent(this, ProfileEditActivity::class.java))

        }

        binding.viewUserProfiles.setOnClickListener {
            startActivity(Intent(this, ShowUserProfilesActivity::class.java))
        }

        loadFavourites()

    }

    private fun loadFavourites() {
        pdfArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebase.uid!!).child("Favourites")
            .addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                pdfArrayList.clear()
                for(ds in snapshot.children)
                {
                    val bookId = "${ds.child("bookId").value}"
                    val modelPdf = ModelPdf()
                    modelPdf.id = bookId

                    pdfArrayList.add(modelPdf)
                }
                binding.favouriteBooksCountTv.text = pdfArrayList.size.toString()

                adapterPdfFavourite = AdapterPdfFavourite(this@ProfileActivity, pdfArrayList)
                binding.favouritesLabelTv.adapter = adapterPdfFavourite
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }


    private fun loadUserInfo() {
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebase.uid!!)
            .addValueEventListener(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val email = snapshot.child("email").value.toString()
                    val username = snapshot.child("username").value.toString()
                    val profileImage = snapshot.child("profileImage").value.toString()
                    val timestamp = snapshot.child("timestamp").value.toString()
                    val uid = snapshot.child("uid").value.toString()
                    val admin = snapshot.child("admin").value.toString()
                    var date=""

                    if(timestamp.isNullOrEmpty())
                    {
                        date = MyApplication.formatTimeStamp(timestamp.toLong())

                    }
                    binding.nameTv.text = username
                    binding.emailTv.text = email
                    binding.dateTv.text = date
                    if(admin=="1")
                    {
                        binding.accountTypeTv.text = "admin"
                    }
                    else
                    {
                        binding.accountTypeTv.text = "user"
                    }


                    try{
                        Glide.with(this@ProfileActivity)
                            .load(profileImage).placeholder(R.drawable.user_icon)
                            .into(binding.profileIv)
                    }
                    catch (e:Exception)
                    {
                        Log.v("load user info","${e.message}")
                    }

                }

                override fun onCancelled(error: DatabaseError) {

                }

            })


        val ref2 = FirebaseDatabase.getInstance().getReference("Users")
        ref2.child(firebase.uid!!).child("Friends")
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val friendList = mutableListOf<String>()

                    /*for (friendSnapshot in snapshot.children) {
                        val username = friendSnapshot.child("username").getValue(ModelUser::class.java)
                        friendList.add(username.toString())

                    }*/

                    for(ds in snapshot.children)
                    {
                        val model = ds.getValue(ModelUser::class.java)
                        friendList.add(model!!.username)

                    }


                    val friendListText = friendList.joinToString("\n")
                    Log.v("friendslist","${friendListText}")
                    //binding.viewFriendList.text = "Friends\n"+friendListText
                    binding.viewFriendList.text = "$friendListText"

                }

                override fun onCancelled(error: DatabaseError) {
                    Log.v("friendslist","blont")
                }
            })

    }
}