package es.efb.isvf_studentapp.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import es.efb.isvf_studentapp.R
import es.efb.isvf_studentapp.databinding.ActivityLoginBinding
import es.efb.isvf_studentapp.fragments.LoginFragment
import es.efb.isvf_studentapp.fragments.RegisterFragment
import es.efb.isvf_studentapp.fragments.ResetPassFragment
import es.efb.isvf_studentapp.managers.FirestoreManager
import es.efb.isvf_studentapp.utils.IS_ADMINISTRADOR
import es.efb.isvf_studentapp.utils.IS_PROFESSOR
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext



class LoginActivity : AppCompatActivity(),
    LoginFragment.LoginFragmentListener,
    RegisterFragment.RegisterFragmentListener,
    ResetPassFragment.ResetPassFragmentListener {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.loginFragmentContainer, LoginFragment())
                .commit()
        }
    }

    override fun onLoginClicked(correo: String, contrasena: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val esProfesor = FirestoreManager().isProfessor(correo)
            val esAdministrador = FirestoreManager().isAdministrador(correo)

            withContext(Dispatchers.Main) {
                IS_PROFESSOR = esProfesor
                IS_ADMINISTRADOR = esAdministrador

                val mensaje = when {
                    esProfesor -> {
                        getString(R.string.bienvenido_profe)
                    }
                    esAdministrador -> {
                        getString(R.string.bienvenido_admin)
                    }
                    else -> {
                        getString(R.string.bienvenido_estud)

                    }
                }

                Toast.makeText(this@LoginActivity, mensaje, Toast.LENGTH_SHORT).show()

                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                startActivity(intent)
            }
        }
    }


    override fun onRegisterClicked() {
        println("Se hizo clic en Registrarse")
        supportFragmentManager.beginTransaction()
            .replace(R.id.loginFragmentContainer, RegisterFragment())
            .addToBackStack(null)
            .commit()
    }

    override fun onForgotPasswordClicked() {
        println("Se hizo clic en olvidar Contraseña")
        supportFragmentManager.beginTransaction()
            .replace(R.id.loginFragmentContainer, ResetPassFragment())
            .addToBackStack(null)
            .commit()
    }

    override fun onPasswordReset(correo: String) {
        println("Restableix contraseña per a: $correo")
    }

    override fun onRegisterCompleted() {
        println("Registro fet")
    }
}
