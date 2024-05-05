package com.example.projekt

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.compose.ui.res.booleanResource
import com.example.projekt.databinding.ActivityPdfViewBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class PdfViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPdfViewBinding
    var bookId = ""
    var bookTitle = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPdfViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bookId = intent.getStringExtra("bookId")!!
        bookTitle = intent.getStringExtra("bookTitle")!!
        loadBookDetails()

        binding.back.setOnClickListener {
            onBackPressed()
        }

        binding.toolbarTitle.text = "Reading: $bookTitle"
    }

    private fun loadBookDetails() {
        Log.v("pdf view activity", "load book details")
        val ref = FirebaseDatabase.getInstance().getReference("Books")
        ref.child(bookId)
            .addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val pdfUrl = snapshot.child("url").value
                    Log.d("pdf view activity", "get url: $pdfUrl")
                    loadBookFromUrl(pdfUrl.toString())
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
    }

    private fun loadBookFromUrl(pdfUrl: String) {
        Log.d("pdf view activity", "load book from url")
        val reference = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl)
        reference.getBytes(Constants.MAX_BYTES_PDF)
            .addOnSuccessListener {bytes->
                Log.d("pdf view activity", "loading book from url")
                binding.pdfView.fromBytes(bytes).swipeHorizontal(false)
                    .onPageChange{page,pageCount->
                        val currentPage = page+1
                        binding.toolbarSubTitle.text = "$currentPage/$pageCount"
                        Log.d("pdf view activity", "page count $currentPage/$pageCount")

                    }
                    .onError{e->
                        Log.d("pdf view activity", "${e.message}")
                    }
                    .onPageError { page, t ->
                        Log.d("pdf view activity", "${t.message}")
                    }.load()
                binding.progressBar.visibility = View.GONE
            }
            .addOnFailureListener { e->
                Log.d("pdf view activity", "${e.message}")
                binding.progressBar.visibility = View.GONE

            }
    }
}