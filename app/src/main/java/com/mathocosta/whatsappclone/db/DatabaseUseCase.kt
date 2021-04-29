package com.mathocosta.whatsappclone.db

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.getValue
import com.mathocosta.whatsappclone.auth.AuthRequiredUseCase
import com.mathocosta.whatsappclone.db.model.UserProfile
import kotlinx.coroutines.tasks.await

interface DatabaseUseCase : AuthRequiredUseCase {
    private val databaseRoot
        get() = DatabaseGateway.database

    fun getMessagesRef(): DatabaseReference = databaseRoot.getReference(MESSAGES_DB)

    fun getUsersDbRef(): DatabaseReference = databaseRoot.getReference(USERS_DB)

    fun getChatsRef(): DatabaseReference = databaseRoot.getReference(CHATS_DB)

    fun getGroupsRef(): DatabaseReference = databaseRoot.getReference(GROUPS_DB)

    fun getUserRef(): DatabaseReference? {
        val currentUser = currentUser ?: return null
        return getUsersDbRef().child(currentUser.uid)
    }

    suspend fun getCurrentUserProfile(): UserProfile? = getUserRef()?.let { ref ->
        ref.get().await().getValue<UserProfile>()?.apply {
            uid = ref.key
        }
    }

    companion object {
        const val TAG = "DATABASE"
        private const val USERS_DB = "users"
        private const val MESSAGES_DB = "messages"
        private const val CHATS_DB = "chats"
        private const val GROUPS_DB = "groups"
    }
}