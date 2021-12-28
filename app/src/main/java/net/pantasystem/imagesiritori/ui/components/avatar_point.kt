package net.pantasystem.imagesiritori.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import net.pantasystem.imagesiritori.models.Account
import net.pantasystem.imagesiritori.models.Member

@Composable
fun AvatarAndPointIcon(member: Member) {
    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = Modifier.padding(4.dp)
            .size(64.dp)

    ){
        Image(
            painter = rememberImagePainter(member.account.avatarUrl),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .clip(CircleShape)                       // clip to the circle shape
                .border(2.dp, Color.Gray, CircleShape)
        )
        Box(
            modifier = Modifier
                .width(20.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White),
                contentAlignment = Alignment.Center,
        ) {
            Text(
                member.point.toString(),
            )
        }

    }

}