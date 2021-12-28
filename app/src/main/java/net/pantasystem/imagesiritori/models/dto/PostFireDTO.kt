package net.pantasystem.imagesiritori.models.dto

import com.google.firebase.firestore.DocumentReference
import net.pantasystem.imagesiritori.models.Account


class PostFireDTO (
    val id: String,
    val word: String,
    val imageUrl: String,
    val account: DocumentReference,
)