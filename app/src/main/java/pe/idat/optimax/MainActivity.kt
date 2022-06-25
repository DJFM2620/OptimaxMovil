package pe.idat.optimax

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import pe.idat.optimax.databinding.ActivityMainBinding
import java.util.zip.Inflater

class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        val intent:Intent = intent
        var email = intent.getStringExtra("Email")
        var pass = intent.getStringExtra("Pass")

        mBinding.email.text = email
        mBinding.pass.text = pass
    }
}