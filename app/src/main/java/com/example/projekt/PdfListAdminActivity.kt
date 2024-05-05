package com.example.projekt

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import com.example.projekt.databinding.ActivityPdfListAdminBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PdfListAdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPdfListAdminBinding

    private var categoryId = ""
    private var category = ""
    private lateinit var pdfArrayList: ArrayList<ModelPdf>
    private lateinit var adapterPdfAdmin: AdapterPdfAdmin

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPdfListAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = intent
        categoryId = intent.getStringExtra("categoryId")!!
        category = intent.getStringExtra("category")!!

        //set pdf
        binding.subTitleTv.text = category
        loadPdfList()

        binding.back.setOnClickListener {
            onBackPressed()
        }

        binding.searchEt.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                Log.v("pdfListAdmin","on text changed, text: ${s.toString()}")
                try{
                    adapterPdfAdmin.filter!!.filter(s)
                    Log.v("filterpdf admin","try ok")
                }
                catch (e:Exception)
                {
                    Log.v("Error on textchanged","e: ${e.message}")
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })


    }

    private fun loadPdfList() {
        pdfArrayList = ArrayList()

        if(category!="All")
        {
            val ref = FirebaseDatabase.getInstance().getReference("Books")
            ref.orderByChild("categoryId").equalTo(categoryId)
                .addValueEventListener(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        pdfArrayList.clear()
                        for(ds in snapshot.children)
                        {
                            val model = ds.getValue(ModelPdf::class.java)
                            if (model != null) {
                                pdfArrayList.add(model)
                                Log.v("","ondatachange: ${model.title}")
                            }

                        }
                        adapterPdfAdmin = AdapterPdfAdmin(this@PdfListAdminActivity,pdfArrayList)
                        binding.booksRv.adapter = adapterPdfAdmin
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })
        }
        else
        {
            val ref = FirebaseDatabase.getInstance().getReference("Books")
            ref.orderByChild("categoryId")
                .addValueEventListener(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        pdfArrayList.clear()
                        for(ds in snapshot.children)
                        {
                            val model = ds.getValue(ModelPdf::class.java)
                            if (model != null) {
                                pdfArrayList.add(model)
                                Log.v("","ondatachange: ${model.title}")
                            }

                        }
                        adapterPdfAdmin = AdapterPdfAdmin(this@PdfListAdminActivity,pdfArrayList)
                        binding.booksRv.adapter = adapterPdfAdmin
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })
        }

    }
}