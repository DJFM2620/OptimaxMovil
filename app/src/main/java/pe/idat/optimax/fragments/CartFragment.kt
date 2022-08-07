package pe.idat.optimax.fragments

import android.os.Build
import android.os.Bundle
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
import pe.idat.optimax.*
import pe.idat.optimax.databinding.FragmentCartBinding
import pe.idat.optimax.model.ArticleCartDto
import pe.idat.optimax.model.ArticleResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.HashMap
import kotlin.math.roundToInt

class CartFragment: Fragment(), OnClickListener{

    private lateinit var mBinding: FragmentCartBinding
    private lateinit var mAdapter: CartArticleAdapter
    private lateinit var listCartArticles: Array<ArticleCartDto>
    /*private lateinit var listCodCli: Array<Int>*/
    private var email: String = ""

    private val baseURL: String = "http://192.168.1.16:8040/idat/Api/"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        mBinding = FragmentCartBinding.inflate(inflater,container,false)

        listCartArticles = arguments?.getSerializable("data") as Array<ArticleCartDto>

        if (listCartArticles != null) {
            initRecyclerView()
        }

        var total = 0.0

        for (item in listCartArticles.indices) {

            total += listCartArticles[item].price.toDouble()
        }

        mBinding.tvSubTotal.text = total.roundToInt().toString()
        mBinding.tvIGV.text = ((total * 0.18).roundToInt()).toString()
        mBinding.tvTotal.text = (((total + (total * 0.18)) + 5).roundToInt()).toString()

        insertOrder()

        /*mBinding.btnRegisterOrder.setOnClickListener {

            searchClientByEmail("joel@hotmail.com")
            Toast.makeText( activity,"${listCodCli.size}", Toast.LENGTH_SHORT).show()
        }*/

        return mBinding.root
    }

    private fun initRecyclerView(){

        mAdapter = CartArticleAdapter(listCartArticles)
        mBinding.recyclerViewCart.layoutManager = GridLayoutManager(activity,1)
        mBinding.recyclerViewCart.adapter = mAdapter
    }

    override fun onClick(articleResponse: ArticleResponse) {
        TODO("Not yet implemented")
    }

    private fun getRetrofit(): Retrofit {

        return Retrofit.Builder()
            .baseUrl(baseURL)
            .addConverterFactory(NullOnEmptyConverterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            /*.client(getClient())*/
            .build()
    }

    private fun searchClientByEmail(Email: String){

        CoroutineScope(Dispatchers.IO).launch {

            val call = getRetrofit().create(APIService::class.java).getClientByEmail("Cliente/$Email")
            val client = call.body()

            activity?.runOnUiThread{
                if(call.isSuccessful){

                    if (client != null) {
                        /*listCodCli.set(0, client)*/
                    }
                    else {
                        Toast.makeText(activity, "El cliente no existe, registrese porfavor", Toast.LENGTH_SHORT).show()
                    }
                }else{
                    showError()
                }
            }
        }
    }

   private fun insertOrder(){

        val user = Firebase.auth.currentUser
        user?.let {
            email = user.email.toString()
        }

        mBinding.btnRegisterOrder.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch{

                listCartArticles = arguments?.getSerializable("data") as Array<ArticleCartDto>

                searchClientByEmail(email)


                val dateOrder: DateTimeFormatter = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    DateTimeFormatter.ofPattern("yyyy-MM-dd")
                } else {
                    TODO("VERSION.SDK_INT < O")
                }

                var listCodArticle = mutableListOf<Int>()

                for (item in listCartArticles.indices) {

                    listCodArticle.add(listCartArticles[item].codArticle.toInt())
                }

                //listCartArticles.get(1).quantity
                //dateOrder.format(LocalDateTime.now())
                //listCodCli[0].toString()

                val map=HashMap<String,String>()
                map["cod_state"] = "1"
                map["cod_client"] = "33"
                map["cod_article"] = listCodArticle.toString()
                map["quantity"] = "10"
                map["subtotal"] = "500.0"

                val call =getRetrofit().create(APIService::class.java).postnewVenta(map)
                activity?.runOnUiThread {
                    if (call.isSuccessful){
                        Toast.makeText(activity,"La orden ha sido registrado exitosamente",Toast.LENGTH_SHORT).show()

                    }else{
                        showError()
                    }
                }
            }
        }
    }

    private fun showError(){
        Toast.makeText(activity, "Ha ocurrido un error", Toast.LENGTH_SHORT).show()
    }
}