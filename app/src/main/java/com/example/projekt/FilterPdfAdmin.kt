package com.example.projekt

import android.util.Log
import android.widget.Filter

class FilterPdfAdmin:Filter {
    private var filterList: ArrayList<ModelPdf>
    private var adapterPdfAdmin: AdapterPdfAdmin

    constructor(filterList: ArrayList<ModelPdf>, adapterPdfAdmin: AdapterPdfAdmin) {
        this.filterList = filterList
        this.adapterPdfAdmin = adapterPdfAdmin
        Log.v("filterpdf admin","constructor")
    }

    override fun performFiltering(constraint: CharSequence?): FilterResults {
        Log.v("filterpdf admin","perform filtering")
        //var constraint:CharSequence? = constraint
        var constraint = constraint
        val results = FilterResults()
        if(constraint!=null && constraint.isNotEmpty())
        {
            constraint = constraint.toString().uppercase()
            val filteredModels:ArrayList<ModelPdf> = ArrayList()
            for(i in 0 until filterList.size)
            {
                if(filterList[i].title.uppercase().contains(constraint))
                {
                    filteredModels.add(filterList[i])
                }
            }
            results.count = filteredModels.size
            results.values = filteredModels
        }
        else
        {
            results.count = filterList.size
            results.values = filterList
        }

        return results
    }

    override fun publishResults(constraint: CharSequence, results: FilterResults) {
        adapterPdfAdmin.pdfArrayList = results.values as ArrayList<ModelPdf>
        adapterPdfAdmin.notifyDataSetChanged()
        Log.v("filterpdf admin","publish results")

    }

}