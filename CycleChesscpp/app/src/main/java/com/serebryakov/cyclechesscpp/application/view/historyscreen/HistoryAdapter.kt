package com.serebryakov.cyclechesscpp.application.view.historyscreen

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.serebryakov.cyclechesscpp.R
import com.serebryakov.cyclechesscpp.application.model.data.HistoryData
import com.serebryakov.cyclechesscpp.databinding.ItemHistoryRecyclerviewBinding


class HistoryAdapter(
    private val listener: Listener
) : RecyclerView.Adapter<HistoryAdapter.HistoryDataViewHolder>(), View.OnClickListener {

    private val _history = mutableListOf<HistoryData>()
    val history: List<HistoryData> = _history

    @SuppressLint("NotifyDataSetChanged")
    fun addHistoryData(historyData: List<HistoryData>) {
        _history.addAll(historyData)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setHistoryData(historyData: List<HistoryData>) {
        _history.clear()
        _history.addAll(historyData)
        notifyDataSetChanged()
    }


    override fun onClick(v: View) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryDataViewHolder {
        val binding = ItemHistoryRecyclerviewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HistoryDataViewHolder(binding)
    }


    override fun onBindViewHolder(holder: HistoryDataViewHolder, position: Int) {
        val historyData = _history[position]
        holder.itemView.tag = historyData
        holder.binding.root.setOnClickListener(this)
        val context = holder.itemView.context // Это для того, чтобы получать строковые ресурсы

        with(holder.binding) {
            opponentsUsernameTextview.text = historyData.opponentsUsername
            gameDateTextview.text = historyData.gameDate
            gameResultTextview.text = historyData.gameResult
            if (historyData.gameResult == "W") {
                gameResultTextview.setTextColor(context.getColor(R.color.game_result_win_text_color))
            }
            if (historyData.gameResult == "L") {
                gameResultTextview.setTextColor(context.getColor(R.color.game_result_loose_text_color))
            }
            if (historyData.gameResult == "D") {
                gameResultTextview.setTextColor(context.getColor(R.color.game_result_draw_text_color))
            }
        }
    }


    override fun getItemCount() = _history.size

    class HistoryDataViewHolder(
        val binding: ItemHistoryRecyclerviewBinding,
    ) : RecyclerView.ViewHolder(binding.root)


    interface Listener {
        fun onHistoryElementClick(historyData: HistoryData)
    }
}