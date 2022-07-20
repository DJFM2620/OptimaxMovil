package pe.idat.optimax.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import pe.idat.optimax.databinding.FragmentHomeBinding
import pe.idat.optimax.model.ArticleResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import pe.idat.optimax.*

class HomeFragment : Fragment() , SearchView.OnQueryTextListener, OnClickListener{

    private lateinit var mBinding: FragmentHomeBinding
    private lateinit var mAdapter: ArticleAdapter
    private val listArticles = mutableListOf<ArticleResponse>()
    private val listCart = mutableListOf<ArticleResponse>()

    private lateinit var communicator: Communicator

    private val baseURL: String = "http://192.168.1.4:8040/idat/Api/"

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

        listCart.add(articleResponse)
    }
}