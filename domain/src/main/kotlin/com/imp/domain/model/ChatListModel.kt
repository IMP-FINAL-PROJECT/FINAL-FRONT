package com.imp.domain.model

/**
 * Main - Chat Model
 */
data class ChatListModel(

    var chatList: ArrayList<Chat> = ArrayList()
) {

    data class Chat(

        var number: String? = null,

        var chat_info: ChatInfo? = null
    )

    data class ChatInfo(

        var name: String? = null,

        var update_at: String? = null,

        val last_chat: String? = null
    )
}