package br.com.msaulo.mlover.home.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import br.com.msaulo.mlover.R
import br.com.msaulo.mlover.databinding.ActivityMainBinding
import br.com.msaulo.mlover.home.HomeFragment
import br.com.msaulo.mlover.profile.ProfileFragment


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navigate(HomeFragment())
        binding.bottomNavigation.setItemSelected(R.id.home)

        binding.bottomNavigation.setOnItemSelectedListener {
            when(it) {
                R.id.home -> navigate(HomeFragment())
                R.id.stream -> navigate(ProfileFragment())
                R.id.events -> navigate(ProfileFragment())
                R.id.profile -> navigate(ProfileFragment())
            }
        }
    }

    private fun navigate(fragment: Fragment) {
        val manger = supportFragmentManager
        val transaction = manger.beginTransaction()
        transaction.replace(R.id.frame_layout, fragment)
        transaction.commit()
    }
}
