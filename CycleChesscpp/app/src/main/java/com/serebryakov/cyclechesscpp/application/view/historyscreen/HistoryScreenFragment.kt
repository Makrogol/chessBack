package com.serebryakov.cyclechesscpp.application.view.historyscreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.serebryakov.cyclechesscpp.application.model.data.HistoryData
import com.serebryakov.cyclechesscpp.application.view.findopponentsscreen.OpponentsAdapter
import com.serebryakov.cyclechesscpp.databinding.HistoryScreenFragmentBinding
import com.serebryakov.cyclechesscpp.foundation.views.BaseFragment
import com.serebryakov.cyclechesscpp.foundation.views.BaseScreen
import com.serebryakov.cyclechesscpp.foundation.views.screenViewModel

class HistoryScreenFragment : BaseFragment() {

    class Screen : BaseScreen

    private lateinit var binding: HistoryScreenFragmentBinding
    override val viewModel by screenViewModel<HistoryScreenViewModel>()

    private lateinit var adapter: HistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = HistoryScreenFragmentBinding.inflate(inflater, container, false)

        with(binding) {
            adapter = HistoryAdapter(viewModel)
            binding.historyRecyclerview.adapter = adapter
            adapter.addHistoryData(
                listOf(
                    HistoryData(
                        imageUrl = "123",
                        opponentsUsername = "jks VS abcd",
                        gameDate = "23-05-2025 20:40",
                        gameResult = "W",
                    ),
                    HistoryData(
                        imageUrl = "123",
                        opponentsUsername = "abcd VS jks",
                        gameDate = "22-05-2025 18:51",
                        gameResult = "D",
                    ),
                    HistoryData(
                        imageUrl = "123",
                        opponentsUsername = "konstantin VS abcd",
                        gameDate = "23-05-2025 20:23",
                        gameResult = "L",
                    ),
                )
            )
        }

        return binding.root
    }
}
