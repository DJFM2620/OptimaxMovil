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
import pe.idat.optimax.APIService
import pe.idat.optimax.MyOrderAdapter
import pe.idat.optimax.NullOnEmptyConverterFactory
import pe.idat.optimax.databinding.FragmentMyOrdersBinding
import pe.idat.optimax.model.MyOrderResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MyOrdersFragment : Fragment() {

    private lateinit var mBinding: FragmentMyOrdersBinding
    private lateinit var mAdapter: MyOrderAdapter
    private val listOrders = mutableListOf<MyOrderResponse>()

    private val baseURL: String = "http://192.168.1.77:8040/idat/Api/"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        mBinding = FragmentMyOrdersBinding.inflate(inflater,container,false)

        initRecyclerView()
        findAll()

        return mBinding.root
    }

    private fun initRecyclerView(){

        mAdapter = MyOrderAdapter(listOrders)
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

            val call = getRetrofit().create(APIService::class.java).getOrdersByEmail("Ordenes/$email")
            val listOrdersResponse: List<MyOrderResponse> = call.body() ?: emptyList()

            activity?.runOnUiThread {
                if (call.isSuccessful) {
                    listOrders.clear()
                    listOrders.addAll(listOrdersResponse)
                    mAdapter.notifyDataSetChanged()
                } else {
                    Toast.makeText( activity, "Hubo un error al listar los pedidos, contacte con Soporte Tecnico, porfavor...", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}