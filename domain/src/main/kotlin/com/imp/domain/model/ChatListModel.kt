package com.imp.domain.model

/**
 * Chat List Model
 */
data class ChatListModel (

    var name: String? = null,

    var chat: String? = null,

    var time: Long = 0L,

    var isRead: Boolean = false
)