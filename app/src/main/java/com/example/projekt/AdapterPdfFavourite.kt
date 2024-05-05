package com.example.projekt

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.datastore.preferences.protobuf.Value
import androidx.recyclerview.widget.RecyclerView
import com.example.projekt.databinding.RowPdfAdminBinding
import com.example.projekt.databinding.RowPdfFavouriteBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdapterPdfFavourite:RecyclerView.Adapter<AdapterPdfFavourite.HolderPdfFavourite>{
    private var context: Context
    var pdfArrayList: ArrayList<ModelPdf>
    private lateinit var binding:RowPdfFavouriteBinding



    constructor(context: Context, pdfArrayList: ArrayList<ModelPdf>){
        this.context = context
        this.pdfArrayList = pdfArrayList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderPdfFavourite {
        binding = RowPdfFavouriteBinding.inflate(LayoutInflater.from(context),parent,false)
        return HolderPdfFavourite(binding.root)
    }

    override fun getItemCount(): Int {
        return pdfArrayList.size
    }

    override fun onBindViewHolder(holder: HolderPdfFavourite, position: Int) {
        //set data
        val model = pdfArrayList[position]

        loadBookDetails(model,holder)
        ///////


        holder.itemView.setOnClickListener {
            val intent = Intent(context, PdfDetailActivity::class.java)
            intent.putExtra("bookId", model.id)
            context.startActivity(intent)
        }

        holder.removeFavBtn.setOnClickListener {
            MyApplication.removeFromFavourites(context,model.id)
        }

    }

    private fun loadBookDetails(model: ModelPdf, holder: AdapterPdfFavourite.HolderPdfFavourite) {
        val pdfId = model.id
        val ref = FirebaseDatabase.getInstance().getReference("Books")
        ref.child(pdfId)
            .addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val categoryId = snapshot.child("categoryId").value.toString()
                    val description = snapshot.child("description").value.toString()
                    val downloads = snapshot.child("downloads").value.toString()
                    val views = snapshot.child("views").value.toString()
                    val title = snapshot.child("title").value.toString()
                    val uid = snapshot.child("uid").value.toString()
                    val url = snapshot.child("url").value.toString()
                    val timestamp = snapshot.child("timestamp").value.toString()

                    model.isFavourite = true
                    model.title = title
                    model.description = description
                    model.categoryId = categoryId
                    model.timestamp = timestamp.toLong()
                    model.uid = uid
                    model.url = url
                    model.views = views.toLong()
                    model.downloads = downloads.toLong()

                    val date = MyApplication.formatTimeStamp(timestamp.toLong())
                    MyApplication.loadCategory(categoryId, holder.categoryTv)
                    MyApplication.loadPdfFromUrlSinglePage(url,title,holder.pdfView, holder.progressBar, null)
                    MyApplication.loadPdfSize(url,title,holder.sizeTv)

                    holder.titleTv.text = title
                    holder.descriptionTv.text = description
                    holder.dateTv.text = date

                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }


    inner class HolderPdfFavourite(itemView: View):RecyclerView.ViewHolder(itemView)
    {
        val pdfView = binding.pdfView
        val progressBar = binding.progressBar
        val titleTv = binding.titleTv
        val descriptionTv = binding.descriptionTv
        val categoryTv = binding.categoryTv
        val sizeTv = binding.sizeTv
        val dateTv = binding.dateTv
        val removeFavBtn = binding.removeFavBtn

    }

}