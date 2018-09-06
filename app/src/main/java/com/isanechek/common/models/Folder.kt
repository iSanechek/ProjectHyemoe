package com.isanechek.common.models

data class Folder(
        val key: String,
        val path: String,
        val name: String,
        val offset: Int,
        val created: String,
        val modified: String,
        val type: String)