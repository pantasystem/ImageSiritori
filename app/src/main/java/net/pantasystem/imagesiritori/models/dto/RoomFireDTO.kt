package net.pantasystem.imagesiritori.models.dto

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ServerTimestamp
import java.util.*

data class RoomFireDTO(
    @DocumentId val id: String,
    val owner: DocumentReference?,
    @ServerTimestamp val createdAt: Date,
    @ServerTimestamp val updatedAt: Date,
    val accounts: List<DocumentReference>?
) {
    constructor() : this("", null, Date(), Date(), emptyList())
}