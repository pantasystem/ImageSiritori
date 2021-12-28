package net.pantasystem.imagesiritori.ui.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*

import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.pantasystem.imagesiritori.asSuspend

@Composable
fun SignUpPage(navController: NavController) {

    val coroutine = rememberCoroutineScope()
    var userName: String by remember {
        mutableStateOf("")
    }

    suspend fun register() {
        val account = FirebaseAuth.getInstance().signInAnonymously().asSuspend()
        Firebase.firestore.collection("accounts")
            .document(account.user!!.uid)
            .set(
                mapOf(
                    "username" to userName,
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).asSuspend()
    }


    Scaffold(
        topBar = {
            TopAppBar {
                Text("登録")
            }
        }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            TextField(
                value = userName,
                onValueChange = {
                    userName = it
                },
                label = {
                  Text("ユーザー名")
                },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    coroutine.launch(Dispatchers.IO) {
                        register()
                    }
                }
            ) {
                Text("登録")
            }
        }
    }
}