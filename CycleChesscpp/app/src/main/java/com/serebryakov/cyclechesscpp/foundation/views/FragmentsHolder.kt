package com.serebryakov.cyclechesscpp.foundation.views

import com.serebryakov.cyclechesscpp.foundation.ActivityScopeViewModel

// Базовый класс для активити

interface FragmentsHolder {
    fun getActivityScopeViewModel() : ActivityScopeViewModel
}