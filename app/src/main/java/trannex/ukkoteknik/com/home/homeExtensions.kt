package trannex.ukkoteknik.com.home

import android.graphics.Color
import android.text.TextUtils
import android.view.ViewGroup
import com.github.salomonbrys.kotson.jsonObject
import com.github.salomonbrys.kotson.string
import com.google.gson.JsonObject
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.linearLayout
import org.jetbrains.anko.padding
import org.jetbrains.anko.textView

/**
 * Created by  Manoj Sadhu on 9/27/2018.
 *
 */

fun ViewGroup.activityRow(name: String, type: String, duration: String, status: String): ViewGroup {
    return linearLayout {
        padding = 10
        backgroundColor = Color.parseColor("#00a7d0")

        weightSum = 6f

        textView(name) {
            textSize = 20f
            ellipsize = TextUtils.TruncateAt.END
            maxLines = 1
        }.lparams(weight = 3f, width = 0)
        textView(type).lparams(weight = 1f, width = 0).textSize = 20f
        textView(duration).lparams(weight = 1f, width = 0).textSize = 20f
        textView(status).lparams(weight = 1f, width = 0).textSize = 20f
    }
}

fun ViewGroup.programRow(isTitle: Boolean, day: JsonObject = jsonObject()): ViewGroup {
    return linearLayout {
        padding = 10
        backgroundColor = Color.parseColor("#00a7d0")
        weightSum = 4f

        textView(if (isTitle) "Planned Date" else day["name"].string)
                .lparams(weight = 1f, width = 0).textSize = 20f
        textView(if (isTitle) "Executed Date" else "---")
                .lparams(weight = 1f, width = 0).textSize = 20f
        textView(if (isTitle) "Status" else "---")
                .lparams(weight = 1f, width = 0).textSize = 20f
        textView(if (isTitle) "Action" else "Execute")
                .lparams(weight = 1f, width = 0).textSize = 20f
    }
}
