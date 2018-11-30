package trannex.ukkoteknik.com.login

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import trannex.ukkoteknik.com.R
import trannex.ukkoteknik.com.extensions.buttonCustom
import trannex.ukkoteknik.com.extensions.margins
import trannex.ukkoteknik.com.extensions.padding
import trannex.ukkoteknik.com.main.MainActivity

class LoginActivity : AppCompatActivity() {
    lateinit var rootView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        rootView =
                frameLayout {
                    imageView(R.drawable.login_background) {
                        scaleType = ImageView.ScaleType.FIT_XY
                    }.apply {
                        layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                    }
                    verticalLayout {
                        gravity = Gravity.CENTER

                        padding(all = 10)

                        linearLayout {
                            gravity = Gravity.CENTER_VERTICAL
                            textView("Trannex") {
                                textColor = Color.WHITE
                                textSize = dip(50).toFloat()
                            }
                            view().lparams(weight = 1f, width = 0, height = 0)
                            imageView(R.drawable.hff_logo).lparams(width = 100, height = 100)
                        }.margins(bottom = 15)

                        textView(R.string.userID) {
                            textColorResource = R.color.grey_text
                        }.margins(bottom = 15)

                        editText {
                            backgroundResource = R.drawable.login_input
                            textColor = Color.WHITE
                            hintTextColor = Color.WHITE
                            padding(left = 10)
                        }.margins(bottom = 15)

                        textView(R.string.password) {
                            textColorResource = R.color.grey_text
                        }.margins(bottom = 15)

                        editText {
                            inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
                            backgroundResource = R.drawable.login_input
                            textColor = Color.WHITE
                            padding(left = 10)
                            hintTextColor = Color.WHITE
                        }.margins(bottom = 15)

                        buttonCustom(R.string.login).apply {
                            backgroundResource = R.drawable.login_button
                            textColor = Color.WHITE
                            layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
                        }.onClick {
                            startActivity<MainActivity>()
                        }
                    }.apply {
                        lparams(width = dip(400), height = WRAP_CONTENT, gravity = Gravity.CENTER)
                    }

                }

        // makeFullscreen()

        setContentView(rootView)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        immersiveMode()
        window.decorView.setOnSystemUiVisibilityChangeListener { immersiveMode() }
    }

    fun immersiveMode() {
        //supportActionBar?.hide()
        //val decorView = window.decorView
        rootView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE)
    }

/*
    fun makeFullscreen() {
        supportActionBar?.hide()

        rootView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LOW_PROFILE or
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION

    }
*/


}
