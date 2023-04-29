package com.plcoding.streamchatapp.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.plcoding.streamchatapp.R
import com.plcoding.streamchatapp.databinding.FragmentLoginBinding
import com.plcoding.streamchatapp.ui.BindingFragment
import com.plcoding.streamchatapp.util.Constants.MIN_USERNAME_LENGTH
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class LoginFragment : BindingFragment<FragmentLoginBinding>() {

    private val viewModel : LoginViewModel by viewModels()

    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentLoginBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnConfirm.setOnClickListener {
            setupConnectingState()
            viewModel.connectUser(binding.etUsername.text.toString())
        }
        binding.etUsername.addTextChangedListener {
            binding.etUsername.text = null
        }
        subscribeToEvent()
    }

    private fun subscribeToEvent(){
        lifecycleScope.launchWhenStarted {
            viewModel.logInEvent.collect { event ->
                when(event){
                    is LoginViewModel.LogInEvent.ErrorInputTooShort ->{
                        setupIdleUiState()
                        binding.etUsername.error = getString(R.string.error_username_too_short, MIN_USERNAME_LENGTH)
                    }
                    is LoginViewModel.LogInEvent.Success ->{
                        setupIdleUiState()
                        Toast.makeText(
                            requireContext(),
                            "Successful login",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    is LoginViewModel.LogInEvent.ErrorLogin -> {
                        setupIdleUiState()
                        Toast.makeText(
                            requireContext(),
                            event.error,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    private fun setupConnectingState (){
        binding.progressBar.isVisible = true
        binding.btnConfirm.isEnabled = false
    }

    private fun setupIdleUiState (){
        binding.progressBar.isVisible = false
        binding.btnConfirm.isEnabled = true
    }
}