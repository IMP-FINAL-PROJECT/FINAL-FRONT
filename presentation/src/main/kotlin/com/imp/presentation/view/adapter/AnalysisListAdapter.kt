package com.imp.presentation.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.imp.presentation.databinding.ItemAnalysisBinding

/**
 * Main - Analysis List Adapter
 */
class AnalysisListAdapter(var context: Context?, val list: ArrayList<String>) : RecyclerView.Adapter<AnalysisListAdapter.CustomViewHolder>() {

    interface SelectItem { fun selectItem(position: Int, type: String) }
    var selectItem: SelectItem? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {

        val binding = ItemAnalysisBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CustomViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {

        if (position < list.size) {
            holder.bindView(context, position, list[position], selectItem, list.size)
        }
    }

    class CustomViewHolder(private val binding: ItemAnalysisBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bindView(context: Context?, position: Int, dao: String, selectItem: SelectItem?, size: Int) {

            context?.let { ctx ->

                with(binding) {

                    // title
                    tvTitle.text = "$position: $dao"
                }
            }
        }
    }
}