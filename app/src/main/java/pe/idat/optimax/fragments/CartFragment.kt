package pe.idat.optimax.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import pe.idat.optimax.*
import pe.idat.optimax.databinding.FragmentCartBinding
import pe.idat.optimax.model.ArticleResponse
import kotlin.math.roundToInt

class CartFragment: Fragment(), OnClickListener{

    private lateinit var mBinding: FragmentCartBinding
    private lateinit var mAdapter: CartArticleAdapter
    private lateinit var listCartArticles: Array<ArticleResponse>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        mBinding = FragmentCartBinding.inflate(inflater,container,false)

        listCartArticles = arguments?.getSerializable("data") as Array<ArticleResponse>

        if (listCartArticles != null) {
            initRecyclerView()
        }

        var total = 0.0

        for (item in listCartArticles.indices) {

            total += listCartArticles[item].price.toDouble()
        }

        mBinding.tvSubTotal.text = total.roundToInt().toString()
        mBinding.tvIGV.text = ((total * 0.18).roundToInt()).toString()
        mBinding.tvTotal.text = (((total + (total * 0.18)) + 5).roundToInt()).toString()

        return mBinding.root
    }

    private fun initRecyclerView(){

        mAdapter = CartArticleAdapter(listCartArticles)
        mBinding.recyclerViewCart.layoutManager = GridLayoutManager(activity,1)
        mBinding.recyclerViewCart.adapter = mAdapter
    }

    override fun onClick(articleResponse: ArticleResponse) {
        TODO("Not yet implemented")
    }
}