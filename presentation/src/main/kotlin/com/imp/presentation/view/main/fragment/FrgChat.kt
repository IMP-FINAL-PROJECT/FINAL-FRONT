package com.imp.presentation.view.main.fragment

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.imp.presentation.BuildConfig
import com.imp.presentation.R
import com.imp.presentation.base.BaseFragment
import com.imp.presentation.constants.BaseConstants
import com.imp.presentation.databinding.FrgChatBinding
import com.imp.presentation.view.adapter.ChatListAdapter
import com.imp.presentation.view.main.activity.ActMain
import com.imp.presentation.viewmodel.ChatViewModel
import com.imp.presentation.widget.component.ItemTouchHelperCallback
import com.imp.presentation.widget.extension.toDp
import com.imp.presentation.widget.utils.PreferencesUtil

/**
 * Main - Chat Fragment
 */
class FrgChat: BaseFragment<FrgChatBinding>() {

    /** Chat ViewModel */
    private val viewModel: ChatViewModel by activityViewModels()

    /** Chat List Adapter */
    private lateinit var chattingAdapter: ChatListAdapter

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) = FrgChatBinding.inflate(inflater, container, false)

    override fun initData() {

    }

    override fun initView() {

        // set status bar color
        activity?.let { if (it is ActMain) it.setCurrentStatusBarColor(BaseConstants.MAIN_NAV_LABEL_CHAT) }

        initObserver()
        initDisplay()
        initRecyclerView()
        setOnClickListener()
    }

    override fun onResume() {
        super.onResume()

        // chat list api 호출
        context?.let { ctx ->

            val id = PreferencesUtil.getPreferencesString(ctx, PreferencesUtil.AUTO_LOGIN_ID_KEY)
            viewModel.chatList(id)
        }
    }

    /**
     * Initialize Observer
     */
    private fun initObserver() {

        /** Chat List */
        viewModel.chatList.observe(viewLifecycleOwner) { chatList ->

            if (::chattingAdapter.isInitialized) {

                chattingAdapter.list.clear()
                chattingAdapter.list.addAll(chatList)
                chattingAdapter.notifyDataSetChanged()
            }
        }

        /** Chatting Callback */
        viewModel.chatCallback.observe(viewLifecycleOwner) { loadChatting(it) }

        /** Error Callback */
        viewModel.errorCallback.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { error ->

                context?.let { Toast.makeText(it, error.message, Toast.LENGTH_SHORT).show() }
            }
        }
    }

    /**
     * Initialize Display
     */
    private fun initDisplay() {

        with(mBinding) {

            // Header
            incHeader.tvTitle.text = getString(R.string.navigation_chat)
            incHeader.ivAddChat.visibility = View.VISIBLE
        }
    }

    /**
     * Initialize RecyclerView
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun initRecyclerView() {

        context?.let { ctx ->

            with(mBinding) {

                rvChat.apply {

                    // itemTouchHelper 등록 (swipe 삭제)
                    val itemTouchHelperCallback = ItemTouchHelperCallback()
                    itemTouchHelperCallback.setClamp(56.toDp(ctx))

                    val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
                    itemTouchHelper.attachToRecyclerView(this)

                    chattingAdapter = ChatListAdapter(ctx, ArrayList())
                    layoutManager = LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false)
                    adapter = chattingAdapter
                    chattingAdapter.apply {

                        selectItem = object : ChatListAdapter.SelectItem {

                            override fun selectItem(position: Int, type: String) {

                                if (list.size > position) {

                                    when(type) {

                                        // 채팅방 열기
                                        BaseConstants.CHAT_CLICK_TYPE_CHATTING -> loadChatting(list[position].number)

                                        // 채팅방 삭제
                                        BaseConstants.CHAT_CLICK_TYPE_EXIT -> {

                                            val id = PreferencesUtil.getPreferencesString(ctx, PreferencesUtil.AUTO_LOGIN_ID_KEY)
                                            list[position].number?.let {
                                                viewModel.deleteChat(id, it)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    setOnTouchListener { v, event ->
                        itemTouchHelperCallback.removePreviousClamp(this)
                        false
                    }
                }
            }
        }
    }

    /**
     * Set OnClickListener
     */
    private fun setOnClickListener() {

        with(mBinding) {

            context?.let { ctx ->

                // 채팅 생성
                incHeader.ivAddChat.setOnClickListener {

                    if (ctx is ActMain) {

                        ctx.showCommonPopup(
                            titleText = getString(R.string.chat_text_1),
                            leftText = getString(R.string.no),
                            rightText = getString(R.string.yes),
                            rightCallback = {

                                val id = PreferencesUtil.getPreferencesString(ctx, PreferencesUtil.AUTO_LOGIN_ID_KEY)
                                viewModel.createChat(id)
                            },
                            cancelable = true
                        )
                    }
                }
            }
        }
    }

    /**
     * Load Chatting
     */
    private fun loadChatting(number: String?) {

        if (number.isNullOrEmpty()) return

        context?.let { ctx ->

            if (ctx is ActMain) {

                val id = PreferencesUtil.getPreferencesString(ctx, PreferencesUtil.AUTO_LOGIN_ID_KEY)
                val url = BuildConfig.CHATTING_SERVER_HOST + "?id=$id&number=$number"
                ctx.moveToChatting("이름", url)
            }
        }
    }
}