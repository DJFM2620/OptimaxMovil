package pe.idat.optimax

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import pe.idat.optimax.model.ArticleResponse
import pe.idat.optimax.databinding.ActivityMainBinding
import pe.idat.optimax.fragments.CartFragment
import pe.idat.optimax.fragments.HomeFragment
import pe.idat.optimax.fragments.ProfileFragment
import pe.idat.optimax.model.ArticleCartDto

class MainActivity : AppCompatActivity(), Communicator{

    private lateinit var mBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        replaceFragment(HomeFragment())

        mBinding.bottomNavigationView.setOnItemSelectedListener {

            when (it.itemId) {

                R.id.menu_home -> replaceFragment(HomeFragment())
                /*R.id.menu_cart -> replaceFragment(CartFragment())*/
                R.id.menu_profile -> replaceFragment(ProfileFragment())
                else -> {
                }
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }

    override fun passData(articleList: MutableList<ArticleCartDto>) {

        val bundle = Bundle()
        bundle.putSerializable("data", articleList.toTypedArray())

        val fragment = CartFragment()
        fragment.arguments = bundle
        replaceFragment(fragment)
    }
}