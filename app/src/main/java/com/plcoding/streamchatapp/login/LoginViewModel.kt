package com.plcoding.streamchatapp.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plcoding.streamchatapp.util.Constants.MIN_USERNAME_LENGTH
import dagger.hilt.android.lifecycle.HiltViewModel
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.call.await
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val client : ChatClient
) : ViewModel(){

    private val _loginEvent = MutableSharedFlow<LogInEvent>()
    val logInEvent = _loginEvent.asSharedFlow()

    private fun isValidUsername(username : String) =
        username.length >= MIN_USERNAME_LENGTH

    fun connectUser(username: String){
        val trimedusername = username.trim()

        viewModelScope.launch {
            if (isValidUsername(trimedusername)){
                val result = client.connectGuestUser(
                    userId = trimedusername,
                    username = trimedusername
                ).await()
                if (result.isError){
                    _loginEvent.emit(LogInEvent.ErrorLogin(result.error().message ?: "Unkwown error"))
                    return@launch
                }
                _loginEvent.emit(LogInEvent.Success)
            } else {
                _loginEvent.emit(LogInEvent.ErrorInputTooShort)
            }
        }

    }
    sealed class LogInEvent{
        object ErrorInputTooShort : LogInEvent()

        data class ErrorLogin(val error: String) : LogInEvent()

        object Success : LogInEvent()
    }

}