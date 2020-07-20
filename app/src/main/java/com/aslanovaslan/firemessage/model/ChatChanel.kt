package com.aslanovaslan.firemessage.model

data class ChatChanel(val userId: MutableList<String>) {
    constructor():this(mutableListOf())
}