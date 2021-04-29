package com.mathocosta.whatsappclone.ui.chat.usecases

import android.util.Log
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.getValue
import com.mathocosta.whatsappclone.db.DatabaseUseCase
import com.mathocosta.whatsappclone.db.model.Message

class GetChatMessagesUseCase : DatabaseUseCase {
    private var chatRef: DatabaseReference? = null
    private var eventListener: ChildEventListener? = null

    private fun getReferenceFor(request: Request): DatabaseReference =
        getMessagesRef().child(request.senderId).child(request.receiverId)

    operator fun invoke(request: Request, listener: OnMessagesChangeListener) {
        getReferenceFor(request).let { databaseReference ->
            // Save reference for later
            chatRef = databaseReference

            // Listen to changes in the chat child reference
            eventListener = databaseReference.addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    Log.d(
                        DatabaseUseCase.TAG,
                        "onChildAdded: " + snapshot.key + " at key $previousChildName"
                    )

                    snapshot.getValue<Message>()?.let {
                        listener.onNewMessage(it)
                    }
                }

                override fun onChildChanged(
                    snapshot: DataSnapshot,
                    previousChildName: String?
                ) {
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
        }
    }

    fun removeListeners() {
        eventListener?.let {
            chatRef?.removeEventListener(it)
        }
    }

    data class Request(val senderId: String, val receiverId: String)

    interface OnMessagesChangeListener {
        fun onNewMessage(message: Message)
    }
}