package trannex.ukkoteknik.com.home

import android.graphics.Color
import android.text.TextUtils
import android.view.ViewGroup
import com.github.salomonbrys.kotson.jsonObject
import com.github.salomonbrys.kotson.string
import com.google.gson.JsonObject
import org.jetbrains.anko.linearLayout
import org.jetbrains.anko.padding
import org.jetbrains.anko.textColor
import org.jetbrains.anko.textView
import trannex.ukkoteknik.com.singleton.Constants

/**
 * Created by  Manoj Sadhu on 9/27/2018.
 *
 */

fun ViewGroup.activityRow(name: String, type: String, duration: String, status: String, exeDate: String, isTitle: Boolean = false): ViewGroup {
    return linearLayout {
        padding = 10
        //backgroundColor = Color.parseColor("#00a7d0")

        weightSum = 8f

        textView(name) {
            textSize = 20f
            textColor = if (isTitle) Color.WHITE else Color.BLACK
            ellipsize = TextUtils.TruncateAt.END
            maxLines = 1
        }.lparams(weight = 3f, width = 0)
        textView(Constants.displayTypes[type]) {
            textSize = 20f
            textColor = if (isTitle) Color.WHITE else Color.BLACK
        }.lparams(weight = 1f, width = 0)
        textView(duration) {
            textSize = 20f
            textColor = if (isTitle) Color.WHITE else Color.BLACK
        }.lparams(weight = 1f, width = 0)
        textView(status) {
            textSize = 20f
            textColor = if (isTitle) Color.WHITE else Color.BLACK
        }.lparams(weight = 1.5f, width = 0)
        textView(exeDate) {
            textSize = 20f
            textColor = if (isTitle) Color.WHITE else Color.BLACK
        }.lparams(weight = 1.5f, width = 0)
    }
}

fun ViewGroup.programRow(isTitle: Boolean, day: JsonObject = jsonObject(), status: String = "Not Completed"): ViewGroup {
    return linearLayout {
        padding = 10
        //backgroundColor = Color.parseColor("#00a7d0")
        weightSum = 3f

        textView(if (isTitle) "Planned Date" else day["name"].string) {
            textSize = 20f
            textColor = if (isTitle) Color.WHITE else Color.BLACK
        }.lparams(weight = 1f, width = 0)

/*
        textView(if (isTitle) "Executed Date" else "---") {
            textSize = 20f
            textColor = Color.BLACK
        }.lparams(weight = 1f, width = 0)
*/
        textView(if (isTitle) "Status" else status) {
            textSize = 20f
            textColor = if (isTitle) Color.WHITE else Color.BLACK
        }.lparams(weight = 1f, width = 0)
        textView(if (isTitle) "Action" else "Execute") {
            textSize = 20f
            textColor = if (isTitle) Color.WHITE else Color.BLACK
        }.lparams(weight = 1f, width = 0)
    }
}
