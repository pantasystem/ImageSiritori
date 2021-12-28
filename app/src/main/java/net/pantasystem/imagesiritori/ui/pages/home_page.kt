package net.pantasystem.imagesiritori.ui.pages

import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavController
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import net.pantasystem.imagesiritori.asFlow
import net.pantasystem.imagesiritori.models.Room

@ExperimentalCoroutinesApi
@Composable
fun HomePage(navController: NavController) {



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
                    navController.navigate("rooms/create")
                }
            )
        }
    ) {

    }
}