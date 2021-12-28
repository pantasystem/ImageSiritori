package net.pantasystem.imagesiritori.ui.pages

import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import net.pantasystem.imagesiritori.models.store.AccountState

@ExperimentalCoroutinesApi
@Composable
fun MainPage() {
    val navController = rememberNavController()
    var accountState: AccountState by remember {
        mutableStateOf(AccountState.Loading)
    }


    DisposableEffect(key1 = null) {
        val listener: (FirebaseAuth)->Unit =  {
            accountState = if(it.currentUser == null) {
                AccountState.Unauthorized
            }else{
                AccountState.Authorized(it.currentUser!!)
            }
        }
        FirebaseAuth.getInstance()
            .addAuthStateListener(listener)

        onDispose {
            FirebaseAuth.getInstance()
                .removeAuthStateListener(listener)
        }


    }

    when(accountState) {
        is AccountState.Loading -> {
            Scaffold {
                CircularProgressIndicator()
            }
        }
        is AccountState.Unauthorized -> {
            SignUpPage(
                navController = navController,
            )
        }
        is AccountState.Authorized -> {
            NavHost(
                navController = navController,
                startDestination = "home"
            ) {
                composable("home") {
                    HomePage(navController)
                }
                composable(
                    route = "rooms/{roomId}",
                    arguments = listOf(navArgument("roomId") { type = NavType.StringType })) {
                    RoomPage(
                        navController = navController,
                        roomId = it.arguments?.getString("roomId")!!
                    )
                }
                composable(
                    route = "rooms/{roomId}/posts/create",
                    arguments = listOf(
                        navArgument("roomId") { type = NavType.StringType },
                        navArgument("postId") { type = NavType.StringType }
                    )
                ) {
                    PostEditor(
                        navController = navController,
                        roomId = it.arguments?.getString("roomId")!!
                    )
                }
                composable("signup") {
                    SignUpPage(
                        navController = navController,
                    )
                }
            }
        }
    }


}