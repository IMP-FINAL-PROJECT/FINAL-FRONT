package com.imp.presentation.view.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.imp.domain.model.RecommendModel
import com.imp.presentation.databinding.ItemRecommendListBinding
import com.imp.presentation.widget.utils.MethodStorageUtil

/**
 * Home - Recommend List Adapter
 */
class RecommendListAdapter(var context: Context?, val list: ArrayList<RecommendModel>) : RecyclerView.Adapter<RecommendListAdapter.CustomViewHolder>() {

    interface SelectItem { fun selectItem(position: Int, url: String) }
    var selectItem: SelectItem? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {

        val binding = ItemRecommendListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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

    class CustomViewHolder(private val binding: ItemRecommendListBinding) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bindView(context: Context?, position: Int, dao: RecommendModel, selectItem: SelectItem?, size: Int) {

            context?.let { ctx ->

                with(binding) {

                    // title
                    tvTitle.text = dao.title

                    // description
                    tvDescription.text = dao.description

                    // image
                    MethodStorageUtil.setImageUrl(ivMain, dao.imageUrl ?: "")

                    // Item 클릭
                    ctItem.tag = position
                    ctItem.setOnClickListener {

                        val pos = it.tag.toString().toInt()
                        selectItem?.selectItem(pos, dao.url ?: "")
                    }
                }
            }
        }
    }
}