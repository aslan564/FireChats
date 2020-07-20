package com.aslanovaslan.firemessage.recyclerview.item

import android.content.Context
import com.aslanovaslan.firemessage.R
import com.aslanovaslan.firemessage.glide.GlideApp
import com.aslanovaslan.firemessage.model.User
import com.aslanovaslan.firemessage.util.StorageUtil
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_person.*

class PersonItem(
    val person: User,
    val userId: String,
    private val context: Context
):Item() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.textView_name.text=person.name
        viewHolder.textView_bio.text=person.bio
        if (person.profilePicturePath != null) {
            GlideApp.with(context)
                .load(StorageUtil.pathToReference(person.profilePicturePath))
                .placeholder(R.drawable.ic_baseline_accounts_circle_24)
                .into(viewHolder.imageView_profile_picture)
        }
    }

    override fun getLayout()=R.layout.item_person
}