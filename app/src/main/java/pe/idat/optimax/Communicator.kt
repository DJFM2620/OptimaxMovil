package pe.idat.optimax

import androidx.fragment.app.Fragment
import pe.idat.optimax.model.ArticleCartDto
import pe.idat.optimax.model.ArticleResponse

interface Communicator {

    fun startFragment(fragment: Fragment)
    fun passData(total: Int)
    fun sendOrder(hashMap: HashMap<String, String>)
}