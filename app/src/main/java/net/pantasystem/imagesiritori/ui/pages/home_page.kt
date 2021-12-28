package net.pantasystem.imagesiritori.ui.pages

import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import net.pantasystem.imagesiritori.asFlow
import net.pantasystem.imagesiritori.asSuspend
import net.pantasystem.imagesiritori.models.Room

@ExperimentalCoroutinesApi
@Composable
fun HomePage(navController: NavController) {

    val coroutineScope = rememberCoroutineScope()

    suspend fun createRoom() {
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        val accountRef = Firebase.firestore
            .collection("accounts")
            .document(uid)
        val result = Firebase.firestore
            .collection("rooms")
            .add(
                mapOf(
                    "owner" to accountRef,
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp(),
                    "accounts" to listOf<Any>()
                )
            ).asSuspend()
        result.collection("members")
            .document(uid)
            .set(mapOf(
                "account" to accountRef
            ))
            .asSuspend()

        navController.navigate("rooms/${result.id}")
    }

    Scaffold(
        topBar = {
            TopAppBar {
                Text("しりとり一覧")
            }
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text("しりとり作成") },
                onClick = {
                    coroutineScope.launch {
                        createRoom()
                    }
                }
            )
        }
    ) {
        TextButton(onClick = { FirebaseAuth.getInstance().signOut() }) {
            Text("ログアウト")
        }
    }
}