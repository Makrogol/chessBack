package com.serebryakov.cyclechesscpp.application.view.profilescreen.viewutils

import android.view.View
import androidx.core.view.children
import com.serebryakov.cyclechesscpp.application.model.data.UserData
import com.serebryakov.cyclechesscpp.databinding.ProfileScreenFragmentBinding

class ProfileScreenViewUtilsImpl(
    private val binding: ProfileScreenFragmentBinding
): ProfileScreenViewUtils {

    override fun getUserDataFromEditText(): UserData = UserData(
        username = binding.usernameEdittext.text.toString(),
        password = binding.passwordEdittext.text.toString(),
    )

    override fun loadProfileUI() {
        goneAllView()
        setEmptyText()
        visibleProfileUI()
    }

    override fun loadRegisterAuthorizationUI() {
        goneAllView()
        setEmptyText()
        visibleRegisterAuthorizationUI()
    }

    override fun setUsername(username: String) {
        binding.usernameTextview.text = username
    }

    override fun setLoginPasswordInfo(info: String) {
        binding.loginPasswordInfoTextview.text = info
    }

    private fun setEmptyText() {
        with(binding) {
            usernameEdittext.setText("")
            passwordEdittext.setText("")
            loginPasswordInfoTextview.text = ""
            usernameTextview.text = ""
        }
    }

    private fun goneAllView() {
        binding.mainUiLinearLayout.children
            .forEach { it.visibility = View.GONE }
    }

    private fun visibleProfileUI() {
        with(binding) {
            usernameTextview.visibility = View.VISIBLE
            exitButton.visibility = View.VISIBLE
        }
    }

    private fun visibleRegisterAuthorizationUI() {
        with(binding) {
            usernameEdittext.visibility = View.VISIBLE
            passwordEdittext.visibility = View.VISIBLE
            loginPasswordInfoTextview.visibility = View.VISIBLE
            registerButton.visibility = View.VISIBLE
            entryButton.visibility = View.VISIBLE
        }
    }
}
