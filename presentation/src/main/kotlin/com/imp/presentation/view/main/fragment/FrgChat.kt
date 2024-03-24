package com.imp.presentation.view.main.fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.imp.presentation.R
import com.imp.presentation.base.BaseFragment
import com.imp.presentation.constants.BaseConstants
import com.imp.presentation.databinding.FrgChatBinding
import com.imp.presentation.view.adapter.ChatListAdapter
import com.imp.presentation.view.main.activity.ActMain
import com.imp.presentation.viewmodel.ChatViewModel

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
    }

    override fun onResume() {
        super.onResume()

        // chat list api 호출
        viewModel.chatList()
    }

    /**
     * Initialize Observer
     */
    private fun initObserver() {

        viewModel.chatList.observe(viewLifecycleOwner) { chatList ->

            if (::chattingAdapter.isInitialized) {

                chattingAdapter.list.clear()
                chattingAdapter.list.addAll(chatList)
                chattingAdapter.notifyDataSetChanged()
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
    private fun initRecyclerView() {

        context?.let { ctx ->

            with(mBinding) {

                rvChat.apply {

                    chattingAdapter = ChatListAdapter(ctx, ArrayList())
                    layoutManager = LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false)
                    adapter = chattingAdapter
                    chattingAdapter.apply {

                        selectItem = object : ChatListAdapter.SelectItem {

                            override fun selectItem(position: Int, type: String) {

                            }
                        }
                    }
                }
            }
        }
    }
}