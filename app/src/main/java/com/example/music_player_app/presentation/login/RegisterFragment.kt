package com.example.music_player_app.presentation.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.music_player_app.databinding.FragmentRegisterBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class RegisterFragment(
    private val onRegisterSuccess: () -> Unit
) : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val client = OkHttpClient()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.buttonRegister.setOnClickListener {
            val login = binding.editTextRegLogin.text.toString().trim()
            val pass = binding.editTextRegPassword.text.toString().trim()
            if (login.isEmpty() || pass.isEmpty()) {
                Toast.makeText(context, "Введите логин и пароль", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            registerOnServer(login, pass)
        }
    }

    private fun registerOnServer(login: String, pass: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val json = """{"username":"$login","password":"$pass"}"""
                val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())
                val request = Request.Builder()
                    .url("http://192.168.213.211:3000/register")
                    .post(requestBody)
                    .build()
                client.newCall(request).execute().use { response ->
                    val responseBody = response.body?.string()
                    if (response.isSuccessful) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Регистрация успешна! Войдите.", Toast.LENGTH_SHORT).show()
                            onRegisterSuccess()
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Ошибка регистрации: ${responseBody ?: response.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Ошибка сети: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}