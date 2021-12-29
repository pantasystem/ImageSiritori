package net.pantasystem.imagesiritori.ui.pages

import android.util.Log
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import net.pantasystem.imagesiritori.asFlow
import net.pantasystem.imagesiritori.asSuspend
import net.pantasystem.imagesiritori.models.Account
import net.pantasystem.imagesiritori.models.Member
import net.pantasystem.imagesiritori.models.Post
import net.pantasystem.imagesiritori.models.Room
import net.pantasystem.imagesiritori.models.dto.PostFireDTO
import net.pantasystem.imagesiritori.models.dto.RoomFireDTO
import net.pantasystem.imagesiritori.ui.components.AvatarAndPointIcon
import net.pantasystem.imagesiritori.ui.components.PostCard

@ExperimentalCoroutinesApi
@Composable
fun RoomPage(navController: NavController, roomId: String) {

    val coroutineScope = rememberCoroutineScope()

    val uid = FirebaseAuth.getInstance().uid!!
    val roomRef = Firebase.firestore
        .collection("rooms")
        .document(roomId)
    val room = roomRef
        .asFlow()
        .map {
            it?.toObject(RoomFireDTO::class.java)
        }.filterNotNull().map {
            Room(
                id = it.id,
                owner = it.owner!!.get().asSuspend().toObject(Account::class.java)!!,
                accounts = it.accounts!!.map { ref ->
                    ref.get()
                        .asSuspend()
                }.map { snapshot ->
                    snapshot.toObject(Account::class.java)!!
                }
            )
        }.collectAsState(initial = null)

    val posts = roomRef.collection("posts")
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
        }.collectAsState(initial = null)



    Scaffold(
        topBar = {
            TopAppBar {
                Text("しりとり")
            }
        },
        floatingActionButton = {
            if(room.value?.accounts?.any { it.id == uid } == true) {
                ExtendedFloatingActionButton(
                    text = {
                        Text("回答する")
                    },
                    icon = {
                        Icon(Icons.Default.Add, contentDescription = "Add Answer")
                    },
                    onClick = {
                        navController.navigate("rooms/${roomId}/posts/create")
                    }
                )
            }else{
                ExtendedFloatingActionButton(
                    text = {
                        Text("参加する")
                    },

                    onClick = {
                        val accountRef = Firebase.firestore
                            .collection("accounts")
                            .document(uid)
                        coroutineScope.launch {
                            roomRef.collection("members")
                                .document(uid)
                                .set(mapOf(
                                    "account" to accountRef
                                ))
                                .asSuspend()
                        }
                    }
                )
            }

        }
    ) {
        if(room.value == null || posts.value == null) {
            CircularProgressIndicator()
        }else{
            RoomContent(room = room.value!!, posts.value!!)
        }

    }
}

@Composable
fun RoomContent(room: Room, posts: List<Post>) {
    val accounts = room.accounts


    Log.d("RoomContent","posts:$posts, room:$room")

    val mem = posts.groupBy {
        it.account
    }
    val members = accounts.map {
        Member(
            it,
            point = mem[it]?.size ?: 0
        )
    }.sortedBy {
        it.point
    }.reversed()

    Row {
        LazyColumn(
            Modifier
                .width(72.dp)
                .fillMaxHeight()
        ) {
            items(members.size) { index ->
                AvatarAndPointIcon(member = members[index])
            }
        }
        LazyColumn(
            Modifier.fillMaxHeight()
        ) {
            items(posts.size) { index ->
                PostCard(post = posts[index])
            }
        }
    }
}