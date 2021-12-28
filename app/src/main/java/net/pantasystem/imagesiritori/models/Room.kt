package net.pantasystem.imagesiritori.models

data class Room (
    val id: String,
    val owner: Account,
    val accounts: List<Account>
)