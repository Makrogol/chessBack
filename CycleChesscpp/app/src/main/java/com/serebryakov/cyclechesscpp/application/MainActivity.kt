package com.serebryakov.cyclechesscpp.application

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.serebryakov.cyclechesscpp.R
import com.serebryakov.cyclechesscpp.application.view.loginscreen.LoginScreenFragment
import com.serebryakov.cyclechesscpp.databinding.MainActivityBinding
import com.serebryakov.cyclechesscpp.foundation.ActivityScopeViewModel
import com.serebryakov.cyclechesscpp.foundation.navigator.FragmentNavigator
import com.serebryakov.cyclechesscpp.foundation.navigator.IntermediateNavigator
import com.serebryakov.cyclechesscpp.foundation.tools.viewModelCreator
import com.serebryakov.cyclechesscpp.foundation.uiactions.AndroidUiActions
import com.serebryakov.cyclechesscpp.foundation.views.FragmentsHolder


class MainActivity : AppCompatActivity(), FragmentsHolder {

    private lateinit var navigator: FragmentNavigator
    private val binding by lazy { MainActivityBinding.inflate(layoutInflater) }

    private val activityViewModel by viewModelCreator<ActivityScopeViewModel> {
        ActivityScopeViewModel(
            uiActions = AndroidUiActions(applicationContext),
            navigator = IntermediateNavigator()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(binding.root)

        navigator = FragmentNavigator(
            activity = this,
            containerId = R.id.container,
            initialScreenCreator = { LoginScreenFragment.Screen() }
        )

        navigator.onCreate(savedInstanceState)
    }

    override fun onDestroy() {
        navigator.onDestroy()
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        activityViewModel.navigator.setTarget(navigator)
    }

    override fun onPause() {
        super.onPause()
        activityViewModel.navigator.setTarget(null)
    }


    override fun getActivityScopeViewModel(): ActivityScopeViewModel {
        return activityViewModel
    }

    override fun onBackPressed() {
        navigator.onBackPressed()
        super.onBackPressed()
    }

    companion object {
        init {
            System.loadLibrary("cyclechesscpp")
        }
    }
}