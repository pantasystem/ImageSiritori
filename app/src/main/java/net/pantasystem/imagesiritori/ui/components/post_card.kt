package net.pantasystem.imagesiritori.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import net.pantasystem.imagesiritori.models.Post

@Composable
fun PostCard(post: Post) {
    Card {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Text(post.word)
            Box(
                modifier = Modifier.fillMaxWidth()
                    .aspectRatio(4f / 3)
            ) {
                Image(
                    painter = rememberImagePainter(post.imageUrl),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit

                )
            }
        }
    }
}