package com.serebryakov.cyclechesscpp.foundation.navigator

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.serebryakov.cyclechesscpp.foundation.ARG_SCREEN
import com.serebryakov.cyclechesscpp.foundation.tools.Event
import com.serebryakov.cyclechesscpp.foundation.views.BaseFragment
import com.serebryakov.cyclechesscpp.foundation.views.BaseScreen


//Навигатор для работы на стороне активити и фрагментов

typealias InitialScreenCreator = () -> BaseScreen


class FragmentNavigator(
    private val activity: AppCompatActivity,
    private val containerId: Int,
    private val initialScreenCreator: InitialScreenCreator
) : Navigator {

    private var result: Event<Any>? = null

    override fun launch(screen: BaseScreen) {
        launchFragment(screen)
    }

    fun onCreate(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            launchFragment(
                screen = initialScreenCreator(),
                addToBackStack = false
            )
        }

        activity.supportFragmentManager.registerFragmentLifecycleCallbacks(fragmentCallbacks, false)
    }

    fun onDestroy() {
        activity.supportFragmentManager.unregisterFragmentLifecycleCallbacks(fragmentCallbacks)
    }

    fun onBackPressed() {
        val f = getCurrentFragment()
        if (f is BaseFragment) {
            f.viewModel.onBackPressed()
        }
    }

    private fun launchFragment(screen: BaseScreen, addToBackStack: Boolean = false) {
        val fragment = screen.javaClass.enclosingClass.newInstance() as Fragment
        fragment.arguments = bundleOf(ARG_SCREEN to screen)
        val transaction = activity.supportFragmentManager.beginTransaction()
        if (addToBackStack) transaction.addToBackStack(null)
        transaction
            .replace(containerId, fragment)
            .commit()
    }

    private fun getCurrentFragment(): Fragment? {
        return activity.supportFragmentManager.findFragmentById(containerId)
    }

    fun publishResults(fragment: Fragment) {
        val result = result?.getValue() ?: return
        if (fragment is BaseFragment) {
            fragment.viewModel.onResult(result)
        }
    }



    private val fragmentCallbacks = object : FragmentManager.FragmentLifecycleCallbacks() {
        override fun onFragmentViewCreated(
            fm: FragmentManager,
            f: Fragment,
            v: View,
            savedInstanceState: Bundle?
        ) {
            publishResults(f)
        }
    }
}