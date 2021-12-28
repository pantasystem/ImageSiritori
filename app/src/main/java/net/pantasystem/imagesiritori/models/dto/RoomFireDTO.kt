package net.pantasystem.imagesiritori.models.dto

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.FieldValue

data class RoomFireDTO(
    @DocumentId val id: String,

)