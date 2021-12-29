package net.pantasystem.imagesiritori.ui.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController


@Composable
fun PostEditor(navController: NavController, roomId: String) {


    Scaffold(
        topBar = {
            TopAppBar {
                Text("投稿作成")
            }
        }
    ) {
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier.verticalScroll(scrollState)
        ) {
            TextButton(onClick = { /*TODO*/ }) {
                Text("画像を選択")
            }
        }
    }
}