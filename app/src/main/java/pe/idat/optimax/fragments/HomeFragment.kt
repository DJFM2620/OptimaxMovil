package pe.idat.optimax.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pe.idat.optimax.*
import pe.idat.optimax.databinding.FragmentHomeBinding
import pe.idat.optimax.model.ArticleResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class HomeFragment : Fragment() , SearchView.OnQueryTextListener {

    private lateinit var mBinding: FragmentHomeBinding
    private lateinit var mAdapter: ArticleAdapter
    private val listArticles = mutableListOf<ArticleResponse>()

    private lateinit var communicator: Communicator

    private val baseURL: String = "http://192.168.1.77:8040/idat/Api/"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        mBinding = FragmentHomeBinding.inflate(inflater,container,false)

        mBinding.svArticle.setOnQueryTextListener(this)
        initRecyclerView()
        findAll()

        val imageSlider = mBinding.imgSlider
        val imageList =ArrayList<SlideModel>()

        imageList.add(SlideModel("https://us.123rf.com/450wm/seoterra/seoterra1312/seoterra131200002/24171162-muchacha-con-estilo-joven-en-las-lentes-%C3%B3pticas-que-presenta-en-estudio-inteligente-y-bellos-espect%C3%A1.jpg"))
        imageList.add(SlideModel("https://i.pinimg.com/736x/4e/5f/1c/4e5f1c36b68fb55d866e6b147879da9e.jpg"))
        imageList.add(SlideModel("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSNWB0wQ26kMdaoqD8TZeAIUJZEmfZc9m5mJ8-z_brgik0PqJGcwp4Miyvx18Mm8nc859s&usqp=CAU"))

        imageSlider.setImageList(imageList,ScaleTypes.CENTER_CROP)

        communicator = activity as Communicator

        mBinding.btnCart.setOnClickListener {
            val fr = CartFragment()
            communicator.startFragment(fr)
        }

        return mBinding.root
    }

    private fun getRetrofit(): Retrofit {

        return Retrofit.Builder()
            .baseUrl(baseURL)
            .addConverterFactory(NullOnEmptyConverterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
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
                    Toast.makeText( activity, "Hubo un error al cargar los articulos, contacte con Soporte Tecnico, porfavor...", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun initRecyclerView(){

        mAdapter = ArticleAdapter(listArticles)
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
        Toast.makeText(activity, "Hubo un error al buscar el articulo, contacte con Soporte Tecnico, porfavor...", Toast.LENGTH_SHORT).show()
    }
}