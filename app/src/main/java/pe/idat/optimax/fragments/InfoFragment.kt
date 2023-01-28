package pe.idat.optimax.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pe.idat.optimax.APIService
import pe.idat.optimax.NullOnEmptyConverterFactory
import pe.idat.optimax.databinding.FragmentInfoBinding
import pe.idat.optimax.model.ClientInfoDto
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class InfoFragment : Fragment() {

    private lateinit var mBinding: FragmentInfoBinding
    private val baseURL: String = "http://192.168.1.77:8040/idat/Api/"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        mBinding = FragmentInfoBinding.inflate(inflater,container,false)
        getClientByEmail()
        updateClient()
        return mBinding.root
    }

    private fun getGson(): Gson {
        return GsonBuilder()
            .setLenient()
            .create()
    }

    private fun getRetrofit(): Retrofit {

        return Retrofit.Builder()
            .baseUrl(baseURL)
            .addConverterFactory(NullOnEmptyConverterFactory())
            .addConverterFactory(GsonConverterFactory.create(getGson()))
            .build()
    }

    private fun updateClient() {

        val user = Firebase.auth.currentUser
        var email = user!!.email.toString()

        mBinding.btnUpdate.setOnClickListener {

            CoroutineScope(Dispatchers.IO).launch {

                var name = mBinding.ietName.text?.trim().toString()
                var pSurname = mBinding.ietPSurname.text?.trim().toString()
                var mSurname = mBinding.ietMSurname.text?.trim().toString()
                var DNI = mBinding.ietDni.text?.trim().toString().toInt()

                var client = ClientInfoDto(
                    name = name,
                    pSurname = pSurname,
                    mSurname = mSurname,
                    DNI = DNI,
                    email = email)

                val call = getRetrofit().create(APIService::class.java).putUpdateInfoClient(client)
                activity?.runOnUiThread {
                    if (call.isSuccessful) {
                        getGson()
                        Toast.makeText(activity,"Sus datos han sido actualizados exitosamente...!", Toast.LENGTH_SHORT).show()
                    } else {
                        Log.d("mensaje","no se pudo...")
                        showError()
                    }
                }
            }
        }
    }
    private fun getClientByEmail() {

        val user = Firebase.auth.currentUser
        var email = user!!.email.toString()

        CoroutineScope(Dispatchers.IO).launch {

            val call = getRetrofit().create(APIService::class.java).getClientByEmail("Cliente/$email")
            val client = call.body()

            activity?.runOnUiThread {
                if (call.isSuccessful) {
                    getGson()
                    if (client != null) {

                        mBinding.ietName.setText(client.name)
                        mBinding.ietPSurname.setText(client.pSurname)
                        mBinding.ietMSurname.setText(client.mSurname)
                        mBinding.ietDni.setText(client.DNI.toString())
                    } else {
                        Toast.makeText(activity,"El cliente no existe, registrese porfavor", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    showError()
                }
            }
        }
    }

    private fun showError() {
        Toast.makeText(activity, "Ha ocurrido un error", Toast.LENGTH_SHORT).show()
    }
}