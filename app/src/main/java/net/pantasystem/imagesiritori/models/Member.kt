package net.pantasystem.imagesiritori.models

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.DocumentReference

data class Member (
    val account: Account,
    val point: Int,

)