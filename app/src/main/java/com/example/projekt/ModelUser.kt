package com.example.projekt

class ModelUser {
    var username:String = ""
    var profileImage:String = ""
    var uid:String = ""

    constructor()
    constructor(username: String, profileImage: String, uid: String) {
        this.username = username
        this.profileImage = profileImage
        this.uid = uid
    }


}