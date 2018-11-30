package trannex.ukkoteknik.com.player

import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import android.widget.RelativeLayout
import org.jetbrains.anko.*
import trannex.ukkoteknik.com.R

/**
 * Created by  Manoj Sadhu on 10/16/2018.
 *
 */

class Footer(viewGroup: ViewGroup) {
    lateinit var view: LinearLayout
    //lateinit var playImage: ImageView

    init {
        create(viewGroup)
    }

    private fun create(viewGroup: ViewGroup): LinearLayout {
        view = viewGroup.linearLayout {
            gravity = Gravity.CENTER
            backgroundColor = Color.parseColor("#72279000")


            textView("Designed and developed by") {
                textColor = Color.parseColor("#ffffff")
            }

            imageView {
                setImageResource(R.drawable.ukkoteknik)
            }.lparams(height = MATCH_PARENT) {
                margin = dip(10)
            }

/*            imageView {
                setImageResource(R.drawable.menu)
            }.lparams(width = dip(35), height = dip(35)) {
                marginEnd = dip(15)
            }.onClick {
                callBack?.close()
            }

            imageView {
                setImageResource(R.drawable.previous)
            }.lparams(width = dip(35), height = dip(35)) {
                marginEnd = dip(15)
            }

            playImage = imageView {
                setImageResource(R.drawable.pause)
                onClick {
                    callBack?.play(playImage)
                }
            }.lparams(width = dip(35), height = dip(35)) {
                marginEnd = dip(15)
            }

            imageView {
                setImageResource(R.drawable.video_next)
            }.lparams(width = dip(35), height = dip(35))*/
        }
        return view
    }

/*    public interface CallBack {
        fun close()
        fun play(imageView: ImageView)
        fun next()
        fun previous()
    }*/
}

