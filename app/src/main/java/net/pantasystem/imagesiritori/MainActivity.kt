package net.pantasystem.imagesiritori

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import net.pantasystem.imagesiritori.ui.pages.MainPage
import net.pantasystem.imagesiritori.ui.pages.RoomPage
import net.pantasystem.imagesiritori.ui.theme.ImageSiritoriTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ImageSiritoriTheme {
                // A surface container using the 'background' color from the theme
                MainPage()
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ImageSiritoriTheme {
        Greeting("Android")
    }
}