package com.aslanovaslan.firemessage

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.aslanovaslan.firemessage.services.MyFirebaseInstanceIDServices
import com.aslanovaslan.firemessage.util.FirestoreUtil
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_sign_in.*
import org.jetbrains.anko.*

class SignInActivity : AppCompatActivity() {
    private val  AA_SIGN_IN = 1
    private val singInProviders =
        listOf(
            AuthUI.IdpConfig.EmailBuilder()
                .setAllowNewAccounts(true)
                .setRequireName(true)
                .build()
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        account_sign_in.apply {
            setOnClickListener {
                val intent = AuthUI.getInstance().createSignInIntentBuilder()
                    .setAvailableProviders(singInProviders)
                    .setLogo(R.drawable.ic_fire_emoji)
                    .build()
                startActivityForResult(intent, AA_SIGN_IN)
            }
        }
    }

    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == AA_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {
                val progressDialog = indeterminateProgressDialog("Setting Up your account")
                FirestoreUtil.initCurrentUserIfFirstTime {
                    startActivity(intentFor<MainActivity>().newTask().clearTask())

                    val newRegistrationToken=FirebaseInstanceId.getInstance().token
                    MyFirebaseInstanceIDServices.addTokenToFirestore(newRegistrationToken)
                    progressDialog.dismiss()
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                if (response == null) {
                    return
                }
                when (response.error?.errorCode) {
                    ErrorCodes.NO_NETWORK -> {
                        Snackbar.make(
                            constraint_layout,
                            "This is a simple Snackbar",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                    ErrorCodes.UNKNOWN_ERROR -> {
                        Snackbar.make(constraint_layout, "Unknown erorr", Snackbar.LENGTH_LONG)
                            .show()
                    }
                }
            }
        }
    }
}