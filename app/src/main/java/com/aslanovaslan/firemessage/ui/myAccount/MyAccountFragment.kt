package com.aslanovaslan.firemessage.ui.myAccount

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.aslanovaslan.firemessage.R
import com.aslanovaslan.firemessage.SignInActivity
import com.aslanovaslan.firemessage.glide.GlideApp
import com.aslanovaslan.firemessage.util.FirestoreUtil
import com.aslanovaslan.firemessage.util.StorageUtil
import com.firebase.ui.auth.AuthUI
import kotlinx.android.synthetic.main.fragment_my_account.*
import kotlinx.android.synthetic.main.fragment_my_account.view.*
import java.io.ByteArrayOutputStream
import org.jetbrains.anko.*


class MyAccountFragment : Fragment() {

    private val AA_SELECTED_IMAGE = 2
    private lateinit var selectedImageBytes: ByteArray
    private var pictureJustChanged = false
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_my_account, container, false)
        root.apply {
            imageView_profile_picture.setOnClickListener {
                val intent = Intent().apply {
                    type = "image/*"
                    action = Intent.ACTION_GET_CONTENT
                    putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png"))
                }
                startActivityForResult(
                    Intent.createChooser(intent, "Select Image"),
                    AA_SELECTED_IMAGE
                )
            }
            btn_save.setOnClickListener {
                if (::selectedImageBytes.isInitialized) {
                    StorageUtil.uploadProfilePhoto(selectedImageBytes) { imagePath ->
                        FirestoreUtil.updateCurrentUser(editText_name.text.toString(),
                        editText_bio.text.toString(),imagePath)
                    }
                }else{
                    FirestoreUtil.updateCurrentUser(editText_name.text.toString(),
                        editText_bio.text.toString(),null)
                }
                Toast.makeText(this.context, "Saving", Toast.LENGTH_SHORT).show()
            }
            btn_sign_out.setOnClickListener{
                AuthUI.getInstance()
                    .signOut(this@MyAccountFragment.requireContext())
                    .addOnCompleteListener{
                        startActivity(Intent(this.context, SignInActivity::class.java).newTask().clearTask())
                    }
            }
        }
        return root
    }

    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AA_SELECTED_IMAGE && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val selectedImagePath = data.data
            val selectedImagePathBitmap = MediaStore.Images.Media
                .getBitmap(requireActivity().contentResolver, selectedImagePath)
            val outputStream = ByteArrayOutputStream()
            selectedImagePathBitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            selectedImageBytes = outputStream.toByteArray()

            GlideApp.with(this)
                .load(selectedImageBytes)
                .into(imageView_profile_picture)

            pictureJustChanged = true
        }
    }

    override fun onStart() {
        super.onStart()
        FirestoreUtil.getCurrentUser {user->
            if (this@MyAccountFragment.isVisible) {
                editText_name.setText(user.name)
                editText_bio.setText(user.bio)
                if (!pictureJustChanged && user.profilePicturePath != null) {
                        GlideApp.with(this)
                            .load(StorageUtil.pathToReference(user.profilePicturePath))
                            .placeholder(R.drawable.ic_baseline_accounts_circle_24)
                            .into(imageView_profile_picture)
                }
            }

        }
    }
}