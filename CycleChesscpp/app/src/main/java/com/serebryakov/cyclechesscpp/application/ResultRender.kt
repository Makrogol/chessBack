package com.serebryakov.cyclechesscpp.application

import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.serebryakov.cyclechesscpp.R
import com.serebryakov.cyclechesscpp.databinding.PartResultBinding
import com.serebryakov.cyclechesscpp.foundation.views.BaseFragment
import com.serebryakov.cyclechesscpp.foundation.model.Result
import java.lang.Exception


fun <T> BaseFragment.renderSimpleResult(
    root: ViewGroup,
    result: Result<T>,
    onError: (e: Exception) -> Unit,
    onSuccess: (T) -> Unit,
    onEmpty: () -> Unit = {},
) {
    val binding = PartResultBinding.bind(root)
    renderResult(
        root = root,
        result = result,
        onPending = {
            binding.progressBar.visibility = View.VISIBLE
        },
        onError = {
            binding.progressBar.visibility = View.GONE
            binding.errorContainer.visibility = View.VISIBLE
            binding.errorTextview.text = it.message
            onError(it)
        },
        onSuccess = { successData ->
            root.alpha = 1.0f
            binding.progressBar.visibility = View.GONE
            binding.errorContainer.visibility = View.GONE
            onSuccess(successData)
        },
        onEmpty = {
            root.alpha = 1.0f
            binding.progressBar.visibility = View.GONE
            binding.errorContainer.visibility = View.GONE
            onEmpty()
        }
    )
}

fun BaseFragment.onTryAgain(root: View, onTryAgainPressed: () -> Unit) {
    root.findViewById<Button>(R.id.try_again_button).setOnClickListener { onTryAgainPressed() }
}
