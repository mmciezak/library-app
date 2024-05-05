package com.example.projekt

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import com.example.projekt.databinding.ActivityPdfDetailBinding
import com.example.projekt.databinding.DialogCommentAddBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PdfDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPdfDetailBinding
    private lateinit var commentArrayList: ArrayList<ModelComment>
    private lateinit var adapterComment: AdapterComment
    private var bookId=""
    var bookTitle=""
    private var isfavourite=false

    private lateinit var firebase: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPdfDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bookId = intent.getStringExtra("bookId")!!

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Loading")
        progressDialog.setCanceledOnTouchOutside(false)

        firebase = FirebaseAuth.getInstance()
        if(firebase.currentUser != null)
        {
            val ref = FirebaseDatabase.getInstance().getReference("Users")
            ref.child(firebase.uid!!).child("Favourites").child(bookId)
                .addValueEventListener(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        isfavourite = snapshot.exists()
                        if(isfavourite)
                        {
                            binding.addToFavourites.setImageResource(R.drawable.star_icon)
                        }
                        else
                        {
                            binding.addToFavourites.setImageResource(R.drawable.star_border_icon)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })
        }


        MyApplication.incrementBookViewCount(bookId)
        loadBookDetails()
        loadComments()


        binding.back.setOnClickListener {
            onBackPressed()
        }

        binding.readbookbutton.setOnClickListener {
            val intent = Intent(this, PdfViewActivity::class.java)
            intent.putExtra("bookId", bookId)
            intent.putExtra("bookTitle", bookTitle)
            startActivity(intent)
        }

        binding.addToFavourites.setOnClickListener {
            if(firebase.currentUser != null)
            {
                if(isfavourite)
                {
                    removeFromFavourites()
                }
                else
                {
                    addToFavourites()
                }
            }
            else
            {
                Toast.makeText(this,"User not logged in", Toast.LENGTH_SHORT).show()
            }
        }

        binding.addComment.setOnClickListener {
            if(firebase.currentUser==null)
            {
                Toast.makeText(this,"User not logged in", Toast.LENGTH_SHORT).show()
            }
            else
            {
                addCommentDialog()
            }
        }
    }

    private fun loadComments() {
        commentArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Books")
        ref.child(bookId).child("Comments")
            .addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    commentArrayList.clear()
                    for(ds in snapshot.children)
                    {
                        val model = ds.getValue(ModelComment::class.java)
                        commentArrayList.add(model!!)

                    }
                    adapterComment = AdapterComment(this@PdfDetailActivity, commentArrayList)
                    binding.commentsRv.adapter = adapterComment
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }

    private var comment=""
    private fun addCommentDialog() {
        val commentAddbinding = DialogCommentAddBinding.inflate(LayoutInflater.from(this))
        val builder = AlertDialog.Builder(this, R.style.CustomDialog)
        builder.setView(commentAddbinding.root)
        val alertDialog = builder.create()
        alertDialog.show()
        commentAddbinding.back.setOnClickListener {
            alertDialog.dismiss()
        }
        commentAddbinding.submitButton.setOnClickListener {
            comment = commentAddbinding.commentEt.text.toString().trim()
            if(comment.isEmpty())
            {
                Toast.makeText(this,"Invalid input", Toast.LENGTH_SHORT).show()

            }
            else
            {
                alertDialog.dismiss()
                addComment()
            }
        }
    }

    private fun addComment() {
        progressDialog.setMessage("Loading")
        progressDialog.show()
        val timestamp = System.currentTimeMillis().toString()
        val hashMap = HashMap<String, Any>()
        hashMap["id"] = timestamp
        hashMap["bookId"] = bookId
        hashMap["comment"] = comment
        hashMap["uid"] = firebase.uid.toString()
        hashMap["timestamp"] = timestamp

        val ref = FirebaseDatabase.getInstance().getReference("Books")
        ref.child(bookId).child("Comments").child(timestamp)
            .setValue(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this,"Comment added", Toast.LENGTH_SHORT).show()

            }
            .addOnFailureListener {e->
                //Toast.makeText(this,"Error", Toast.LENGTH_SHORT).show()
                Log.v("error adding comment", "${e.message}")
                progressDialog.dismiss()
            }

    }

    private fun addToFavourites() {
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

    }


    private fun loadBookDetails() {
        val ref = FirebaseDatabase.getInstance().getReference("Books")
        ref.child(bookId)
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val categoryId = snapshot.child("categoryId").value.toString()
                    val description = snapshot.child("description").value.toString()
                    val downloads = snapshot.child("downloads").value.toString()
                    val views = snapshot.child("views").value.toString()
                    val title = snapshot.child("title").value.toString()
                    val uid = snapshot.child("uid").value.toString()
                    val url = snapshot.child("url").value.toString()
                    val timestamp = snapshot.child("timestamp").value.toString()

                    val date = MyApplication.formatTimeStamp(timestamp.toLong())
                    MyApplication.loadCategory(categoryId, binding.categoryTv)
                    MyApplication.loadPdfFromUrlSinglePage(url,title,binding.pdfView, binding.progressBar,binding.pagesTv)
                    MyApplication.loadPdfSize(url,title,binding.sizeTv)

                    binding.titleTv.text = title
                    binding.descriptionTv.text = description
                    binding.viewsTv.text = views
                    binding.downloadsTv.text = downloads
                    binding.dateTv.text = date

                    bookTitle = title
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }
}