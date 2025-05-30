package com.serebryakov.cyclechesscpp.application.view.findopponentsscreen

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.serebryakov.cyclechesscpp.application.model.data.OpponentData
import com.serebryakov.cyclechesscpp.databinding.ItemOpponentsRecyclerviewBinding


class OpponentsAdapter(
    private val listener: Listener,
    private val username: String,
) : RecyclerView.Adapter<OpponentsAdapter.OpponentsDataViewHolder>(), View.OnClickListener {

    private val _opponents = mutableListOf<OpponentData>()
    val opponents: List<OpponentData> = _opponents

    @SuppressLint("NotifyDataSetChanged")
    fun addOpponentData(opponentsData: List<OpponentData>) {
        _opponents.addAll(opponentsData)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setOpponentData(opponentsData: List<OpponentData>) {
        _opponents.clear()
        _opponents.addAll(opponentsData)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun changeOpponentDataByUsername(opponentData: OpponentData) {
        val idx = _opponents.indexOfFirst { _opponentData ->
            _opponentData.username == opponentData.username
        }
        _opponents[idx] = opponentData
        notifyDataSetChanged()
    }

    override fun onClick(v: View) {
        // Возможно потом что-то придумаю с тем, чтобы можно было нажимать на оппонента
//        val opponentData = v.tag as OpponentData
//        listener.onOpponentClick(opponentData)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OpponentsDataViewHolder {
        val binding = ItemOpponentsRecyclerviewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OpponentsDataViewHolder(binding)
    }


    override fun onBindViewHolder(holder: OpponentsDataViewHolder, position: Int) {
        val opponentData = _opponents[position]
        holder.itemView.tag = opponentData
        holder.binding.root.setOnClickListener(this)
        val context = holder.itemView.context // Это для того, чтобы получать строковые ресурсы

        with(holder.binding) {
            if (opponentData.username == username || !opponentData.user_available) {
                startGameButton.visibility = View.GONE
            } else {
                startGameButton.visibility = View.VISIBLE
            }
            opponentNameTextview.text = opponentData.username
            startGameButton.setOnClickListener {
                listener.onOpponentClick(opponentData)
            }
        }
    }


    override fun getItemCount() = _opponents.size

    class OpponentsDataViewHolder(
        val binding: ItemOpponentsRecyclerviewBinding,
    ) : RecyclerView.ViewHolder(binding.root)


    interface Listener {
        fun onOpponentClick(opponentData: OpponentData)
    }
}