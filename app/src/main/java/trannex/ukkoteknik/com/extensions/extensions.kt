package trannex.ukkoteknik.com.extensions

import android.os.AsyncTask
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.CardView
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import org.jetbrains.anko.dip
import trannex.ukkoteknik.com.singleton.Constants
import java.sql.Date
import java.text.SimpleDateFormat

/**
 * Created by  Manoj Sadhu on 9/25/2018.
 *
 */

fun View.margins(left: Int = 0, top: Int = 0, right: Int = 0, bottom: Int = 0): View {
    if (layoutParams is ViewGroup.MarginLayoutParams) {
        val p = layoutParams as ViewGroup.MarginLayoutParams
        p.setMargins(dip(left), dip(top), dip(right), dip(bottom))
        requestLayout()
    }
    return this;
}

fun View.padding(all: Int = 0, left: Int = 0, top: Int = 0, right: Int = 0, bottom: Int = 0): View {
    if (all != 0) {
        setPadding(dip(all), dip(all), dip(all), dip(all))
    } else {
        setPadding(dip(left), dip(top), dip(right), dip(bottom))
    }
    return this;
}

fun ViewGroup.buttonCustom(resource: Any): Button {

    val params = when (this) {
        is LinearLayout ->
            LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        is RelativeLayout ->
            RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        is FrameLayout ->
            FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        is CardView ->
            FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)

        else -> {
            ViewGroup.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        }
    }

    val button = Button(this.context).apply {
        if (resource is Int)
            setText(resource)
        if (resource is String)
            setText(resource)
        layoutParams = params
    }
    addView(button)
    return button
}


fun AppCompatActivity.replaceFragment(fragment: Fragment, frameId: Int) {
    supportFragmentManager.inTransaction { replace(frameId, fragment) }
}

fun Fragment.replaceChildFragment(fragment: Fragment, frameId: Int) {
    childFragmentManager.inTransaction { replace(frameId, fragment) }
}

fun Date.equalString(date: Date): Boolean {
    val dateFormat = SimpleDateFormat(Constants.TIMESTAMP_FORMAT)
    val thisTime = dateFormat.format(this).split(" ")[0]
    val dateTime = dateFormat.format(date).split(" ")[0]

    return thisTime == dateTime
}

inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> FragmentTransaction) {
    beginTransaction().func().commit()
}

class asyncExtension(val backgroundHandler: () -> Unit, val completeHandler: () -> Unit) : AsyncTask<Void, Void, Void>() {
    override fun doInBackground(vararg params: Void?): Void? {
        backgroundHandler()
        return null
    }

    override fun onPostExecute(result: Void?) {
        super.onPostExecute(result)
        completeHandler()
    }
}