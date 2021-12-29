package net.pantasystem.imagesiritori.models.request

import kotlinx.serialization.Serializable


@Serializable
data class RequestAnnotateImage (
    val image: Image,
    val features: List<Feature>,
    val imageContext: ImageContext?
) {
    @Serializable
    data class Image(
        val source: ImageSource
    )
    @Serializable
    data class ImageSource(val gcsImageUri: String)
    @Serializable
    data class Feature(
        val type: String,
    )

    @Serializable
    data class ImageContext(
        val languageHints: List<String>
    )
}
