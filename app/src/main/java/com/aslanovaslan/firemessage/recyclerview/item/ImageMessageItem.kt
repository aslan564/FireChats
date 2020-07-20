package com.aslanovaslan.firemessage.recyclerview.item

import android.content.Context
import com.aslanovaslan.firemessage.R
import com.aslanovaslan.firemessage.glide.GlideApp
import com.aslanovaslan.firemessage.model.ImageMessage
import com.aslanovaslan.firemessage.model.Message
import com.aslanovaslan.firemessage.util.StorageUtil
import com.xwray.groupie.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_image_message.*

class ImageMessageItem(val message: ImageMessage, val context: Context):MessageItem(message) {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        super.bind(viewHolder, position)
        GlideApp.with(context)
            .load(StorageUtil.pathToReference(message.imagePath))
            .placeholder(R.drawable.ic_image_black_24dp)
            .into(viewHolder.imageView_message_image)
    }
    override fun getLayout()=R.layout.item_image_message

    override fun isSameAs(other: Item<*>?): Boolean {
        if (other !is ImageMessageItem)
            return false
        if (other.message!=this.message)
            return false
        return true
    }

    override fun equals(other: Any?): Boolean {
        return isSameAs(other as ImageMessageItem)
    }

    override fun hashCode(): Int {
        var result = message.hashCode()
        result = 31 * result + context.hashCode()
        return result
    }


}