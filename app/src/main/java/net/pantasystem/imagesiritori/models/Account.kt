package net.pantasystem.imagesiritori.models

import com.google.firebase.firestore.DocumentId

data class Account(
    @DocumentId val id: String,
    val username: String,
    val avatarUrl: String,
) {
    constructor() : this("", "", "")
}