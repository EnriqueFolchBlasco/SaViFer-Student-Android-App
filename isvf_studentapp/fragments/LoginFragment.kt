package es.efb.isvf_studentapp.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import es.efb.isvf_studentapp.R
import es.efb.isvf_studentapp.databinding.FragmentLoginBinding
import es.efb.isvf_studentapp.managers.AuthManager
import es.efb.isvf_studentapp.utils.IS_ADMINISTRADOR
import es.efb.isvf_studentapp.utils.IS_PROFESSOR
import es.efb.isvf_studentapp.utils.PREFERENCES_EMAIL_SAVED
import es.efb.isvf_studentapp.utils.PREFERENCES_FILENAME
import es.efb.isvf_studentapp.utils.PREFERENCES_PASS_SAVED
import es.efb.isvf_studentapp.utils.PREFERENCES_REMEMBER_LOGIN
import es.efb.isvf_studentapp.utils.PREFERENCES_USERNAME_KEY
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginFragment : Fragment() {

    private var listener: LoginFragmentListener? = null
    private lateinit var binding: FragmentLoginBinding
    private val authManager : AuthManager by lazy { AuthManager() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)

        val prefs = requireContext().getSharedPreferences(PREFERENCES_FILENAME, Context.MODE_PRIVATE)
        val emailGuardado = prefs.getString(PREFERENCES_EMAIL_SAVED, null)
        val passGuardado = prefs.getString(PREFERENCES_PASS_SAVED, null)

        if (emailGuardado != null && passGuardado != null) {
            //binding.editTextUsername.setText(emailGuardado)

            listener?.onLoginClicked(emailGuardado, passGuardado)
            binding.checkBoxRememberMe.isChecked = true
        } else {
            binding.checkBoxRememberMe.isChecked = false
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.editTextUsername.text.toString()
            val pass = binding.editTextPassword.text.toString()

            if (!email.isNullOrBlank() && !pass.isNullOrBlank()) {
                lifecycleScope.launch (Dispatchers.IO) {
                    val userLogged = authManager.login(requireContext(),email, pass)
                    withContext(Dispatchers.Main) {
                        if (userLogged != null) {

                            //la box
                            if (binding.checkBoxRememberMe.isChecked) {

                                guardarCorreo(email, pass)
                            } else {
                                eliminarCorreo()
                            }
                            guardarUsuario(email)

                            val editor = prefs.edit()
                            editor.putBoolean(PREFERENCES_REMEMBER_LOGIN, binding.checkBoxRememberMe.isChecked)
                            editor.apply()


                            //Toast.makeText(requireContext(), userLogged.email, Toast.LENGTH_SHORT).show()
                            listener?.onLoginClicked(email, pass)
                        } else {
                            Toast.makeText(requireContext(), getString(R.string.credenciales_incorrectas), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

        binding.btnRegister.setOnClickListener {
            listener?.onRegisterClicked()
        }

        binding.tvForgotPassword.setOnClickListener {
            listener?.onForgotPasswordClicked()
        }

        return binding.root
    }

    private fun netejaInfo() {
        val prefs = requireContext().getSharedPreferences(PREFERENCES_FILENAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.clear()
        editor.apply()

        IS_PROFESSOR = false
        IS_ADMINISTRADOR = false
    }


    private fun guardarCorreo(email: String, pass: String) {
        val prefs = requireContext().getSharedPreferences(PREFERENCES_FILENAME, Context.MODE_PRIVATE)
        with(prefs.edit()) {
            putString(PREFERENCES_EMAIL_SAVED, email)
            putString(PREFERENCES_PASS_SAVED, pass)
            apply()
        }
    }

    private fun guardarUsuario(email: String) {
        val prefs = requireContext().getSharedPreferences(PREFERENCES_FILENAME, Context.MODE_PRIVATE)
        with(prefs.edit()) {
            putString(PREFERENCES_USERNAME_KEY, email)
            apply()
        }
    }

    private fun eliminarCorreo() {
        val prefs = requireContext().getSharedPreferences(PREFERENCES_FILENAME, Context.MODE_PRIVATE)
        with(prefs.edit()) {
            remove(PREFERENCES_EMAIL_SAVED)
            apply()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is LoginFragmentListener) {
            listener = context
        } else {
            throw Exception("La actividad debe implementar LoginFragmentListener")
        }
    }

    interface LoginFragmentListener {
        fun onLoginClicked(username: String, password: String)
        fun onRegisterClicked()
        fun onForgotPasswordClicked()
    }

    override fun onResume() {
        super.onResume()
        val prefs = requireContext().getSharedPreferences(PREFERENCES_FILENAME, Context.MODE_PRIVATE)
        val rememberLogin = prefs.getBoolean(PREFERENCES_REMEMBER_LOGIN, false)
        if (!rememberLogin) {
            netejaInfo()
        }
    }

}
