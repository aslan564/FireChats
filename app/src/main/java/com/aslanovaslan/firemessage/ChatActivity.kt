package com.aslanovaslan.firemessage

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import androidx.recyclerview.widget.LinearLayoutManager
import com.aslanovaslan.firemessage.model.ImageMessage
import com.aslanovaslan.firemessage.model.TextMessage
import com.aslanovaslan.firemessage.model.User
import com.aslanovaslan.firemessage.util.FirestoreUtil
import com.aslanovaslan.firemessage.util.StorageUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.activity_chat.*
import java.io.ByteArrayOutputStream
import java.util.*

const val AA_SELECTED_IMAGE = 2

class ChatActivity : AppCompatActivity() {

    private lateinit var currentChannelId: String
    private lateinit var currentUser: User
    private lateinit var otherUserId: String

    private lateinit var messagesListenerRegistration: ListenerRegistration
    private var shouldInitRecyclerView = true
    private lateinit var messagesSection: Section

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = intent.getStringExtra(AppConstants.USER_NAME)
        FirestoreUtil.getCurrentUser {
            currentUser = it
        }


        otherUserId = intent.getStringExtra(AppConstants.USER_ID)!!
        FirestoreUtil.getOrCreateChatChannels(otherUserId) { channelId ->
            currentChannelId = channelId

            messagesListenerRegistration =
                FirestoreUtil.addChatMessagesListener(channelId, this, this::updateRecyclerView)

            imageView_send.setOnClickListener {
                val messageToSend =
                    TextMessage(
                        editText_message.text.toString(), Calendar.getInstance().time,
                        FirebaseAuth.getInstance().currentUser!!.uid,
                        otherUserId, currentUser.name
                    )
                editText_message.setText("")
                FirestoreUtil.sendMessage(messageToSend, channelId)
            }

            fab_send_image.setOnClickListener {
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
        }
    }

    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == AA_SELECTED_IMAGE && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val selectedImagePath = data.data
            if (selectedImagePath != null) {
                val selectedImageBytes = if (Build.VERSION.SDK_INT >= 28) {
                    val sources = ImageDecoder.createSource(contentResolver, selectedImagePath)
                    val selectedImageBitmap = ImageDecoder.decodeBitmap(sources)
                    val outputStream = ByteArrayOutputStream()
                    selectedImageBitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
                    outputStream.toByteArray()
                } else {
                    val selectedImageBitmap =
                        MediaStore.Images.Media.getBitmap(contentResolver, selectedImagePath)
                    val outputStream = ByteArrayOutputStream()
                    selectedImageBitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
                    outputStream.toByteArray()
                }
                StorageUtil.uploadMessageImage(selectedImageBytes) { imagePath ->
                    val messageToSend = ImageMessage(
                        imagePath, Calendar.getInstance().time,
                        FirebaseAuth.getInstance().currentUser!!.uid,
                        otherUserId, currentUser.name
                    )
                    FirestoreUtil.sendMessage(messageToSend, currentChannelId)
                }
            }

        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun updateRecyclerView(message: List<Item>) {
        fun init() {
            recycler_view_messages.apply {
                layoutManager = LinearLayoutManager(this@ChatActivity)
                adapter = GroupAdapter<ViewHolder>().apply {
                    messagesSection = Section(message)
                    this.add(messagesSection)
                }
            }
            shouldInitRecyclerView = false
        }

        fun updateItems() = messagesSection.update(message)

        if (shouldInitRecyclerView)
            init()
        else
            updateItems()

        recycler_view_messages.scrollToPosition(recycler_view_messages.adapter!!.itemCount - 1)

    }

}