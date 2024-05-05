package com.example.projekt

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.projekt.databinding.RowUserBinding
import com.google.firebase.database.FirebaseDatabase

class AdapterUsers:RecyclerView.Adapter<AdapterUsers.HolderUsers>{//filterable (?)
    private val context: Context
    var usersArrayList: ArrayList<ModelUser>
    //private var filterList: ArrayList<ModelUser>
    //private var filter: FilterCategory? = null
    private lateinit var binding: RowUserBinding


    constructor(context: Context, usersArrayList: ArrayList<ModelUser>) {
        this.context = context
        this.usersArrayList = usersArrayList
        //this.filterList = usersArrayList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderUsers {
        binding = RowUserBinding.inflate(LayoutInflater.from(context),parent,false)
        return HolderUsers(binding.root)
    }

    override fun getItemCount(): Int {
        return usersArrayList.size
    }

    override fun onBindViewHolder(holder: HolderUsers, position: Int) {
        val model = usersArrayList[position]
        val username = model.username
        val profileImage = model.profileImage
        val uid = model.uid


        holder.usernameTv.text = username

        /*holder.addFriendBtn.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Add friend")
                .setMessage("Add user to friend list?")
                .setPositiveButton("Confirm"){a, d->
                    Toast.makeText(context,"Adding user to friendlist", Toast.LENGTH_SHORT).show()
                    //addFriend(model, holder)
                }
                .setNegativeButton("Cancel"){a, d->
                    a.dismiss()
                }
                .show()
        }*/

        /*holder.deleteBtn.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Delete")
                .setMessage("Delete category?")
                .setPositiveButton("Confirm"){a, d->
                    Toast.makeText(context,"Deleting", Toast.LENGTH_SHORT).show()
                    deleteCategory(model, holder)
                }
                .setNegativeButton("Cancel"){a, d->
                    a.dismiss()
                }
                .show()
        }*/

        //handle click
        holder.itemView.setOnClickListener{
            val intent = Intent(context, OtherUserProfileActivity::class.java)
            intent.putExtra("uid", uid)
            intent.putExtra("username", username)
            context.startActivity(intent)
        }

    }
//add freind
    /*private fun deleteCategory(model: ModelCategory, holder:HolderUsers) {
        val id = model.id
        val ref = FirebaseDatabase.getInstance().getReference("Categories")
        ref.child(id)
            .removeValue()
            .addOnSuccessListener {
                Toast.makeText(context,"Deleted succesfully", Toast.LENGTH_SHORT).show()

            }
            .addOnFailureListener { e->
                Toast.makeText(context,"Error deleting", Toast.LENGTH_SHORT).show()

            }
    }*/

    inner class HolderUsers(itemView: View): RecyclerView.ViewHolder(itemView){
        //var profileImage
        var usernameTv:TextView = binding.usernameTv
        //var addFriendBtn:ImageButton = binding.addFriendBtn
    }

    /*private fun addToFavourites() {
        Log.v("pdf detail activity", "adding to favourites, accessing db")
        val timestamp = System.currentTimeMillis()
        val hashMap = HashMap<String, Any>()
        hashMap["bookId"]=bookId
        hashMap["timestamp"]=timestamp
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebase.uid!!).child("Favourites").child(bookId)
            .setValue(hashMap)
            .addOnSuccessListener {
                Log.v("pdf detail activity", "added to favourites")
                Toast.makeText(this,"Added to favourites", Toast.LENGTH_SHORT).show()


            }
            .addOnFailureListener { e->
                Log.v("add to favuirteu", "e: ${e.message}")
            }
    }

    private fun removeFromFavourites() {
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebase.uid!!).child("Favourites").child(bookId)
            .removeValue().addOnSuccessListener {
                Log.v("pdf detail activity", "removed from favirtets")
                Toast.makeText(this,"Removed from favourites", Toast.LENGTH_SHORT).show()

            }
            .addOnFailureListener {e->
                Log.v("remove from favorutie", "e: ${e.message}")
            }

    }*/


}