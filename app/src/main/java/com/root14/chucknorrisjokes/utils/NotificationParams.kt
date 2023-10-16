package com.root14.chucknorrisjokes.utils

import android.content.Context
import com.root14.chucknorrisjokes.R
import java.util.Objects

class NotificationParams(
    var title: String?, var contentText: String?, var smallIcon: Int?, var context: Context?
) {

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
            title = builderTitle,
            contentText = builderContentText,
            smallIcon = builderSmallIcon,
            context = builderContext
        )
    }

}