package net.pantasystem.imagesiritori.ui.pages

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Resources
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import net.pantasystem.imagesiritori.asSuspend
import net.pantasystem.imagesiritori.models.repositories.RepositoryFactory
import net.pantasystem.imagesiritori.utils.AppState
import net.pantasystem.imagesiritori.utils.StateContent
import net.pantasystem.imagesiritori.utils.getFileName


@Composable
fun PostEditor(navController: NavController, repositoryFactory: RepositoryFactory, roomId: String) {

    val posts = repositoryFactory.postRepository().findByRoomId(roomId)
        .collectAsState(initial = null)

    val coroutineScope = rememberCoroutineScope()

    val context = LocalContext.current
    val uid = FirebaseAuth.getInstance().uid!!

    var imageUrl: Uri? by remember {
        mutableStateOf(null)
    }

    var selectedWordItemIndex: Int? by remember {
        mutableStateOf(null)
    }

    var wordSuggestionState: AppState<List<String>> by remember {
        mutableStateOf(AppState.Fixed(StateContent.NotExist()))
    }

    suspend fun uploadImage(uri: Uri) {
        val fileName = context.getFileName(uri)

        val ref = FirebaseStorage.getInstance()
            .getReference(roomId)
            .child(uid)
            .child(fileName)

        ref.putFile(uri).asSuspend()

        val url = ref.downloadUrl.asSuspend()
        imageUrl = url
        Log.d("PostEditorPage", "imageUrl:$imageUrl")
    }

    val openImageFile = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if(uri != null) {
            coroutineScope.launch {
                uploadImage(uri)
            }
        }
    }
    val requestReadExternalStorage = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        openImageFile.launch(arrayOf("image/*"))
    }



    fun checkReadExternalStorage(): Boolean {
        return ContextCompat
            .checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
    }



    Scaffold(
        topBar = {
            TopAppBar {
                Text("投稿作成")
            }
        }
    ) {
        if(posts.value == null) {
            Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }
        LazyColumn {
            item {
                Text("前のワード")
                Text(posts.value?.firstOrNull()?.word ?: "", fontSize = 18.sp)
            }
            if(imageUrl == null) {
                item {
                    TextButton(
                        onClick = {
                            if(checkReadExternalStorage()) {
                                openImageFile.launch(arrayOf("image/*"))
                            }else{
                                requestReadExternalStorage.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                            }
                        },
                        Modifier.fillMaxWidth(),
                    ) {
                        Icon(Icons.Outlined.Add, contentDescription = null)
                        Text("画像を選択")
                    }
                }

            }else{
                item {
                    Box(
                        Modifier
                            .aspectRatio(4f / 3)
                            .fillMaxWidth()
                    ) {
                        Image(
                            rememberImagePainter(imageUrl!!), contentDescription = null,
                            contentScale = ContentScale.Crop
                        )
                    }
                }


            }
            val words = listOf("hogehoge", "piyopiyo", "fugafuga")
            item {
                Spacer(modifier = Modifier.padding(vertical = 4.dp))
            }
            item {
                Text("候補を選択")
            }

            when (wordSuggestionState) {
                is AppState.Loading -> {
                    item {
                        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                }
                is AppState.Error -> {
                    item {
                        Text("候補の取得に失敗しました")
                    }
                }
                is AppState.Fixed -> {
                    if((wordSuggestionState.content as? StateContent.Exist)?.rawContent.isNullOrEmpty()) {
                        item {
                            Text("関連するワードはありません")
                        }
                    }else{
                        items(words.size) { index ->
                            Card(
                                backgroundColor = if (index == selectedWordItemIndex) {
                                    MaterialTheme.colors.primary
                                } else {
                                    MaterialTheme.colors.surface
                                },
                                modifier = Modifier
                                    .clickable {
                                        selectedWordItemIndex = index
                                    }

                            ) {
                                Text(words[index],
                                    Modifier
                                        .padding(16.dp)
                                        .fillMaxWidth())
                            }
                        }
                    }

                }
            }


        }
    }
}

@Composable
fun LazyListScope.SuggestionWord() {

}