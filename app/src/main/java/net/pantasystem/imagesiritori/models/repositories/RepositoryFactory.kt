package net.pantasystem.imagesiritori.models.repositories

interface RepositoryFactory {

    fun postRepository(): PostRepository
}