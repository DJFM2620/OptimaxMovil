package pe.idat.optimax

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import pe.idat.optimax.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        mBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        navigateToRegister()
        showHome()
    }

    private fun showHome() {

        mBinding.btnLogin.setOnClickListener {

            val email = mBinding.tieEmail.text.toString().trim()
            val pass = mBinding.tiePassword.text.toString().trim()

            if (email == "Joel@hotmail.es" && pass == "123") {

                val intent = Intent(this, MainActivity::class.java).apply {
                }
                intent.putExtra("Email", email)
                intent.putExtra("Pass", pass)
                startActivity(intent)

            } else {
                Toast.makeText(this, "Error en las credenciales $email o $pass", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigateToRegister() {

        mBinding.tvRegister.setOnClickListener {

            val intent = Intent(this, RegisterActivity::class.java).apply {
            }
            startActivity(intent)
        }
    }
}