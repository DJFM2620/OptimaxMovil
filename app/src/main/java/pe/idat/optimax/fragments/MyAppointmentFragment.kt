package pe.idat.optimax.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pe.idat.optimax.*
import pe.idat.optimax.databinding.FragmentHomeBinding
import pe.idat.optimax.databinding.FragmentMyAppointmentBinding
import pe.idat.optimax.model.ArticleResponse
import pe.idat.optimax.model.MyAppointmentResponse
import pe.idat.optimax.model.MyOrderResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MyAppointmentFragment : Fragment() {

    private lateinit var mBinding: FragmentMyAppointmentBinding
    private lateinit var mAdapter: MyAppointmentAdapter
    private val listAppointments = mutableListOf<MyAppointmentResponse>()

    private val baseURL: String = "http://192.168.1.16:8040/idat/Api/"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        mBinding = FragmentMyAppointmentBinding.inflate(inflater,container,false)

        initRecyclerView()
        findAll()

        return mBinding.root
    }

    private fun initRecyclerView(){

        mAdapter = MyAppointmentAdapter(listAppointments)
        mBinding.recyclerView.layoutManager = GridLayoutManager(activity,1)
        mBinding.recyclerView.adapter = mAdapter
    }

    private fun getRetrofit(): Retrofit {

        return Retrofit.Builder()
            .baseUrl(baseURL)
            .addConverterFactory(NullOnEmptyConverterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun findAll() {

        val user = Firebase.auth.currentUser
        var email = user!!.email

        CoroutineScope(Dispatchers.IO).launch {

            val call = getRetrofit().create(APIService::class.java).getAppointmetsByEmail("Cita/$email")
            val listAppointmentResponse: List<MyAppointmentResponse> = call.body() ?: emptyList()

            activity?.runOnUiThread {
                if (call.isSuccessful) {
                    listAppointments.clear()
                    listAppointments.addAll(listAppointmentResponse)
                    mAdapter.notifyDataSetChanged()
                } else {
                    Toast.makeText( activity, "Error al listar UwU", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}