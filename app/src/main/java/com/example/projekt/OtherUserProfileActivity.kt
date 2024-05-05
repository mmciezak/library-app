package com.example.projekt

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.projekt.databinding.ActivityOtherUserProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class OtherUserProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOtherUserProfileBinding
    private lateinit var pdfArrayList: ArrayList<ModelPdf>
    private lateinit var adapterPdfFavourite: AdapterPdfFavourite
    private var username=""
    var profileImage=""
    private var uid=""
    var isfriend=false


    private lateinit var firebase: FirebaseAuth

    //lateinit var receiver : BroadcastReceiver


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtherUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        uid = intent.getStringExtra("uid")!!
        username = intent.getStringExtra("username")!!


        firebase = FirebaseAuth.getInstance()
        if(firebase.currentUser != null)
        {
            val ref = FirebaseDatabase.getInstance().getReference("Users")
            ref.child(firebase.uid!!).child("Friends").child(uid)
                .addValueEventListener(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        isfriend = snapshot.exists()
                        if(isfriend)
                        {
                            binding.addFriendBtn.setImageResource(R.drawable.check_icon)
                        }
                        else
                        {
                            binding.addFriendBtn.setImageResource(R.drawable.add_friend_icon)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })
        }

        if(uid==firebase.uid)
        {
            binding.addFriendBtn.visibility = View.GONE
        }


        loadUserDetails()


        binding.back.setOnClickListener {
            onBackPressed()
        }

        binding.addFriendBtn.setOnClickListener {
            //add freidn
            if(firebase.currentUser != null)
            {
                if(isfriend)
                {
                    removeFromFriendList()
                }
                else
                {
                    addToFriendList()
                }
            }
            else
            {
                Toast.makeText(this,"User not logged in", Toast.LENGTH_SHORT).show()
            }

        }

        loadFavourites()


        /*val filter = IntentFilter()
        filter.addAction(Intent.ACTION_POWER_CONNECTED)
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED)
        receiver = object : BroadcastReceiver(){
            override fun onReceive(context: Context?, intent: Intent?) {
                Toast.makeText(context,intent?.action, Toast.LENGTH_SHORT).show()
            }

        }
        registerReceiver(receiver,filter)*/



    }

    /*override fun onDestroy() {
        unregisterReceiver(receiver)
        super.onDestroy()
    }*/

    private fun loadFavourites() {
        pdfArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(uid).child("Favourites")
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

                    adapterPdfFavourite = AdapterPdfFavourite(this@OtherUserProfileActivity, pdfArrayList)
                    binding.favouritesLabelTv.adapter = adapterPdfFavourite
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }

    private fun addToFriendList() {
        Log.v("user profile activity", "adding to friendlist, accessing db")
        val timestamp = System.currentTimeMillis()
        val hashMap = HashMap<String, Any>()
        hashMap["uid"]=uid
        hashMap["timestamp"]=timestamp
        hashMap["username"]=username
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebase.uid!!).child("Friends").child(uid)
            .setValue(hashMap)
            .addOnSuccessListener {
                Log.v("user profile activity", "added to friendlist")
                Toast.makeText(this,"Added to friendlist", Toast.LENGTH_SHORT).show()


            }
            .addOnFailureListener { e->
                Log.v("add to favuirteu", "e: ${e.message}")
            }
    }

    private fun removeFromFriendList() {
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebase.uid!!).child("Friends").child(uid)
            .removeValue().addOnSuccessListener {
                Log.v("user profile activity", "removed from favirtets")
                Toast.makeText(this,"Removed from friend list", Toast.LENGTH_SHORT).show()

            }
            .addOnFailureListener {e->
                Log.v("remove from fariendorutie", "e: ${e.message}")
            }
    }


    private fun loadUserDetails() {
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val username = snapshot.child("username").value.toString()
                    val profileImage = snapshot.child("profileImage").value.toString()


                    binding.nameTv.text = username

                    try{
                        Glide.with(this@OtherUserProfileActivity)
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
    }

}