package net.pantasystem.imagesiritori.ui.pages

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Base64
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
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.pantasystem.imagesiritori.asSuspend
import net.pantasystem.imagesiritori.models.repositories.RepositoryFactory
import net.pantasystem.imagesiritori.models.request.RequestAnnotateImage
import net.pantasystem.imagesiritori.models.response.AnnotateImageResponse
import net.pantasystem.imagesiritori.utils.AppState
import net.pantasystem.imagesiritori.utils.StateContent
import net.pantasystem.imagesiritori.utils.getFileName
import java.io.ByteArrayOutputStream


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

    suspend fun loadWordSuggestion(uri: Uri) {
        wordSuggestionState = AppState.Loading(wordSuggestionState.content)
        runCatching {
            val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            val scaleDownedBitmap = scaleBitmapDown(bitmap, 1024)
            val byteArrayOutputStream = ByteArrayOutputStream()
            scaleDownedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
            val imageBytes = byteArrayOutputStream.toByteArray()
            val base64encoded = Base64.encodeToString(imageBytes, Base64.NO_WRAP)
            val req = RequestAnnotateImage(
                image = RequestAnnotateImage.Image(
                    source = null,
                    content = base64encoded
                ),
                features = listOf(
                    RequestAnnotateImage.Feature(
                        type = "OBJECT_LOCALIZATION"
                    )
                ),
                imageContext = RequestAnnotateImage.ImageContext(
                    languageHints = listOf("ja")
                )
            )

            //Firebase.functions.getHttpsCallable("helloWorld").call()
            val json = Json.encodeToString(
                req
            )
            Log.d("json", "json:$json")
            Firebase.functions.getHttpsCallable("annotationImage")
                .call(json).addOnSuccessListener { result ->
                    Log.d("PostEditor", "success, type:${result.data!!::class.java}, ${result.data}")
                    val responseWords = (result.data as ArrayList<*>).mapNotNull {
                        it as HashMap<*, *>?
                    }.map {
                        it["localizedObjectAnnotations"] as ArrayList<*>
                    }.map {
                        it.mapNotNull { i ->
                            i as HashMap<*, *>?
                        }.map { localized ->
                            localized["name"] as String
                        }
                    }.flatten()
                    wordSuggestionState = AppState.Fixed(
                        StateContent.Exist(responseWords)
                    )

                }.addOnFailureListener {
                    Log.d("PostEditor", "failure, msg:${it.message}", it)
                    wordSuggestionState = AppState.Error(wordSuggestionState.content, throwable = it)
                }
            //Log.d("PostEditor", "response:${res.data}")

            //res.data
            //Log.d("json", req.toString())
        }
    }

    suspend fun uploadImage(uri: Uri) {
        val fileName = context.getFileName(uri)

        val ref = FirebaseStorage.getInstance()
            .getReference(roomId)
            .child(uid)
            .child(fileName)

        val result = ref.putFile(uri).asSuspend()

        val url = ref.downloadUrl.asSuspend()
        imageUrl = url
        Log.d("PostEditorPage", "imageUrl:$imageUrl")

        loadWordSuggestion(uri)
    }

    val openImageFile = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if(uri != null) {
            coroutineScope.launch {
                uploadImage(uri)
            }
        }
    }
    // success:[{cropHintsAnnotation=null, safeSearchAnnotation=null, faceAnnotations=[], landmarkAnnotations=[], logoAnnotations=[], localizedObjectAnnotations=[], error={code=3, details=[], message=Invalid GCS path specified: https://firebasestorage.googleapis.com/v0/b/imagesiritori.appspot.com/o/2S8Nixbu5T2DPiCGzclJ%2F7rRBJDr7NZPdPV5pA5P78r8aFq13%2FIMG_20211219_215340.jpg?alt=media&token=c8e42564-a8e8-432e-9b1d-9fc8e58be5ba}, productSearchResults=null, webDetection=null, fullTextAnnotation=null, context=null, textAnnotations=[], labelAnnotations=[], imagePropertiesAnnotation=null}]
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
        LazyColumn(
            Modifier.padding(8.dp)
        ) {
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
                        val words = (wordSuggestionState.content as StateContent.Exist?)?.rawContent
                            ?: emptyList()
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

private fun scaleBitmapDown(bitmap: Bitmap, maxDimension: Int): Bitmap {
    val originalWidth = bitmap.width
    val originalHeight = bitmap.height
    var resizedWidth = maxDimension
    var resizedHeight = maxDimension
    when {
        originalHeight > originalWidth -> {
            resizedHeight = maxDimension
            resizedWidth =
                (resizedHeight * originalWidth.toFloat() / originalHeight.toFloat()).toInt()
        }
        originalWidth > originalHeight -> {
            resizedWidth = maxDimension
            resizedHeight =
                (resizedWidth * originalHeight.toFloat() / originalWidth.toFloat()).toInt()
        }
        originalHeight == originalWidth -> {
            resizedHeight = maxDimension
            resizedWidth = maxDimension
        }
    }
    return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false)
}