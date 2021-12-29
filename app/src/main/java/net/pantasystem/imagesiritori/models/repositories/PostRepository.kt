package net.pantasystem.imagesiritori.models.repositories

import kotlinx.coroutines.flow.Flow
import net.pantasystem.imagesiritori.models.Post

interface PostRepository {

    fun findByRoomId(roomId: String) : Flow<List<Post>>
}