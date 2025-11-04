package ru.practicum.android.diploma.ui.screens

import android.content.Intent
import android.net.Uri
import android.text.Spannable
import android.text.method.LinkMovementMethod
import android.text.style.URLSpan
import android.view.View
import android.widget.TextView

object ContactsClickHandler {

    fun makeLinksClickable(textView: TextView) {
        val text = textView.text
        if (text !is Spannable) return

        val spans = text.getSpans(0, text.length, URLSpan::class.java)

        for (span in spans) {
            val start = text.getSpanStart(span)
            val end = text.getSpanEnd(span)
            val flags = text.getSpanFlags(span)

            text.removeSpan(span)

            val newSpan = object : URLSpan(span.url) {
                override fun onClick(widget: View) {
                    handleClick(widget as TextView, span.url)
                }
            }

            text.setSpan(newSpan, start, end, flags)
        }

        textView.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun handleClick(textView: TextView, url: String) {
        val context = textView.context

        when {
            url.startsWith("mailto:") -> {
                val intent = Intent(Intent.ACTION_SENDTO, Uri.parse(url))
                context.startActivity(Intent.createChooser(intent, "Выберите почтовый клиент"))
            }
            url.startsWith("tel:") -> {
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse(url))
                context.startActivity(intent)
            }
        }
    }
}
