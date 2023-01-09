package com.example.calculator.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.calculator.R
import kotlinx.android.synthetic.main.item_user.view.*


class HistoryAdapter(private val calculations: List<Calculation>) :
    RecyclerView.Adapter<HistoryAdapter.CalculationViewHolder>() {


    class CalculationViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalculationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return CalculationViewHolder(view)
    }

    override fun onBindViewHolder(holder: CalculationViewHolder, position: Int) {
        val calculation = calculations[position]
        holder.view.inputTextView.text = calculation.input
        holder.view.outputTextView.text = calculation.output
    }

    override fun getItemCount() = calculations.size
}
data class Calculation(val input: String, val output:String)