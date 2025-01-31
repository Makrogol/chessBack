package com.serebryakov.cyclechesscpp.foundation.tools


typealias ResourceAction<T> = (T) -> Unit

//Класс для работы с активити, когда она существует

class ResourceActions<T> {

    var resource: T? = null
        set(value) {
            field = value
            if (value != null) {
                actions.forEach { it(value) }
                actions.clear()
            }
        }

    private val actions = mutableListOf<ResourceAction<T>>()

    operator fun invoke(action: ResourceAction<T>) {
        val resource = this.resource
        if (resource == null) {
            actions += action
        } else {
            action(resource)
        }
    }

    fun clear() {
        actions.clear()
    }
}