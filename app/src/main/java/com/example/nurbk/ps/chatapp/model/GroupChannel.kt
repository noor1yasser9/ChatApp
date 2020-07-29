package com.example.nurbk.ps.chatapp.model

import java.io.Serializable

class GroupChannel(
    val id: String,
    val group_name: String,
    val channelId: String,
    val photo: String,
    val arrayList: ArrayList<String>
) : Serializable {

    constructor() : this("", "", "", "", arrayListOf())
}