@file:Suppress("DEPRECATION")

package com.aslanovaslan.firemessage.services

import com.aslanovaslan.firemessage.util.FirestoreUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdReceiver
import com.google.firebase.iid.FirebaseInstanceIdService
import com.google.firebase.messaging.FirebaseMessagingService
import java.lang.NullPointerException

class MyFirebaseInstanceIDServices : FirebaseInstanceIdService() {
    override fun onTokenRefresh() {
        val newRegistrationToken = FirebaseInstanceId.getInstance().token
        if (FirebaseAuth.getInstance().currentUser != null) {
            addTokenToFirestore(newRegistrationToken)
        }
    }

    companion object {
        fun addTokenToFirestore(newRegistrationToken: String?) {
            if (newRegistrationToken == null) throw NullPointerException("FCM Token is null")
            FirestoreUtil.getFCMRegistrationToken { tokens ->
                if (tokens.contains(newRegistrationToken)) return@getFCMRegistrationToken
                tokens.add(newRegistrationToken)
                FirestoreUtil.setFCMRegistrationToken(tokens)
            }
        }
    }
}