package pe.idat.optimax

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.common.api.Api
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.newCoroutineContext
import okhttp3.OkHttpClient
import pe.idat.optimax.databinding.ActivityMainBinding
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity(), SearchView.OnQueryTextListener {

    private lateinit var mBinding: ActivityMainBinding
    private lateinit var mAdapter: ArticleAdapter
    private val listArticles = mutableListOf<ArticleResponse>()
    private val listDistricts = mutableListOf<DistrictResponse>()

    private val baseURL: String = "http://192.168.1.15:8040/idat/Api/"

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mBinding.svArticle.setOnQueryTextListener(this)
        initRecyclerView()
        findAll()

        searchDistrictById("1")
    }

    private fun getRetrofit():Retrofit{

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

    private fun findAll(){

        CoroutineScope(Dispatchers.IO).launch {

            val call = getRetrofit().create(APIService::class.java).getArticles(baseURL)
            val listArticleResponse: List<ArticleResponse> = call.body() ?: emptyList()

            runOnUiThread {
                if (call.isSuccessful){
                    listArticles.clear()
                    listArticles.addAll(listArticleResponse)
                    mAdapter.notifyDataSetChanged()
                }else{
                    showError()
                }
            }
        }
    }

    private fun searchDistrictById(Id: String) {
        mBinding.btnPost.setOnClickListener{

            CoroutineScope(Dispatchers.IO).launch {

                val call = getRetrofit().create(APIService::class.java).getDistrictById("Distrito/$Id")
                val district = call.body()

                runOnUiThread{
                    if(call.isSuccessful){
                        val districts = DistrictResponse(codDistrict = district?.codDistrict!!.toInt(), name = district?.name.toString())
                        insertClient(districts)
                    }else{
                        showError()
                    }
                }
            }
        }
    }

    private fun searchById(Id: String){

        CoroutineScope(Dispatchers.IO).launch {

            val call = getRetrofit().create(APIService::class.java).getArticlesById("$Id")
            val article = call.body()

            runOnUiThread{
                if(call.isSuccessful){
                    /*article?.imagen*/
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

    private fun insertClient(districtResponse: DistrictResponse){

        val client = ClientDto(
                                name = "MovilUser",
                                pSurname = "MovilPaternalSurname",
                                mSurname = "MovillMaternalSurname",
                                DNI = 12345678,
                                phone = 112233445,
                                email = "MovilUser@Hotmail.com",
                                ruc = "",
                                codDistrict = districtResponse
                                )

        CoroutineScope(Dispatchers.IO).launch {

            val call = getRetrofit().create(APIService::class.java).postNewClient(client)
            runOnUiThread{
                if(call.isSuccessful){
                    Toast.makeText(this@MainActivity, "El cliente ha sido agregado exitosamente", Toast.LENGTH_SHORT).show()
                }else{
                    showError()
                }
            }
        }
    }

    private fun initRecyclerView(){
        mAdapter = ArticleAdapter(listArticles)
        mBinding.recyclerView.layoutManager = GridLayoutManager(this,1)
        mBinding.recyclerView.adapter = mAdapter
    }

    private fun showError(){
        Toast.makeText(this, "Ha ocurrido un error", Toast.LENGTH_SHORT).show()
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
}