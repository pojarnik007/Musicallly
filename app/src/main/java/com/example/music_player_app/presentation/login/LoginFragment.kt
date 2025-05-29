package com.example.music_player_app.presentation.login

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.music_player_app.data.repository.NetworkConfig
import com.example.music_player_app.databinding.FragmentLoginBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class LoginFragment(
    private val onLoginSuccess: () -> Unit,
    private val onGoToRegister: () -> Unit
) : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val client = OkHttpClient()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.buttonLogin.setOnClickListener {
            val login = binding.editTextLogin.text.toString().trim()
            val pass = binding.editTextPassword.text.toString().trim()
            if (login.isEmpty() || pass.isEmpty()) {
                Toast.makeText(context, "Введите логин и пароль", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            loginOnServer(login, pass)
        }
        binding.buttonGoToRegister.setOnClickListener {
            onGoToRegister()
        }
    }

    private fun loginOnServer(login: String, pass: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val json = """{"username":"$login","password":"$pass"}"""
                val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())
                val request = Request.Builder()
                    .url("${NetworkConfig.BASE_URL}/login")
                    .post(requestBody)
                    .build()
                client.newCall(request).execute().use { response ->
                    val responseBody = response.body?.string()
                    if (response.isSuccessful) {
                        // В вашем LoginFragment (или где происходит вход)
                        val prefs = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                        val isAdmin = (login == "admin")
                        prefs.edit()

                            .putString("user_name", login) // realUserName — имя пользователя из БД
                            .putBoolean("is_logged_in", true)
                            .putBoolean("is_admin", isAdmin)
                            .apply()
                        // Сохраняем логин в SharedPreferences
                        val sp = activity?.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                        sp?.edit()?.putBoolean("is_logged_in", true)?.apply()
                        withContext(Dispatchers.Main) {
                            onLoginSuccess()
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(requireContext(), "Ошибка входа: ...", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Ошибка сети!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}