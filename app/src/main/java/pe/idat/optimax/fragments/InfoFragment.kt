package pe.idat.optimax.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import pe.idat.optimax.R
import pe.idat.optimax.databinding.FragmentHomeBinding
import pe.idat.optimax.databinding.FragmentInfoBinding

class InfoFragment : Fragment() {

    private lateinit var mBinding: FragmentInfoBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        mBinding = FragmentInfoBinding.inflate(inflater,container,false)

        return mBinding.root
    }


}