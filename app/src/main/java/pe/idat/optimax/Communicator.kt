package pe.idat.optimax

import androidx.fragment.app.Fragment

interface Communicator {

    fun startFragment(fragment: Fragment)
    fun passData(total: Int)
    fun sendOrder(hashMap: HashMap<String, String>)
}