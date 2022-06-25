package pe.idat.optimax

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import pe.idat.optimax.databinding.ActivityLoginBinding

class LoginActivity: AppCompatActivity() {

    private lateinit var mBinding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        mBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
    }

    fun showHome(view: View){

        val email = mBinding.tieEmail.text.toString().trim()
        val pass = mBinding.tiePassword.text.toString().trim()

        if(email == "Joel@hotmail.es" && pass == "123"){

            val intent = Intent(this, MainActivity::class.java).apply {
            }
            intent.putExtra("Email", email)
            intent.putExtra("Pass", pass)
            startActivity(intent)

        }else{
            Toast.makeText(this, "Error en las credenciales", Toast.LENGTH_SHORT).show()
        }
    }
}