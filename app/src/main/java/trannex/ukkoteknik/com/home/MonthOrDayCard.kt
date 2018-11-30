package trannex.ukkoteknik.com.home

import android.app.Activity
import android.support.v7.widget.CardView
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import org.jetbrains.anko.cardview.v7.cardView
import org.jetbrains.anko.dip
import org.jetbrains.anko.textView

/**
 * Created by  Manoj Sadhu on 9/25/2018.
 *
 */

fun ViewGroup.monthOrDayCard(activity: Activity, title: String): CardView {
    return cardView {
        textView(title) {
            gravity = Gravity.CENTER
            textSize = dip(30).toFloat()
        }

    }.apply {
        layoutParams = LinearLayout.LayoutParams(dip(250), dip(300))
    }
}