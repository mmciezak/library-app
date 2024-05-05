package com.example.projekt

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projekt.databinding.RowCommentBinding
import com.example.projekt.databinding.RowPdfAdminBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdapterComment: RecyclerView.Adapter<AdapterComment.HolderComment>{
    private var context: Context
    var commentArrayList: ArrayList<ModelComment>
    private lateinit var binding: RowCommentBinding
    private lateinit var firebase: FirebaseAuth

    constructor(context: Context, commentArrayList: ArrayList<ModelComment>){
        this.context = context
        this.commentArrayList = commentArrayList
        firebase = FirebaseAuth.getInstance()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderComment {
        binding = RowCommentBinding.inflate(LayoutInflater.from(context),parent,false)
        return HolderComment(binding.root)
    }

    override fun getItemCount(): Int {
        return commentArrayList.size
    }

    override fun onBindViewHolder(holder: HolderComment, position: Int) {
        val model = commentArrayList[position]
        val id = model.id
        val bookId = model.bookId
        val comment = model.comment
        val uid = model.uid
        val timestamp = model.timestamp

        val date = MyApplication.formatTimeStamp(timestamp.toLong())

        holder.dateTv.text = date
        holder.commentTv.text = comment
        loadUserDetails(model, holder)
        holder.itemView.setOnClickListener {
            //delete a comment(?)
        }
    }

    private fun loadUserDetails(model: ModelComment, holder: AdapterComment.HolderComment) {
        val uid = model.uid
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(uid)
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val name = snapshot.child("username").value.toString()
                    val profileImage = snapshot.child("profileImage").value.toString()

                    holder.nameTv.text = name
                    try{
                        Glide.with(context)
                            .load(profileImage)
                            .placeholder(R.drawable.user_grey_icon)
                            .into(holder.profileIv)
                    }
                    catch (e:Exception)
                    {
                        holder.profileIv.setImageResource(R.drawable.user_grey_icon)
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }


    inner class HolderComment(itemView: View):RecyclerView.ViewHolder(itemView)
    {
        val nameTv = binding.nameTv
        val profileIv = binding.profileIv
        val dateTv = binding.dateTv
        val commentTv = binding.commentTv



    }


}