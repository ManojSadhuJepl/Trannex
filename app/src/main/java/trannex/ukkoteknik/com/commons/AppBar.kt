package trannex.ukkoteknik.com.commons

import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.linearLayout
import org.jetbrains.anko.textColor
import org.jetbrains.anko.textView

/**
 * Created by  Manoj Sadhu on 10/16/2018.
 *
 */

class AppBar(viewGroup: ViewGroup) {
    lateinit var view: LinearLayout

    init {
        create(viewGroup)
    }

    private fun create(viewGroup: ViewGroup): LinearLayout {
        view = viewGroup.linearLayout {
            gravity = Gravity.CENTER
            backgroundColor = Color.parseColor("#ffffff")
            alpha = 0.5f
            textView("Trannex") {
                textColor = Color.parseColor("#000000")
                textSize = 40f
            }

        }
        return view
    }

}

