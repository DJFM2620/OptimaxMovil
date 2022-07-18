package pe.idat.optimax

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import pe.idat.optimax.model.ArticleResponse
import pe.idat.optimax.model.ClientDto
import pe.idat.optimax.model.DistrictResponse
import pe.idat.optimax.databinding.ActivityMainBinding
import pe.idat.optimax.fragments.CartFragment
import pe.idat.optimax.fragments.HomeFragment
import pe.idat.optimax.fragments.ProfileFragment
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity(){

    private lateinit var mBinding: ActivityMainBinding
    private lateinit var mAdapter: ArticleAdapter
    private val listArticles = mutableListOf<ArticleResponse>()
    private val listDistricts = mutableListOf<DistrictResponse>()

    private val baseURL: String = "http://192.168.1.4:8040/idat/Api/"

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        replaceFragment(HomeFragment())

        mBinding.bottomNavigationView.setOnItemSelectedListener {

            when (it.itemId) {

                R.id.menu_home -> replaceFragment(HomeFragment())
                R.id.menu_cart -> replaceFragment(CartFragment())
                R.id.menu_profile -> replaceFragment(ProfileFragment())
                else -> {
                }
            }
            true
        }

        /*mBinding.svArticle.setOnQueryTextListener(this)*/
        /*initRecyclerView()
        findAll()

        searchDistrictById("1")
        insertOrder()*/
    }

    /*private fun searchDistrictById(Id: String) {
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

    private fun insertOrder(){

        mBinding.btnPayment.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch{

                val map=HashMap<String,String>()
                map["cod_state"] = "1"
                map["cod_client"] = "18"
                map["date"] = "1996-05-21"
                map["cod_article"] = "1"
                map["quantity"] = "1"
                map["subtotal"] = "200.0"

                val call =getRetrofit().create(APIService::class.java).postnewVenta(map)
                runOnUiThread {
                    if (call.isSuccessful){
                        Toast.makeText(this@MainActivity,"La orden ha sido registrado exitosamente",Toast.LENGTH_SHORT).show()

                    }else{
                        showError()
                    }
                }
            }
        }
    }*/

    private fun replaceFragment(fragment: Fragment) {

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
}