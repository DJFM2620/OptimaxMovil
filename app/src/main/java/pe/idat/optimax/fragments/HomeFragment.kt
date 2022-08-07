package pe.idat.optimax.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import pe.idat.optimax.*
import pe.idat.optimax.databinding.FragmentHomeBinding
import pe.idat.optimax.model.ArticleCartDto
import pe.idat.optimax.model.ArticleResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class HomeFragment : Fragment() , SearchView.OnQueryTextListener, OnClickListener{

    private lateinit var mBinding: FragmentHomeBinding
    private lateinit var mAdapter: ArticleAdapter
    private val listArticles = mutableListOf<ArticleResponse>()
    private val listCart = mutableListOf<ArticleCartDto>()

    private lateinit var communicator: Communicator

    private val baseURL: String = "http://192.168.1.16:8040/idat/Api/"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        mBinding = FragmentHomeBinding.inflate(inflater,container,false)

        mBinding.svArticle.setOnQueryTextListener(this)
        initRecyclerView()
        findAll()

        communicator = activity as Communicator

        mBinding.btnCart.setOnClickListener{

            communicator.passData(listCart)
        }

        return mBinding.root
    }

    private fun getRetrofit(): Retrofit {

        return Retrofit.Builder()
            .baseUrl(baseURL)
            .addConverterFactory(NullOnEmptyConverterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            /*.client(getClient())*/
            .build()
    }

    private fun getClient(): OkHttpClient {

        val client = OkHttpClient.Builder().addInterceptor(HeaderInterceptor()).build()

        return client
    }

    private fun recepcion(){

        CoroutineScope(Dispatchers.IO).launch{

            var listQuantities = mutableListOf<Int>()
            listQuantities.add(1)
            listQuantities.add(10)
            listQuantities.add(5)
            listQuantities.add(4)

            /*var listCodArticle = mutableListOf<Int>()

            for (item in listArticles.indices) {

                 listCodArticle.add(listArticles[item].codArticle.toInt())
            }*/

            val map=HashMap<String,String>()
            map["cod_article"] = listQuantities.toString()

            val call =getRetrofit().create(APIService::class.java).postRecepcion(map)
            activity?.runOnUiThread {
                if (call.isSuccessful){
                    Toast.makeText(activity,"La data ha sido enviada exitosamente",Toast.LENGTH_SHORT).show()

                }else{
                    showError()
                }
            }
        }
    }

    private fun findAll() {

        CoroutineScope(Dispatchers.IO).launch {

            val call = getRetrofit().create(APIService::class.java).getArticles(baseURL)
            val listArticleResponse: List<ArticleResponse> = call.body() ?: emptyList()

            activity?.runOnUiThread {
                if (call.isSuccessful) {
                    listArticles.clear()
                    listArticles.addAll(listArticleResponse)
                    mAdapter.notifyDataSetChanged()
                } else {
                    Toast.makeText( activity, "Error al listar UwU", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun initRecyclerView(){

        mAdapter = ArticleAdapter(listArticles, this)
        mBinding.recyclerView.layoutManager = GridLayoutManager(activity,1)
        mBinding.recyclerView.adapter = mAdapter
    }

    private fun searchById(Id: String){

        CoroutineScope(Dispatchers.IO).launch {

            val call = getRetrofit().create(APIService::class.java).getArticlesById("$Id")
            val article = call.body()

            activity?.runOnUiThread{
                if(call.isSuccessful){
                    val articles = ArticleResponse(codArticle = article?.codArticle.toString(),
                        price = article?.price.toString(),
                        imagen = article?.imagen.toString())
                    listArticles.clear()
                    listArticles.addAll(listOf(articles))
                    mAdapter.notifyDataSetChanged()
                }else{
                    showError()
                }
            }
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {

        if(!query.isNullOrEmpty()){
            searchById(query.toLowerCase())
        }
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {

        if(newText.isNullOrEmpty()){
            if(!mBinding.svArticle.isIconified){
                findAll()
            }
        }
        return true
    }

    private fun showError(){
        Toast.makeText(activity, "Ha ocurrido un error", Toast.LENGTH_SHORT).show()
    }

    override fun onClick(articleResponse: ArticleResponse) {

        val articleCartDto = ArticleCartDto(codArticle = articleResponse.codArticle,
                                            price = articleResponse.price,
                                            imagen = articleResponse.imagen)

        listCart.add(articleCartDto)
    }
}