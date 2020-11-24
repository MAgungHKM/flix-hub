package com.hkm.flixhub.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.hkm.flixhub.R
import com.hkm.flixhub.databinding.ActivityMainBinding
import com.hkm.flixhub.utils.OnMyFragmentListener

class MainActivity : AppCompatActivity(), OnMyFragmentListener {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        onChangeToolbarElevation(0f)
    }

    override fun onChangeToolbarTitle(title: String) {
        binding.toolbar.title = title
    }

    override fun onChangeToolbarDisplayHome(display: Boolean) {
        if (display) {
            binding.toolbar.navigationIcon =
                ContextCompat.getDrawable(this, R.drawable.ic_arrow_back_white)
            binding.toolbar.setNavigationOnClickListener { onBackPressed() }
        } else {
            binding.toolbar.navigationIcon = null
            binding.toolbar.setNavigationOnClickListener(null)
        }
    }

    override fun onChangeToolbarElevation(elevation: Float) {
        binding.toolbar.elevation = elevation
    }
}