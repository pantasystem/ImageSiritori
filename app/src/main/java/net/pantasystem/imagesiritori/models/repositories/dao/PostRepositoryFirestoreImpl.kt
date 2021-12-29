package net.pantasystem.imagesiritori.models.repositories.dao

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import net.pantasystem.imagesiritori.asFlow
import net.pantasystem.imagesiritori.asSuspend
import net.pantasystem.imagesiritori.models.Account
import net.pantasystem.imagesiritori.models.Post
import net.pantasystem.imagesiritori.models.dto.PostFireDTO
import net.pantasystem.imagesiritori.models.repositories.PostRepository

class PostRepositoryFirestoreImpl : PostRepository{

    @ExperimentalCoroutinesApi
    override fun findByRoomId(roomId: String): Flow<List<Post>> {
        val roomRef = Firebase.firestore
            .collection("rooms")
            .document(roomId)
        return roomRef.collection("posts")
            .asFlow()
            .map {
                it.toObjects(PostFireDTO::class.java)
            }.map {
                it.map { dto ->
                    Post(
                        dto.id,
                        account = dto.account.get().asSuspend().toObject(Account::class.java)!!,
                        imageUrl = dto.imageUrl,
                        word = dto.word
                    )
                }
            }
    }
}