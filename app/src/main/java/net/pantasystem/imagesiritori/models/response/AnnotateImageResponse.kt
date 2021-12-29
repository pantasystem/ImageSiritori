package net.pantasystem.imagesiritori.models.response

import kotlinx.serialization.Serializable

@Serializable
data class AnnotateImageResponse(
    val localizedObjectAnnotations: List<LocalizedObjectAnnotation>
) {
    @Serializable
    data class LocalizedObjectAnnotation(
        val mid: String,
        val score: Double,
        val name: String,
        val boundingPoly: BoundingPoly
    ) {
        @Serializable
        data class BoundingPoly(
            val normalizedVertices: List<NormalizedVertices>
        ) {
            @Serializable
            data class NormalizedVertices(val x: Double, val y: Double)
        }
    }

}