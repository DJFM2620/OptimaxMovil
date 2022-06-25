package pe.idat.optimax

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import pe.idat.optimax.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        navigateToLogin()
        validateInputs()

        mBinding.ietName.setOnFocusChangeListener { view, b ->  mBinding.tilName.error = null}
        mBinding.ietLastName.setOnFocusChangeListener { view, b ->  mBinding.tilLastName.error = null}
        mBinding.ietEmail.setOnFocusChangeListener { view, b ->  mBinding.tilEmail.error = null}
        with(mBinding.ietPassword){
            setOnFocusChangeListener {
                view, b ->  mBinding.tilPassword.error = null
                mBinding.tilPassword.isPasswordVisibilityToggleEnabled = true
            }
        }
        with(mBinding.ietConfirmPassword){
            setOnFocusChangeListener {
                view, b ->  mBinding.tilConfirmPassword.error = null
                mBinding.tilConfirmPassword.isPasswordVisibilityToggleEnabled = true
            }
        }
    }

    private fun navigateToLogin() {

        mBinding.tvLogin.setOnClickListener {

            val intent = Intent(this, LoginActivity::class.java).apply {
            }
            startActivity(intent)
        }
    }

    private fun validateInputs() {

        mBinding.btnRegister.setOnClickListener {

            val name = mBinding.ietName.text.toString().trim()
            val lastName = mBinding.ietLastName.text.toString().trim()
            val email = mBinding.ietEmail.text.toString().trim()
            val pass = mBinding.ietPassword.text.toString().trim()
            val confirmPass = mBinding.ietConfirmPassword.text.toString().trim()

            mBinding.tilName.clearFocus()
            mBinding.tilLastName.clearFocus()
            mBinding.tilEmail.clearFocus()
            mBinding.tilPassword.clearFocus()
            mBinding.tilConfirmPassword.clearFocus()
            mBinding.cbTermsAndConditions.clearFocus()


            if (name.isEmpty()){ mBinding.ietName.error = "Requerido" }
            else if (lastName.isEmpty()){ mBinding.ietLastName.error = "Requerido" }
            else if (email.isEmpty()){ mBinding.ietEmail.error = "Requerido" }
            else if (pass.isEmpty()){

                mBinding.ietPassword.error = "Requerido"
                mBinding.tilPassword.isPasswordVisibilityToggleEnabled = false

            } else if (confirmPass.isEmpty()) {

                mBinding.ietConfirmPassword.error = "Requerido"
                mBinding.tilConfirmPassword.isPasswordVisibilityToggleEnabled = false

            } else if (pass != confirmPass) {

                mBinding.tilConfirmPassword.error = "Las contraseñas no son iguales"
                mBinding.tilConfirmPassword.isPasswordVisibilityToggleEnabled = false

                mBinding.tilPassword.error = " "
                mBinding.tilPassword.isPasswordVisibilityToggleEnabled = false

            }else if (!mBinding.cbTermsAndConditions.isChecked){

                mBinding.cbTermsAndConditions.error = "Requerido que acepte"
            }else if(pass.length <= 6){

                mBinding.ietPassword.error = "Caracteres Mayor o igual a 7"
                mBinding.tilPassword.isPasswordVisibilityToggleEnabled = false

            }else {
                createUser(email, pass)
            }
        }
    }

    private fun createUser(email: String, pass: String){

        auth = Firebase.auth
        auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(this){

            if (it.isSuccessful){
                showHome(email, pass)
            }else{
                showAlertValidateUser()
            }
        }
    }

    private fun showHome(email: String, pass: String){

        val intent = Intent(this, MainActivity::class.java).apply {
        }
        intent.putExtra("Email", email)
        intent.putExtra("Pass", pass)
        startActivity(intent)
    }

    private fun showAlertValidateUser(){

        val builder = AlertDialog.Builder(this)

        with(builder){

            builder.setTitle("ERROR")
            builder.setMessage("Se ha producido un error autenticando al usuario")
            builder.setPositiveButton("Aceptar", null)
        }
        val dialog:AlertDialog = builder.create()
        dialog.show()
    }
}