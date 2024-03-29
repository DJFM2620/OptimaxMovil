package pe.idat.optimax

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import pe.idat.optimax.databinding.ActivityMainBinding
import pe.idat.optimax.fragments.*

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
                R.id.menu_profile -> replaceFragment(ProfileFragment())
                R.id.menu_appointment -> replaceFragment(AppointmentFragment())
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

    override fun startFragment(fragment: Fragment) {

        replaceFragment(fragment)
    }

    override fun passData(total: Int) {

        val bundle = Bundle()
        bundle.putInt("total", total)

        val fragment = PaymentFragment()
        fragment.arguments = bundle

        replaceFragment(fragment)
    }

    override fun sendOrder(hashMap: HashMap<String, String>) {

        val bundle = Bundle()
        bundle.putSerializable("order", hashMap)

        val fragment = PaymentFragment()
        fragment.arguments = bundle

        replaceFragment(fragment)
    }
}