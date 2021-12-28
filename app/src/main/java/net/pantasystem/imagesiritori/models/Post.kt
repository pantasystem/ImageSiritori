package net.pantasystem.imagesiritori.models

data class Post (
    val id: String,
    val word: String,
    val imageUrl: String,
    val account: Account,
)