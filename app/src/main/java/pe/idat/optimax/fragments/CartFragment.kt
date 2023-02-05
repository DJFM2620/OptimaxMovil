package pe.idat.optimax.fragments

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.gson.GsonBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import pe.idat.optimax.*
import pe.idat.optimax.databinding.FragmentCartBinding
import pe.idat.optimax.model.ArticleCartDto
import pe.idat.optimax.model.ArticleResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.time.format.DateTimeFormatter
import kotlin.collections.HashMap
import kotlin.math.roundToInt

class CartFragment: Fragment(), OnClickListener {

    private lateinit var mBinding: FragmentCartBinding
    private lateinit var mAdapter: CartArticleAdapter
    private lateinit var auth: FirebaseAuth
    private lateinit var directionClient: String
    private var listQuantities = HashMap<String, Int>()

    private lateinit var communicator: Communicator

    private val baseURL: String = "http://192.168.1.77:8040/idat/Api/"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        mBinding = FragmentCartBinding.inflate(inflater,container,false)

        initRecyclerView()

        communicator = activity as Communicator

        mBinding.btnRegisterOrder.setOnClickListener {

            getDirectionToValidate()
        }
        return mBinding.root
    }

    private fun initRecyclerView(){

        mAdapter = CartArticleAdapter(mutableListOf(), this)

        doAsync {
            val articlesDB= OptimaxApplication.database.OptimaxDao().findAllDB()

            uiThread {
                mAdapter.setCollection(articlesDB)
            }
        }
        mBinding.recyclerViewCart.layoutManager = GridLayoutManager(activity,1)
        mBinding.recyclerViewCart.adapter = mAdapter
    }

    override fun setTotal(total: Double) {

        var subTotal = total
        var igv = (total * 0.18)
        var newTotal = (igv + total + 5.0)

        mBinding.tvSubTotal.text = "S/${String.format("%.2f", subTotal)}"
        mBinding.tvIGV.text = "S/${String.format("%.2f", igv)}"
        mBinding.tvTotal.text = "S/${String.format("%.2f", newTotal)}"
    }

    override fun sumPrice(price: Double) {

        var oldSubTotal = mBinding.tvSubTotal.text.toString().split("S/")

        var subTotal = oldSubTotal[1].toDouble() + price
        var igv = (subTotal * 0.18)
        var newTotal = (igv + subTotal + 5.0)

        mBinding.tvSubTotal.text = "S/${String.format("%.2f", subTotal)}"
        mBinding.tvIGV.text = "S/${String.format("%.2f", igv)}"
        mBinding.tvTotal.text = "S/${String.format("%.2f", newTotal)}"
    }

    override fun subsTractPrice(price: Double) {

        var oldSubTotal = mBinding.tvSubTotal.text.toString().split("S/")

        var subTotal = (oldSubTotal[1].toDouble() - price)
        var igv = (subTotal * 0.18)
        var newTotal = (igv + subTotal + 5.0)

        mBinding.tvSubTotal.text = "S/${String.format("%.2f", subTotal)}"
        mBinding.tvIGV.text = "S/${String.format("%.2f", igv)}"
        mBinding.tvTotal.text = "S/${String.format("%.2f", newTotal)}"
    }

    override fun getQuantities(hashMap: HashMap<String, Int>) {
        listQuantities.clear()
        for ((key, value) in hashMap) {
            listQuantities[key] = value
        }
    }

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseURL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
    }

    private fun getDirectionToValidate(){

        auth = Firebase.auth
        val email = auth.currentUser!!.email

        CoroutineScope(Dispatchers.IO).launch {

            val call = getRetrofit().create(APIService::class.java).getDirectionClient("Cliente/Direccion/$email")

            requireActivity().runOnUiThread {
                if(call.isSuccessful) {

                    if (call.body().isNullOrEmpty()) {

                        showAlert("Error: ¡Información Incompleta!", "Lo sentimos, para completar su solicitud es necesario que proporcione su dirección. Por favor, verifique los campos en su Perfil e inténtelo de nuevo.")
                    }else {
                        directionClient = call.body().toString()
                        insertOrder()
                    }
                }
            }
        }
    }

    private fun insertOrder(){

        val user = Firebase.auth.currentUser
        var email = user!!.email.toString()

        var total = mBinding.tvTotal.text.toString().split("S/")
        var totalSplit = total[1].split(".")

        val map = HashMap<String,String>()

        map["cod_state"] = "1"
        map["email_client"] = email
        map["direction"] = directionClient
        map["articles"] = listQuantities.keys.toString()
        map["quantities"] = listQuantities.values.toString()
        map["subtotal"] = (totalSplit[0]+totalSplit[1])
        map["totaltv"] = mBinding.tvTotal.text.toString()

        communicator.sendOrder(map)
    }

    private fun showAlert(title: String, msg: String){

        val builder = AlertDialog.Builder(requireActivity())

        with(builder){

            builder.setTitle(title)
            builder.setMessage(msg)
            builder.setPositiveButton("Aceptar", null)
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}