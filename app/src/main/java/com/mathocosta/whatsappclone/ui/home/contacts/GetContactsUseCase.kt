package com.mathocosta.whatsappclone.ui.home.contacts

import com.google.firebase.database.ktx.getValue
import com.mathocosta.whatsappclone.db.DatabaseUseCase
import com.mathocosta.whatsappclone.db.model.UserProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class GetContactsUseCase : DatabaseUseCase {
    suspend operator fun invoke(): List<UserProfile> = withContext(Dispatchers.IO) {
        val dataSnapshot = getUsersDbRef().get().await()

        dataSnapshot.children.mapNotNull {
            val contact = it.getValue<UserProfile>()
            contact?.uid = it.key
            contact
        }
    }
}