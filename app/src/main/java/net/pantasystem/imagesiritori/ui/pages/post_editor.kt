package net.pantasystem.imagesiritori.ui.pages

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import net.pantasystem.imagesiritori.asSuspend
import net.pantasystem.imagesiritori.utils.getFileName


@Composable
fun PostEditor(navController: NavController, roomId: String) {


    val coroutineScope = rememberCoroutineScope()

    val context = LocalContext.current
    val uid = FirebaseAuth.getInstance().uid!!

    var imageUrl: Uri? by remember {
        mutableStateOf(null)
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
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            if(imageUrl == null) {
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
            }else{
                Box(
                    Modifier.aspectRatio(3/4f)
                        .fillMaxWidth()
                ) {
                    Image(
                        rememberImagePainter(imageUrl!!), contentDescription = null
                    )
                }

            }

        }
    }
}