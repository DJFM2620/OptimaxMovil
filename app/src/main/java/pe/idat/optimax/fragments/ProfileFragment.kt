package pe.idat.optimax.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pe.idat.optimax.*
import pe.idat.optimax.databinding.FragmentProfileBinding
import pe.idat.optimax.model.ListViewOption
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ProfileFragment : Fragment() {

    private lateinit var mBinding: FragmentProfileBinding
    private lateinit var auth: FirebaseAuth

    private val baseURL: String = "http://192.168.1.77:8040/idat/Api/"
    private lateinit var communicator: Communicator

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        mBinding = FragmentProfileBinding.inflate(inflater, container, false)

        communicator = activity as Communicator

        val option1 = ListViewOption("Informacion Personal", R.drawable.ic_person)
        val option2 = ListViewOption("Informacion Adicional", R.drawable.ic_info_add)
        val option3 = ListViewOption("Mis Citas", R.drawable.ic_calendar)
        val option4 = ListViewOption("Mis Pedidos", R.drawable.ic_order)
        val option5 = ListViewOption("LogOut", R.drawable.ic_logout)

        val listOptions = listOf(option1, option2, option3, option4, option5)

        val adapter = ListViewAdapter(requireActivity(), listOptions)
        mBinding.lvOptions.adapter = adapter

        mBinding.lvOptions.setOnItemClickListener { adapterView, view, position, id ->

            when(adapterView.getItemIdAtPosition(position)){

                0L -> communicator.startFragment(InfoFragment())
                1L -> communicator.startFragment(AdditionalInfoFragment())
                2L -> communicator.startFragment(MyAppointmentFragment())
                3L -> communicator.startFragment(MyOrdersFragment())
                4L -> {
                    auth = Firebase.auth
                    auth.signOut()
                    val intent = Intent(requireActivity(), LoginActivity::class.java).apply {
                    }
                    startActivity(intent)
                }

                else -> {
                }
            }
        }
        getClientByEmail()

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

    private fun getClientByEmail(){

        val user = Firebase.auth.currentUser
        var email = user!!.email.toString()

        CoroutineScope(Dispatchers.IO).launch {

            val call = getRetrofit().create(APIService::class.java).getClientByEmail("Cliente/$email")

            activity?.runOnUiThread {
                if (call.isSuccessful){
                    mBinding.tvFullName.text = "${call.body()!!.name} ${call.body()!!.pSurname} ${call.body()!!.mSurname}"
                    mBinding.tvEmail.text = call.body()?.email
                    mBinding.tvPhone.text = call.body()?.phone.toString()
                }else{
                    Toast.makeText(activity, "Hubo un error al cargar sus datos, contacte con Soporte Tecnico, porfavor...",Toast.LENGTH_SHORT)
                }
            }
        }
    }
}