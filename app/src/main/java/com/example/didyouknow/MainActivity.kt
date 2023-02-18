package com.example.didyouknow

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.example.didyouknow.databinding.ActivityMainBinding
import com.example.didyouknow.ui.viewmodels.HomeFeedViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var binding:ActivityMainBinding? = null
    private lateinit var navController:NavHostFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_DidYouKnow)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        navController = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment

    }


}