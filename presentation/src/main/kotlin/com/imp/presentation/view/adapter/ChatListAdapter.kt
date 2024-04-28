package com.imp.presentation.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.imp.domain.model.ChatListModel
import com.imp.presentation.R
import com.imp.presentation.constants.BaseConstants
import com.imp.presentation.databinding.ItemChattingListBinding
import com.imp.presentation.widget.extension.toVisibleOrGone

/**
 * Main - Chat List Adapter
 */
class ChatListAdapter(var context: Context?, val list: ArrayList<ChatListModel.Chat>) : RecyclerView.Adapter<ChatListAdapter.CustomViewHolder>() {

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

        fun bindView(context: Context?, position: Int, dao: ChatListModel.Chat, selectItem: SelectItem?, size: Int) {

            context?.let { ctx ->

                with(binding) {

                    // x 위치 초기화
                    ctChat.translationX = 0f

                    // 프로필 이미지
                    ivProfile.setBackgroundColor(ContextCompat.getColor(ctx, R.color.color_3377ff))

                    // 이름
                    tvName.text = "이름 $position"

                    // 채팅
                    tvChat.text = "내용 $position"

                    // 시간
                    tvTime.text = dao.update_at

                    // 읽지 않은 알림 여부
                    cvNotification.visibility = View.GONE

                    // 하단 라인 노출 여부
                    bottomLineView.visibility = (position == size - 1).toVisibleOrGone()

                    // 삭제
                    tvDelete.text = ctx.getString(R.string.delete)

                    // 채팅 클릭
                    ctChat.tag = position
                    ctChat.setOnClickListener {

                        val pos = it.tag.toString().toInt()
                        selectItem?.selectItem(pos, BaseConstants.CHAT_CLICK_TYPE_CHATTING)
                    }

                    // 채팅 삭제 클릭
                    clExit.tag = position
                    clExit.setOnClickListener {

                        val pos = it.tag.toString().toInt()
                        selectItem?.selectItem(pos, BaseConstants.CHAT_CLICK_TYPE_EXIT)
                    }
                }
            }
        }
    }
}