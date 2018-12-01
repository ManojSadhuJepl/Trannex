package trannex.ukkoteknik.com.player

import android.graphics.Color
import android.graphics.PorterDuff
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RatingBar
import org.jetbrains.anko.ratingBar
import org.jetbrains.anko.sdk27.coroutines.onRatingBarChange
import org.jetbrains.anko.textColor
import org.jetbrains.anko.textView
import org.jetbrains.anko.verticalLayout
import trannex.ukkoteknik.com.extensions.margins

/**
 * Created by  Manoj Sadhu on 12/1/2018.
 *
 */

fun ViewGroup.question(title: String, rating: Float, ratingChange: (id: Float) -> Unit): Pair<LinearLayout, RatingBar> {
    var ratingBar: RatingBar? = null

    val layout = verticalLayout {
        textView(title) {
            textSize = 20f
            textColor = Color.WHITE
        }
        ratingBar = ratingBar {
            numStars = 5
            stepSize = 0.5f
            setRating(rating)
            progressDrawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)

            onRatingBarChange { ratingBar, rating, fromUser -> ratingChange(rating) }

        }.apply {
            margins(bottom = 5)
            lparams(width = ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }

    return layout to ratingBar!!
}