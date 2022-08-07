package pe.idat.optimax

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.gson.GsonBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pe.idat.optimax.databinding.ActivityRegisterBinding
import pe.idat.optimax.model.ClientDto
import pe.idat.optimax.model.DistrictResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class RegisterActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth

    private val baseURL: String = "http://192.168.1.16:8040/idat/Api/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        navigateToLogin()

        with(mBinding){

            ietEmail.setOnFocusChangeListener { view, b ->

                mBinding.tilEmail.error = null
            }
            ietName.setOnFocusChangeListener { view, b ->

                mBinding.tilName.error = null
            }
            ietPSurname.setOnFocusChangeListener { view, b ->

                mBinding.tilPSurname.error = null
            }
            ietMSurname.setOnFocusChangeListener { view, b ->

                mBinding.tilMSurname.error = null
            }
            ietPassword.setOnFocusChangeListener { view, b ->

                mBinding.tilPassword.error = null
                mBinding.tilPassword.isPasswordVisibilityToggleEnabled = true
            }
            ietConfirmPassword.setOnFocusChangeListener { view, b ->

                mBinding.tilConfirmPassword.error = null
                mBinding.tilConfirmPassword.isPasswordVisibilityToggleEnabled = true
            }
        }

        mBinding.btnRegister.setOnClickListener {
            if(isValidateEmpty(mBinding.tilName, mBinding.tilPSurname,
                               mBinding.tilMSurname, mBinding.tilEmail,
                               mBinding.tilPassword, mBinding.tilConfirmPassword)){

                if (isValidatePass()) {
                    insertClient()
                }
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

    private fun isValidateEmpty (vararg txtArray: TextInputLayout): Boolean{

        var isValid = true

        for (txt in txtArray){

            txt.clearFocus()

            if(txt.editText?.text.toString()?.trim().isEmpty()){

                txt.error = getString(R.string.helper_required)
                isValid = false
            }
        }
        return isValid
    }

    private fun isValidatePass(): Boolean {

        var isValid = true

        val pass = mBinding.ietPassword.text.toString().trim()
        val confirmPass = mBinding.ietConfirmPassword.text.toString().trim()

        val tilPass = mBinding.tilPassword
        val tilConfirmPass = mBinding.tilConfirmPassword

        if (pass != confirmPass) {

            mBinding.tilConfirmPassword.error = "Las contraseñas no son iguales"
            mBinding.tilConfirmPassword.isPasswordVisibilityToggleEnabled = false

            mBinding.tilPassword.error = " "
            mBinding.tilPassword.isPasswordVisibilityToggleEnabled = false

            isValid = false

        }else if (!mBinding.cbTermsAndConditions.isChecked){

            mBinding.cbTermsAndConditions.error = "Requerido que acepte"

            isValid = false
        }else if(pass.length <= 6){

            mBinding.tilPassword.error = "Caracteres Mayor o igual a 7"
            mBinding.tilPassword.isPasswordVisibilityToggleEnabled = false

            isValid = false

        }else {

            tilPass.error = null
            tilConfirmPass.error = null
            mBinding.cbTermsAndConditions.error = null

            tilPass.isPasswordVisibilityToggleEnabled = true
            tilConfirmPass.isPasswordVisibilityToggleEnabled = true
        }
        return isValid
    }

    private fun getRetrofit(): Retrofit {

        val gson = GsonBuilder()
            .setLenient()
            .create()

        return Retrofit.Builder()
            .baseUrl(baseURL)
            .addConverterFactory(NullOnEmptyConverterFactory())
            .addConverterFactory(GsonConverterFactory.create(gson))
            /*.client(getClient())*/
            .build()
    }

    private fun insertClient(){

        var name = mBinding.ietName.text.toString().trim()
        var pSurname = mBinding.ietPSurname.text.toString().trim()
        var mSurname = mBinding.ietMSurname.text.toString().trim()
        var email = mBinding.ietEmail.text.toString().trim()
        var pass = mBinding.ietPassword.text.toString().trim()

        val district = DistrictResponse(codDistrict = 1, name = "Chorrillos")
        val clientDto = ClientDto(cod_client = 0 ,name = name, pSurname = pSurname, mSurname = mSurname, email = email, district = district)

        CoroutineScope(Dispatchers.IO).launch{

            val call =getRetrofit().create(APIService::class.java).postNewClient(clientDto)

            runOnUiThread {
                if (call.isSuccessful){
                    Toast.makeText(this@RegisterActivity, "Se registro exitosamente...!!", Toast.LENGTH_SHORT).show()
                    createUser(email, pass)

                }else{
                    showError()
                }
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

    private fun showError(){
        Toast.makeText(this, "Ha ocurrido un error", Toast.LENGTH_SHORT).show()
    }
}