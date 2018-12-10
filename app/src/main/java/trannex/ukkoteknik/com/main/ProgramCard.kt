package trannex.ukkoteknik.com.main

import android.app.Activity
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.support.v7.widget.CardView
import android.view.Gravity
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.activity_player_new.view.*
import org.jetbrains.anko.*
import org.jetbrains.anko.cardview.v7.cardView
import org.jetbrains.anko.sdk27.coroutines.onClick
import trannex.ukkoteknik.com.R
import trannex.ukkoteknik.com.extensions.buttonCustom
import trannex.ukkoteknik.com.extensions.margins
import trannex.ukkoteknik.com.helper.SelectedBatchHandler
import trannex.ukkoteknik.com.intro.IntroActivity

/**
 * Created by  Manoj Sadhu on 9/25/2018.
 *
 */
class ProgramCard {
}

fun ViewGroup.programCard(activity: Activity,
                          program: String,
                          batchName: String,
                          location: String,
                          duration: String,
                          index: Int,
                          isContainsAssets: Boolean,
                          click: () -> Unit): CardView {
    return cardView {
        radius = 5f
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            elevation = 5f
        }
        //alpha = 0.8f

        verticalLayout {
            gravity = Gravity.CENTER

            buttonCustom(program).apply {
                textColorResource = R.color.blue
                textSize = dip(25).toFloat()
                width = WRAP_CONTENT
                backgroundResource = R.drawable.circle
                padding = 10
                setTypeface(null, Typeface.BOLD)
                gravity = Gravity.CENTER
            }.lparams(width = dip(110),height = dip(110)).margins(top = 10)
/*
            cardView {
                radius = 50f

            }.lparams(width = 100, height = 100).margins(top = 10, bottom = 5)
*/

            verticalLayout {
                padding = 20
                gravity = Gravity.CENTER
                textView(batchName) {
                    textSize = dip(20).toFloat()
                    width = MATCH_PARENT
                    textColor = Color.BLACK
                    gravity = Gravity.CENTER
                }.margins(bottom = 5)

                linearLayout {
                    padding = 7
                    backgroundColorResource = R.color.black_opacity

                    textView("Location") {
                        textSize = dip(20).toFloat()
                        setTypeface(null, Typeface.BOLD)
                        width = dip(270)
                        textColor = Color.WHITE
                    }
                    textView(location) {
                        textSize = dip(20).toFloat()
                        width = dip(180)
                        textColor = Color.WHITE
                    }
                }

                linearLayout {
                    padding = 7
                    backgroundColorResource = R.color.black
                    textView("Duration") {
                        textSize = dip(20).toFloat()
                        setTypeface(null, Typeface.BOLD)
                        width = dip(270)
                        textColor = Color.WHITE
                    }
                    textView(duration) {
                        textSize = dip(20).toFloat()
                        width = dip(180)
                        textColor = Color.WHITE
                    }
                }

                buttonCustom(if (isContainsAssets) "Execute" else "Download").apply {
                    gravity = Gravity.CENTER
                    this@verticalLayout.gravity = Gravity.END
                    backgroundResource = R.drawable.button
                    textColor = Color.WHITE
                }.margins(top = 10).onClick {
                    if (isContainsAssets) {
                        SelectedBatchHandler.index = index
                        activity.startActivity<IntroActivity>("title" to program)
                    } else {
                        click()
                    }

                }
            }
        }

    }.apply {
        layoutParams = LinearLayout.LayoutParams(dip(450), LinearLayout.LayoutParams.WRAP_CONTENT)
    }
}
