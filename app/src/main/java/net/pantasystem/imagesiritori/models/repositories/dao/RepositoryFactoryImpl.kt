package net.pantasystem.imagesiritori.models.repositories.dao

import net.pantasystem.imagesiritori.models.repositories.PostRepository
import net.pantasystem.imagesiritori.models.repositories.RepositoryFactory

class RepositoryFactoryImpl : RepositoryFactory {
    override fun postRepository(): PostRepository {
        return PostRepositoryFirestoreImpl()
    }
}