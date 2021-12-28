package net.pantasystem.imagesiritori.models.store

import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

sealed interface AccountState {
    object Loading : AccountState
    object Unauthorized : AccountState
    data class Authorized(val firebaseUser: FirebaseUser) : AccountState
}
class AccountStore {
    private val _state = MutableStateFlow(AccountState.Loading)
    val state: StateFlow<AccountState> = _state

}