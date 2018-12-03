package trannex.ukkoteknik.com.commons

import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import org.jetbrains.anko.*
import trannex.ukkoteknik.com.R

/**
 * Created by  Manoj Sadhu on 10/16/2018.
 *
 */

class Footer(viewGroup: ViewGroup) {
    lateinit var view: LinearLayout

    init {
        create(viewGroup)
    }

    private fun create(viewGroup: ViewGroup): LinearLayout {
        view = viewGroup.linearLayout {
            gravity = Gravity.CENTER
            backgroundColor = Color.parseColor("#ffffff")
            //alpha = 0.5f
            textView("Designed and developed by") {
                textColor = Color.parseColor("#000000")
            }

            imageView {
                setImageResource(R.drawable.ukkoteknik)
            }.lparams(height = MATCH_PARENT) {
                margin = dip(10)
            }
        }
        return view
    }

}

