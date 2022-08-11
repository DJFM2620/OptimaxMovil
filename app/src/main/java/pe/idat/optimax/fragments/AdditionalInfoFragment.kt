package pe.idat.optimax.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import pe.idat.optimax.Communicator
import pe.idat.optimax.R
import pe.idat.optimax.databinding.FragmentAdditionalInfoBinding
import pe.idat.optimax.databinding.FragmentHomeBinding

class AdditionalInfoFragment : Fragment() {

    private lateinit var mBinding: FragmentAdditionalInfoBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        mBinding = FragmentAdditionalInfoBinding.inflate(inflater,container,false)

        return mBinding.root
    }
}