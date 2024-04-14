package com.imp.presentation.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.imp.domain.model.AddressModel
import com.imp.presentation.databinding.ItemAddressBinding

/**
 * Register - Address List Adapter
 */
class AddressAdapter(var context: Context?, val list: ArrayList<AddressModel.Place>) : RecyclerView.Adapter<AddressAdapter.CustomViewHolder>() {

    interface SelectItem { fun selectItem(position: Int, dao: AddressModel.Place) }
    var selectItem: SelectItem? = null

    fun updateList(newer: ArrayList<AddressModel.Place>?) {

        if (newer.isNullOrEmpty()) return

        list.clear()
        list.addAll(newer)

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {

        val binding = ItemAddressBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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

    class CustomViewHolder(private val binding: ItemAddressBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bindView(context: Context?, position: Int, dao: AddressModel.Place, selectItem: SelectItem?, size: Int) {

            context?.let { ctx ->

                with(binding) {

                    tvAddress.text = dao.address_name

                    // address 클릭
                    tvAddress.tag = position
                    tvAddress.setOnClickListener {

                        selectItem?.selectItem(position, dao)
                    }
                }
            }
        }
    }
}