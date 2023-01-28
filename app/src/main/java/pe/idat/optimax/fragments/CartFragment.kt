package pe.idat.optimax.fragments

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
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
import java.time.format.DateTimeFormatter
import kotlin.collections.HashMap
import kotlin.math.roundToInt

class CartFragment: Fragment(), OnClickListener {

    private lateinit var mBinding: FragmentCartBinding
    private lateinit var mAdapter: CartArticleAdapter
    private  var listQuantities = HashMap<String, Int>()

    private lateinit var communicator: Communicator

    private val baseURL: String = "http://192.168.1.77:8040/idat/Api/"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        mBinding = FragmentCartBinding.inflate(inflater,container,false)

        initRecyclerView()

        communicator = activity as Communicator

        mBinding.btnRegisterOrder.setOnClickListener {

            insertOrder()
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
            .addConverterFactory(NullOnEmptyConverterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

   private fun insertOrder(){


       val user = Firebase.auth.currentUser
       var email = user!!.email.toString()

       var total = mBinding.tvTotal.text.toString().split("S/")
       var totalSplit = total[1].split(".")

       val map=HashMap<String,String>()

       map["cod_state"] = "1"
       map["email_client"] = email
       map["articles"] = listQuantities.keys.toString()
       map["quantities"] = listQuantities.values.toString()
       map["subtotal"] = (totalSplit[0]+totalSplit[1])
       map["totaltv"] = mBinding.tvTotal.text.toString()

       communicator.sendOrder(map)
    }
}