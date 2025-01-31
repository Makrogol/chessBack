package com.serebryakov.cyclechesscpp.foundation.views

import androidx.fragment.app.viewModels
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.serebryakov.cyclechesscpp.foundation.ARG_SCREEN
import com.serebryakov.cyclechesscpp.foundation.BaseApplication
import java.lang.reflect.Constructor


//Фабрика для создания вью моделей, с переменным числом параметров в конструкторе
// (то есть любых на данный момент)

inline fun <reified VM : ViewModel> BaseFragment.screenViewModel() = viewModels<VM> {
    val application = requireActivity().application as BaseApplication
    val screen = requireArguments().getSerializable(ARG_SCREEN) as BaseScreen

    val activityScopeViewModel = (requireActivity() as FragmentsHolder).getActivityScopeViewModel()
    val dependencies = listOf(screen, activityScopeViewModel) + application.singletonScopeDependencies

    ViewModelFactory(dependencies, this)
}



class ViewModelFactory(
    private val dependencies: List<Any>,
    owner: SavedStateRegistryOwner
) : AbstractSavedStateViewModelFactory(owner, null) {


    private fun findDependencies(constructor: Constructor<*>, dependencies: List<Any>): List<Any> {
        val args = mutableListOf<Any>()
        constructor.parameterTypes.forEach { parameterClass ->
            val dependency = dependencies.first { parameterClass.isAssignableFrom(it.javaClass) }
            args.add(dependency)
        }
        return args
    }

    override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        val constructors = modelClass.constructors
        val constructor = constructors.maxByOrNull { it.typeParameters.size }!!
        val dependenciesWithSavedState = dependencies + handle
        val arguments = findDependencies(constructor, dependenciesWithSavedState)
        return constructor.newInstance(*arguments.toTypedArray()) as T
    }

}