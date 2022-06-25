package pe.idat.optimax

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import pe.idat.optimax.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        navigateToLogin()
        showHome()
    }

    private fun navigateToLogin() {

        mBinding.tvLogin.setOnClickListener {

            val intent = Intent(this, LoginActivity::class.java).apply {
            }
            startActivity(intent)
        }
    }

    private fun showHome() {

        mBinding.btnRegister.setOnClickListener {

            val intent = Intent(this, MainActivity::class.java).apply {
            }
            startActivity(intent)
        }
    }
}