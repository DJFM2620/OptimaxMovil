package pe.idat.optimax

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pe.idat.optimax.databinding.ActivityMainBinding
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.util.zip.Inflater

class MainActivity : AppCompatActivity(), SearchView.OnQueryTextListener {

    private lateinit var mBinding: ActivityMainBinding
    private lateinit var mAdapter: ArticleAdapter
    private val listArticles = mutableListOf<ArticleResponse>()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        /*val intent:Intent = intent
        var email = intent.getStringExtra("Email")
        var pass = intent.getStringExtra("Pass")

        mBinding.email.text = email
        mBinding.pass.text = pass*/

        mBinding.svArticle.setOnQueryTextListener(this)
        initRecyclerView()
    }

    private fun getRetrofit():Retrofit{

        return Retrofit.Builder()
                       .baseUrl("http://192.168.1.15:8040/idat/Api/")
                       .addConverterFactory(GsonConverterFactory.create())
                       .build()
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

    private fun initRecyclerView(){
        mAdapter = ArticleAdapter(listArticles)
        mBinding.recyclerView.layoutManager = LinearLayoutManager(this)
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
        return true
    }
}