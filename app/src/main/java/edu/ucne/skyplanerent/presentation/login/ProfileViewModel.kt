package edu.ucne.skyplanerent.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.skyplanerent.data.local.entity.UserRegisterAccount
import edu.ucne.skyplanerent.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


// ProfileViewModel.kt
@HiltViewModel
class ProfileViewModel @Inject constructor(
     val userRepository: UserRepository
) : ViewModel() {
     private val _user = MutableStateFlow<UserRegisterAccount?>(null)
    val user: StateFlow<UserRegisterAccount?> get() = _user

    fun loadUser(email: String?) {
        email?.let {
            viewModelScope.launch {
                _user.value = userRepository.getUserByEmail(it)
            }
        }
    }
}