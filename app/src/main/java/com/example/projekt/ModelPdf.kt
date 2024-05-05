package com.example.projekt

class ModelPdf {

    var uid:String=""
    var id:String=""
    var title:String=""
    var description:String=""
    var categoryId:String=""
    var url:String=""
    var timestamp:Long=0
    var views:Long = 0
    var downloads:Long = 0
    var isFavourite=false


    constructor()
    constructor(
        uid: String,
        id: String,
        title: String,
        description: String,
        categoryId: String,
        url: String,
        timestamp: Long,
        views: Long,
        downloads: Long,
        isFavourite: Boolean
    ) {
        this.uid = uid
        this.id = id
        this.title = title
        this.description = description
        this.categoryId = categoryId
        this.url = url
        this.timestamp = timestamp
        this.views = views
        this.downloads = downloads
        this.isFavourite = isFavourite
    }


}