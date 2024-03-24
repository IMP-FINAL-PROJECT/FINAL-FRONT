package com.imp.presentation.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.imp.domain.model.ChatListModel
import com.imp.presentation.R
import com.imp.presentation.databinding.ItemChattingListBinding
import com.imp.presentation.widget.extension.toGoneOrVisible
import com.imp.presentation.widget.extension.toVisibleOrGone
import com.imp.presentation.widget.utils.DateUtil

/**
 * Main - Chat List Adapter
 */
class ChatListAdapter(var context: Context?, val list: ArrayList<ChatListModel>) : RecyclerView.Adapter<ChatListAdapter.CustomViewHolder>() {

    interface SelectItem { fun selectItem(position: Int, type: String) }
    var selectItem: SelectItem? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {

        val binding = ItemChattingListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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

    class CustomViewHolder(private val binding: ItemChattingListBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bindView(context: Context?, position: Int, dao: ChatListModel, selectItem: SelectItem?, size: Int) {

            context?.let { ctx ->

                with(binding) {

                    // 프로필 이미지
                    ivProfile.setBackgroundColor(ContextCompat.getColor(ctx, R.color.color_3377ff))

                    // 이름
                    tvName.text = dao.name

                    // 채팅
                    tvChat.text = dao.chat

                    // 시간
                    tvTime.text = DateUtil.timestampToTimeMin(dao.time)

                    // 읽지 않은 알림 여부
                    cvNotification.visibility = dao.isRead.toGoneOrVisible()

                    // 하단 라인 노출 여부
                    bottomLineView.visibility = (position == size - 1).toVisibleOrGone()
                }
            }
        }
    }
}