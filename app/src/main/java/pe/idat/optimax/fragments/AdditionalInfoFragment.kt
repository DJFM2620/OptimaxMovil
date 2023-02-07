package pe.idat.optimax.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pe.idat.optimax.APIService
import pe.idat.optimax.NullOnEmptyConverterFactory
import pe.idat.optimax.R
import pe.idat.optimax.databinding.FragmentAdditionalInfoBinding
import pe.idat.optimax.model.ClientAddInfoDto
import pe.idat.optimax.model.DistrictDto
import pe.idat.optimax.model.DistrictResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class AdditionalInfoFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mBinding: FragmentAdditionalInfoBinding
    private lateinit var map: GoogleMap
    private lateinit var spinnerAdapter: ArrayAdapter<String>

    private var currentMarker: Marker? = null
    private val baseURL: String = "http://192.168.1.77:8040/idat/Api/"
    private var dniResponse: Int = 0
    private val districtMap = mutableMapOf<String, Int>()
    private var district: Int = 0
    private var districtClient: Int = 0

    companion object {
        const val REQUEST_CODE_LOCATION = 0
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        mBinding = FragmentAdditionalInfoBinding.inflate(inflater,container,false)

        val supportMapFragment =
            childFragmentManager.findFragmentById(R.id.google_map) as SupportMapFragment?
        supportMapFragment!!.getMapAsync(this)

        spinnerAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        mBinding.spnDistricts.adapter = spinnerAdapter

        mBinding.spnDistricts.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem = parent?.getItemAtPosition(position)
                val selectedCode = districtMap[selectedItem]

                district = selectedCode!!
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        getClientByEmail()
        updateClient()
        getDistricts()

        return mBinding.root
    }

    private fun getGson(): Gson {
        return GsonBuilder()
            .setLenient()
            .create()
    }
    private fun showError() {
        Toast.makeText(activity, "Ha ocurrido un error, contacte con Soporte Tecnico, porfavor...", Toast.LENGTH_SHORT).show()
    }

    private fun getRetrofit(): Retrofit {

        return Retrofit.Builder()
            .baseUrl(baseURL)
            .addConverterFactory(NullOnEmptyConverterFactory())
            .addConverterFactory(GsonConverterFactory.create(getGson()))
            .build()
    }

    override fun onMapReady(googleMap: GoogleMap) {

        map = googleMap

        val coordinates = LatLng(-12.1214598, -77.0269784)

        createMarker(coordinates)
        enableLocation()

        map.setOnMapClickListener {
            val markerOptions = MarkerOptions().position(it)
            if (currentMarker != null) {
                currentMarker!!.remove()
            }
            currentMarker = map.addMarker(markerOptions.snippet(getAddress(it))
                .draggable(true))
            map.animateCamera(CameraUpdateFactory.newLatLng(it))
        }
    }

    private fun createMarker(latLong: LatLng) {

        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLong, 17f), 3000, null)
        currentMarker?.showInfoWindow()
    }

    private fun getAddress(latLong: LatLng): String? {

        val geoCoder = Geocoder(context, Locale.getDefault())
        val addresses = geoCoder.getFromLocation(latLong.latitude, latLong.longitude, 1)

        mBinding.ietDirection.setText(addresses[0].getAddressLine(0).toString())

        return addresses[0].getAddressLine(0).toString()
    }

    private fun isLocationPermissionGranted() = context?.let {
        ContextCompat.checkSelfPermission(it, Manifest.permission.ACCESS_FINE_LOCATION)
    } == PackageManager.PERMISSION_GRANTED

    @SuppressLint("MissingPermission")
    private fun enableLocation() {
        if (!::map.isInitialized) return
        if (isLocationPermissionGranted()) {
            map.isMyLocationEnabled = true

        } else {
            requestLocationPermission()
        }
    }

    private fun requestLocationPermission() {
        if (activity?.let {
                ActivityCompat.shouldShowRequestPermissionRationale(it,
                    Manifest.permission.ACCESS_FINE_LOCATION)
            } == true) {
            Toast.makeText(activity, "Ve a ajustes y acepta los permisos", Toast.LENGTH_SHORT)
                .show()

        } else {
            activity?.let {
                ActivityCompat.requestPermissions(it,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_CODE_LOCATION)
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        when (requestCode) {
            REQUEST_CODE_LOCATION -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                map.isMyLocationEnabled = true
            } else {
                Toast.makeText(activity,
                    "Para activar la localizaion ve a ajustes y acepta los permisos",
                    Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }

    @SuppressLint("MissingPermission")
    override fun onResume() {
        super.onResume()

        if (!::map.isInitialized) return
        if (!isLocationPermissionGranted()) {
            map.isMyLocationEnabled = false
            Toast.makeText(activity,
                "Para activar la localizaion ve a ajustes y acepta los permisos",
                Toast.LENGTH_SHORT).show()
        }
    }

    private fun getKey(value: Int){

        for (key in districtMap.keys)
        {
            if (value == districtMap[key]) {
                mBinding.spnDistricts.setSelection(spinnerAdapter.getPosition(key))
            }
        }
    }

    private fun getDistricts() {

        CoroutineScope(Dispatchers.IO).launch {

            val call = getRetrofit().create(APIService::class.java).getDistricts("Distritos")
            val listDistrictResponse: List<DistrictResponse> = call.body() ?: emptyList()

            requireActivity().runOnUiThread {
                if (call.isSuccessful) {
                    for (district in listDistrictResponse) {
                        spinnerAdapter.add(district.name)
                        districtMap[district.name] = district.codDistrict
                    }
                }
            }
        }
    }

    private fun updateClient() {

        mBinding.btnUpdate.setOnClickListener {

            CoroutineScope(Dispatchers.IO).launch {

                var phone = mBinding.ietPhone.text?.trim().toString().toInt()
                var email = mBinding.ietEmail.text?.trim().toString()
                var direction = mBinding.ietDirection.text?.trim().toString()

                var districtDto = DistrictDto(codDistrict = district)

                var client = ClientAddInfoDto(
                    email = email,
                    phone = phone,
                    direction = direction,
                    DNI = dniResponse,
                    district = districtDto
                )

                val call = getRetrofit().create(APIService::class.java).putUpdateAddInfoClient(client)
                activity?.runOnUiThread {
                    if (call.isSuccessful) {
                        getGson()

                        Firebase.auth.currentUser?.updateEmail(email)

                        Toast.makeText(activity,"Sus datos han sido actualizados exitosamente...!", Toast.LENGTH_SHORT).show()
                    } else {
                        showError()
                    }
                }
            }
        }
    }

    private fun getClientByEmail() {

        val user = Firebase.auth.currentUser
        var email = user!!.email.toString()

        CoroutineScope(Dispatchers.IO).launch {

            val call = getRetrofit().create(APIService::class.java).getClientByEmail("Cliente/$email")
            val client = call.body()

            activity?.runOnUiThread {
                if (call.isSuccessful) {
                    getGson()
                    if (client != null) {

                        mBinding.ietDirection.setText(client.direction)
                        mBinding.ietPhone.setText(client.phone.toString())

                        districtClient = client.district.codDistrict

                        getKey(client.district.codDistrict)

                        mBinding.ietEmail.setText(client.email)
                        dniResponse = client.DNI

                    } else {
                        Toast.makeText(activity,"Hubo un error al cargar los datos, contacte con Soporte Tecnico, porfavor...", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    showError()
                }
            }
        }
    }
}