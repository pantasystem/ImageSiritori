package net.pantasystem.imagesiritori.ui.pages

import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

@Composable
fun RoomEditor(navController: NavController) {

    Scaffold(
        topBar = {
            TopAppBar {
                Text("しりとり作成")
            }
        }
    ) {

    }
}