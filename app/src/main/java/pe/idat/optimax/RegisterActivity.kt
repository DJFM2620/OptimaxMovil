package pe.idat.optimax

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
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
import pe.idat.optimax.model.ClientInfoDto
import pe.idat.optimax.model.DistrictDto
import pe.idat.optimax.model.DistrictResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RegisterActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth

    private val baseURL: String = "http://192.168.1.77:8040/idat/Api/"
    private val districtMap = mutableMapOf<String, Int>()
    private var district: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        navigateToLogin()

        mBinding.spnDistricts.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem = parent?.getItemAtPosition(position)
                val selectedCode = districtMap[selectedItem]

                district = selectedCode!!
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        with(mBinding){

            ietEmail.setOnFocusChangeListener { view, b ->

                mBinding.tilEmail.error = null
            }
            ietDni.setOnFocusChangeListener { view, b ->

                mBinding.tilDni.error = null
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
                               mBinding.tilPassword, mBinding.tilConfirmPassword,
                               mBinding.tilDni)){

                if (isValidatePass()) {
                    validateUserFirebase(mBinding.ietEmail.text.toString().trim())
                }
            }
        }
        getDistricts()
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
            .build()
    }

    private fun validateClientExistence(){

        var dni = mBinding.ietDni.text.toString().trim()

        CoroutineScope(Dispatchers.IO).launch {

            val call = getRetrofit().create(APIService::class.java).getValidateClientByDni("Cliente/Existencia/$dni")
            val response = call.body()

            runOnUiThread {
                if (call.isSuccessful){
                    if(response == 0){
                        insertClient()
                    }else {
                        updateClient()//Actualizamos el Email del cliente
                    }
                }
            }
        }
    }

    private fun getDistricts() {

        val spinnerAdapter = ArrayAdapter<String>(this@RegisterActivity, android.R.layout.simple_spinner_item)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        mBinding.spnDistricts.adapter = spinnerAdapter

        CoroutineScope(Dispatchers.IO).launch {

            val call = getRetrofit().create(APIService::class.java).getDistricts("Distritos")
            val listDistrictResponse: List<DistrictResponse> = call.body() ?: emptyList()

            runOnUiThread {
                if (call.isSuccessful) {
                    for (district in listDistrictResponse) {
                        spinnerAdapter.add(district.name)
                        districtMap[district.name] = district.codDistrict
                    }
                }
            }
        }
    }

    private fun updateClient() {

        CoroutineScope(Dispatchers.IO).launch {

            var name = mBinding.ietName.text?.trim().toString()
            var pSurname = mBinding.ietPSurname.text?.trim().toString()
            var mSurname = mBinding.ietMSurname.text?.trim().toString()
            var DNI = mBinding.ietDni.text?.trim().toString().toInt()
            var email = mBinding.ietEmail.text?.trim().toString()
            var pass = mBinding.ietPassword.text?.trim().toString()

            var client = ClientInfoDto(
                name = name,
                pSurname = pSurname,
                mSurname = mSurname,
                DNI = DNI,
                email = email)

            val call = getRetrofit().create(APIService::class.java).putUpdateInfoClient(client)

            runOnUiThread {
                if (call.isSuccessful) {
                    createUser(email, pass)
                    showAlert("Usuario Creado", "Nuestras verificaciones revelaron que posee una cuenta en nuestro sitio web. Con los datos que usted nos brinda aquí, nos esforzaremos por brindarle una actualización majestuosa en su perfil en línea.")
                } else {
                    showAlert("Error", "Hubo un problema al crear el usuario, intentelo dentro de unos minutos")
                    showError()
                }
            }
        }
    }

    private fun insertClient(){

        var name = mBinding.ietName.text.toString().trim()
        var pSurname = mBinding.ietPSurname.text.toString().trim()
        var mSurname = mBinding.ietMSurname.text.toString().trim()
        var dni = mBinding.ietDni.text.toString().trim().toInt()
        var email = mBinding.ietEmail.text.toString().trim()
        var pass = mBinding.ietPassword.text.toString().trim()

        val districtDto = DistrictDto(codDistrict = district)
        val clientDto = ClientDto(cod_client = 0 ,name = name, pSurname = pSurname, mSurname = mSurname, email = email, DNI = dni, district = districtDto)

        CoroutineScope(Dispatchers.IO).launch{

            val call = getRetrofit().create(APIService::class.java).postNewClient(clientDto)

            runOnUiThread {
                if (call.isSuccessful){
                    createUser(email, pass)
                    showAlert("Usuario Creado", "Su cuenta ha sido creada con un éxito sin igual, y está lista para ser utilizada en nuestro sitio web.")
                }else{
                    showAlert("Error", "Hubo un problema al crear el usuario, intentelo dentro de unos minutos")
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
                showAlert("Error", "Hubo un problema al crear el usuario, intentelo dentro de unos minutos")
            }
        }
    }

    private fun validateUserFirebase(email: String){

        auth = Firebase.auth

        auth.fetchSignInMethodsForEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val signInMethods = task.result?.signInMethods
                    if (signInMethods.isNullOrEmpty()) {

                        validateClientExistence()

                    } else {
                        showAlert("Informacion", "Lo sentimos, el correo electrónico que ha ingresado ya está en uso por otro usuario.")
                    }
                } else {
                    showAlert("Error", "Lo sentimos, hubo problemas al verificar los datos.")
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

    private fun showAlert(title: String, msg: String){

        val builder = AlertDialog.Builder(this)

        with(builder){

            builder.setTitle(title)
            builder.setMessage(msg)
            builder.setPositiveButton("Aceptar", null)
        }
        val dialog:AlertDialog = builder.create()
        dialog.show()
    }

    private fun showError(){
        Toast.makeText(this, "Ha ocurrido un error", Toast.LENGTH_SHORT).show()
    }
}