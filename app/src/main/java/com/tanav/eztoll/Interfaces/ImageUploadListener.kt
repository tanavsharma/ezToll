package com.tanav.eztoll.Interfaces

interface ImageUploadListener
{
    fun onUpload(error: Boolean, Message: String?, url: String?)
}