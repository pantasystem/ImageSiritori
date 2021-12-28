package net.pantasystem.imagesiritori.ui.pages

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import net.pantasystem.imagesiritori.models.Account
import net.pantasystem.imagesiritori.models.Member
import net.pantasystem.imagesiritori.models.Post
import net.pantasystem.imagesiritori.ui.components.AvatarAndPointIcon
import net.pantasystem.imagesiritori.ui.components.PostCard

@Composable
fun RoomPage(navController: NavController, roomId: String) {

    val accounts = listOf(
        Account("", "hogehoge", "https://pbs.twimg.com/profile_images/1317853360364515364/OIaBg_it_400x400.jpg"),
        Account("", "piyopiyo", "https://pbs.twimg.com/profile_images/1317853360364515364/OIaBg_it_400x400.jpg"),
        Account("", "fugafuga", "https://pbs.twimg.com/profile_images/1317853360364515364/OIaBg_it_400x400.jpg")
    )

    val posts = listOf(
        Post("", "hogehoge", "https://pbs.twimg.com/media/FHcItt5aQAErBLb?format=jpg&name=large", accounts[0]),
        Post("", "hogehoge", "https://pbs.twimg.com/media/FHcItt5aQAErBLb?format=jpg&name=large", accounts[0]),
        Post("", "hogehoge", "https://pbs.twimg.com/media/FHcItt5aQAErBLb?format=jpg&name=large", accounts[1]),
        Post("", "hogehoge", "https://pbs.twimg.com/media/FHcItt5aQAErBLb?format=jpg&name=large", accounts[2]),
    )

    val members = posts.groupBy {
        it.account
    }.map {
        Member(
            it.key,
            it.value.size
        )
    }.sortedBy {
        it.point
    }.reversed()



    Scaffold(
        topBar = {
            TopAppBar {
                Text("しりとり")
            }
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = {
                    Text("回答する")
                },
                icon = {
                    Icon(Icons.Default.Add, contentDescription = "Add Answer")
                },
                onClick = { /*TODO*/ }
            )
        }
    ) {

        Row() {
            LazyColumn(
            ) {
                items(members.size) { index ->
                    AvatarAndPointIcon(member = members[index])
                }
            }
            LazyColumn() {
                items(posts.size) { index ->
                    PostCard(post = posts[index])
                }
            }
        }
    }
}