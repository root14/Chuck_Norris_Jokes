package com.root14.chucknorrisjokes.utils

import android.content.Context
import com.root14.chucknorrisjokes.R
import java.util.Objects

class NotificationParams(title: String?, contentText: String?, smallIcon: Int?) {

    private var title: String? = null
    private var contentText: String? = null
    private var smallIcon: Int? = null
    private var context: Context? = null

    fun getTitle() = title.toString()
    fun getContentText() = contentText.toString()
    fun getSmallIcon() =
        if (smallIcon != null) smallIcon else androidx.core.R.drawable.notification_bg_low

    fun getContext() =
        Objects.requireNonNull(context, "context cannot be null at notification params!")!!

    class Builder {

        private var builderTitle: String? = null
        private var builderContentText: String? = null
        private var builderSmallIcon: Int? = null
        private var builderContext: Context? = null
        fun setTitle(title: String): Builder {
            builderTitle = title
            return this
        }

        fun setContentText(contentText: String): Builder {
            builderContentText = contentText
            return this
        }

        fun setBuilderSmallIcon(smallIcon: Int): Builder {
            builderSmallIcon = smallIcon
            return this
        }

        fun setContext(context: Context): Builder {
            builderContext = context
            return this
        }

        fun build() = NotificationParams(
            title = builderTitle, contentText = builderContentText, smallIcon = builderSmallIcon
        )
    }

}