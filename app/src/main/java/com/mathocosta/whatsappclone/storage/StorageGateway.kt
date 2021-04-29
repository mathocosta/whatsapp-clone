package com.mathocosta.whatsappclone.storage

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage

object StorageGateway {
    private val storage
        get() = Firebase.storage

    private const val IMAGES_DIR = "images"
    private const val GROUP_DIR = "groups"

    fun getImagesRef(user: FirebaseUser): StorageReference =
        storage.getReference(IMAGES_DIR).child(user.uid)

    fun getGroupImagesRef(): StorageReference = storage.getReference(IMAGES_DIR).child(GROUP_DIR)
}