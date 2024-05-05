package com.example.projekt

import android.app.Application
import android.content.Context
import android.text.format.DateFormat
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.github.barteksc.pdfviewer.PDFView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.util.Calendar
import java.util.Locale

class MyApplication:Application() {
    override fun onCreate() {
        super.onCreate()
    }

    companion object
    {
        fun formatTimeStamp(timestamp: Long):String
        {
            val cal = Calendar.getInstance(Locale.ENGLISH)
            cal.timeInMillis = timestamp
            return DateFormat.format("dd/MM/yyyy",cal).toString()
        }

        fun loadPdfSize(pdfUrl: String, pdfTitle: String, sizeTv: TextView)
        {
            val ref = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl)
            ref.metadata
                .addOnSuccessListener {storageMetadata->
                    val bytes = storageMetadata.sizeBytes.toDouble()
                    Log.v("","pdfSize: ${bytes} bytes")

                    val kb = bytes/1024
                    val mb = kb/1024
                    if(mb>1)
                    {
                        sizeTv.text = "${String.format("%.2f", mb)} MB"
                    }
                    else if(kb>=1)
                    {
                        sizeTv.text = "${String.format("%.2f", kb)} KB"
                    }
                    else
                    {
                        sizeTv.text = "${String.format("%.2f", bytes)} B"
                    }
                }
                .addOnFailureListener {e->
                    //Toast.makeText(context, "Error - ${e}",Toast.LENGTH_SHORT).show()

                }
        }

        fun loadPdfFromUrlSinglePage(pdfUrl: String, pdfTitle:String, pdfView: PDFView, progressBar: ProgressBar, pages: TextView?)
        {
            val ref = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl)
            ref.getBytes(Constants.MAX_BYTES_PDF)
                .addOnSuccessListener {bytes->
                    //val bytes = storageMetadata.sizeBytes
                    //Log.v("","pdfSize: ${bytes} bytes")

                    //to pdf
                    pdfView.fromBytes(bytes)
                        .pages(0)
                        .spacing(0)
                        .swipeHorizontal(false)
                        .enableSwipe(false)
                        .onError{t->
                            progressBar.visibility = View.INVISIBLE

                        }
                        .onPageError{page, t->
                            progressBar.visibility = View.INVISIBLE
                        }
                        .onLoad { nbpages->
                            progressBar.visibility = View.INVISIBLE
                            if(pages!=null)
                            {
                                pages.text = "$nbpages"
                            }
                        }.load()
                }
                .addOnFailureListener {e->
                    //Toast.makeText(context, "Error - ${e}",Toast.LENGTH_SHORT).show()

                }
        }

        fun loadCategory(categoryId: String, categoryTv: TextView)
        {
            val ref = FirebaseDatabase.getInstance().getReference("Categories")
            ref.child(categoryId)
                .addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        categoryTv.text = snapshot.child("category").value.toString()

                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })

        }

        fun incrementBookViewCount(bookId: String)
        {
            val ref = FirebaseDatabase.getInstance().getReference("Books")
            ref.child(bookId)
                .addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var views = snapshot.child("views").value.toString()
                        if(views=="" || views=="null")
                        {
                            views = "0"
                        }
                        val hashMap = HashMap<String, Any>()
                        hashMap["views"] = views.toLong() + 1

                        val dbRef = FirebaseDatabase.getInstance().getReference("Books")
                        dbRef.child(bookId).updateChildren(hashMap)

                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })
        }

        fun removeFromFavourites(context: Context, bookId: String) {

            val firebase = FirebaseAuth.getInstance()
            val ref = FirebaseDatabase.getInstance().getReference("Users")
            ref.child(firebase.uid!!).child("Favourites").child(bookId)
                .removeValue().addOnSuccessListener {
                    Log.v("pdf detail activity", "removed from favirtets")
                    Toast.makeText(context,"Removed from favourites", Toast.LENGTH_SHORT).show()

                }
                .addOnFailureListener {e->
                    Log.v("remove from favorutie", "e: ${e.message}")
                }

        }

    }
}