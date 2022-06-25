package pe.idat.optimax

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import pe.idat.optimax.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        mBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        navigateToRegister()
        validateInputs()

        mBinding.ietEmail.setOnFocusChangeListener { view, b ->  mBinding.tilEmail.error = null}
        with(mBinding.ietPassword){
            setOnFocusChangeListener {
                    view, b ->  mBinding.tilPassword.error = null
                    mBinding.tilPassword.isPasswordVisibilityToggleEnabled = true
            }
        }
    }

    private fun validateInputs(){

        mBinding.btnLogin.setOnClickListener {

            mBinding.tilEmail.clearFocus()
            mBinding.tilPassword.clearFocus()

            val email = mBinding.ietEmail.text.toString().trim()
            val pass = mBinding.ietPassword.text.toString().trim()

            mBinding.tilEmail.clearFocus()
            mBinding.tilPassword.clearFocus()

            if (email.isEmpty()){ mBinding.ietEmail.error = "Requerido" }
            else if (pass.isEmpty()){

                mBinding.ietPassword.error = "Requerido"
                mBinding.tilPassword.isPasswordVisibilityToggleEnabled = false

            }else {
                loginUser(email, pass)
            }
        }
    }

    private fun showHome(email: String, pass: String) {

        mBinding.btnLogin.setOnClickListener {

            val intent = Intent(this, MainActivity::class.java).apply {
                }
                intent.putExtra("Email", email)
                intent.putExtra("Pass", pass)
                startActivity(intent)
            }
        }

    private fun navigateToRegister() {

        mBinding.tvRegister.setOnClickListener {

            val intent = Intent(this, RegisterActivity::class.java).apply {
            }
            startActivity(intent)
        }
    }

    private fun loginUser(email: String, pass: String){

        auth = Firebase.auth
        auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(this){

            if (it.isSuccessful){
                showHome(email, pass) //Contraseña 123456789
            }else{
                showAlertValidateUser()
                it?.exception?.printStackTrace()
            }
        }
    }

    private fun showAlertValidateUser(){

        val builder = AlertDialog.Builder(this)

        with(builder){

            builder.setTitle("ERROR")
            builder.setMessage("Se ha producido un error autenticando al usuario")
            builder.setPositiveButton("Aceptar", null)
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}