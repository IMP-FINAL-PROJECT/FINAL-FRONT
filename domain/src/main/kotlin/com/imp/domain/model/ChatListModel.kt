package com.imp.domain.model

/**
 * Main - Chat Model
 */
data class ChatListModel(

    var chatList: ArrayList<Chat> = ArrayList()
) {

    data class Chat(

        var number: String? = null,

        var create_number: String? = null,

        var update_at: String? = null
    )
}