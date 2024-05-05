package com.example.projekt

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.example.projekt.databinding.RowPdfAdminBinding

class AdapterPdfAdmin:RecyclerView.Adapter<AdapterPdfAdmin.HolderPdfAdmin>, Filterable {
    private var context: Context
    var pdfArrayList: ArrayList<ModelPdf>
    private val filterList:ArrayList<ModelPdf>
    //var filter: FilterPdfAdmin? = null
    private var filter: FilterPdfAdmin? = null
    private lateinit var binding:RowPdfAdminBinding



    constructor(context: Context, pdfArrayList: ArrayList<ModelPdf>){
        this.context = context
        this.pdfArrayList = pdfArrayList
        this.filterList = pdfArrayList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderPdfAdmin {
        binding = RowPdfAdminBinding.inflate(LayoutInflater.from(context),parent,false)
        return HolderPdfAdmin(binding.root)
    }

    override fun getItemCount(): Int {
        return pdfArrayList.size
    }

    override fun onBindViewHolder(holder: HolderPdfAdmin, position: Int) {
        //set data
        val model = pdfArrayList[position]
        val pdfId = model.id
        val categoryId = model.categoryId
        val title = model.title
        val description = model.description
        val pdfUrl = model.url
        val timestamp = model.timestamp

        //convert timestamp to date
        val formattedDate = MyApplication.formatTimeStamp(timestamp)
        holder.titleTv.text = title
        holder.descriptionTv.text = description
        holder.dateTv.text = formattedDate

        MyApplication.loadCategory(categoryId, holder.categoryTv)
        MyApplication.loadPdfFromUrlSinglePage(pdfUrl, title, holder.pdfView, holder.progressBar, null)
        MyApplication.loadPdfSize(pdfUrl,title,holder.sizeTv)

        holder.expand.setOnClickListener {

        }

        holder.itemView.setOnClickListener {
            val intent = Intent(context, PdfDetailActivity::class.java)
            intent.putExtra("bookId", pdfId)
            context.startActivity(intent)
        }

    }



    inner class HolderPdfAdmin(itemView: View):RecyclerView.ViewHolder(itemView)
    {
        val pdfView = binding.pdfView
        val progressBar = binding.progressBar
        val titleTv = binding.titleTv
        val descriptionTv = binding.descriptionTv
        val categoryTv = binding.categoryTv
        val sizeTv = binding.sizeTv
        val dateTv = binding.dateTv
        val expand = binding.expand

    }

    override fun getFilter(): Filter {
        Log.v("adapter pdf admin","get filter")
        if(filter == null)
        {
            filter = FilterPdfAdmin(filterList,this)
            Log.v("filterpdf admin","filter - ${filter}")
        }
        return filter as FilterPdfAdmin
    }
}