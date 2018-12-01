package trannex.ukkoteknik.com.intro

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewTreeObserver
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.TranslateAnimation
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import org.jetbrains.anko.*
import org.jetbrains.anko.cardview.v7.cardView
import org.jetbrains.anko.sdk27.coroutines.onClick
import trannex.ukkoteknik.com.R
import trannex.ukkoteknik.com.extensions.margins
import trannex.ukkoteknik.com.home.HomeActivity

class IntroActivity : AppCompatActivity() {
    lateinit var hff: ImageView
    lateinit var oppertunity: ImageView
    lateinit var ukkoteknok: ImageView
    lateinit var nextImage: ImageView
    lateinit var videoLayout: LinearLayout
    lateinit var rootView: FrameLayout

    lateinit var fadeinAnimation: Animation

    val logger = AnkoLogger<IntroActivity>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fadeinAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in)

        rootView = frameLayout() {
            backgroundResource = R.drawable.background
        }
        setContentView(rootView)

        rootView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                rootView.viewTreeObserver.removeOnGlobalLayoutListener(this);

                val widthRoot = rootView.width
                val heightRoot = rootView.height

                rootView.frameLayout {

                    hff = imageView {
                        setImageResource(R.drawable.hff)
                    }.lparams {
                        width = 300
                        height = 100
                        setMargins(widthRoot / 2 - width - 15, heightRoot / 2 - height, 0, 0)
                    }

                    oppertunity = imageView {
                        setImageResource(R.drawable.oppertunity)
                        backgroundColor = Color.WHITE
                    }.lparams {
                        width = 300
                        height = 100
                        setMargins(0, heightRoot / 2 - height, widthRoot / 2 - width - 15, 0)
                        gravity = Gravity.RIGHT
                    }

                    ukkoteknok = imageView {
                        setImageResource(R.drawable.ukkoteknik)
                        backgroundColor = Color.WHITE
                    }.lparams {
                        width = 300
                        height = 100
                        setMargins(widthRoot / 2 - width / 2, 0, 0, heightRoot / 2 - height)
                        gravity = Gravity.BOTTOM
                    }

                    nextImage = imageView {
                        setImageResource(R.drawable.next)
                        visibility = GONE
                        onClick {
                            startActivity<HomeActivity>("title" to intent.getStringExtra("title"))
                        }
                    }.lparams {
                        setMargins(0, 0, 40, 40)
                        gravity = Gravity.RIGHT or Gravity.BOTTOM
                    }

                    videoLayout = linearLayout {
                        gravity = Gravity.CENTER
                        visibility = GONE
                        alpha = 0.8f
                        cardView {
                            backgroundResource = R.drawable.intro_video_thumbnail1
                            radius = 15f
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                elevation = 8f
                            }
                        }.apply {
                            layoutParams = LinearLayout.LayoutParams(dip(300), dip(200))
                        }

                        cardView {
                            radius = 15f
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                elevation = 8f
                            }
                            backgroundResource = R.drawable.intro_video_thumbnail2

                        }.apply {
                            layoutParams = LinearLayout.LayoutParams(dip(300), dip(200))
                            margins(left = 30)
                        }
                    }
                }
            }
        })


        Handler().postDelayed({
            logger.info("x: ${hff.x}")
            logger.info("y: ${hff.y}")

            val hffAnimation = TranslateAnimation(0f, -1 * (hff.x - 40), 0f, -1 * (hff.y - 40))
            hffAnimation.duration = 500
            hffAnimation.fillAfter = true
            hff.startAnimation(hffAnimation)

            val oppertunityAnimation = TranslateAnimation(0f, hff.x - 40, 0f, -1 * (hff.y - 40))
            oppertunityAnimation.duration = 500
            oppertunityAnimation.fillAfter = true
            oppertunity.startAnimation(oppertunityAnimation)

            val ukkoteknikAnimation = TranslateAnimation(0f, 0f, 0f, hff.y - 40)
            ukkoteknikAnimation.duration = 500
            ukkoteknikAnimation.fillAfter = true
            ukkoteknok.startAnimation(ukkoteknikAnimation)

            ukkoteknikAnimation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationRepeat(p0: Animation?) {
                }

                override fun onAnimationEnd(p0: Animation?) {
                    runOnUiThread {
                        nextImage.visibility = VISIBLE
                        nextImage.startAnimation(fadeinAnimation)
                        videoLayout.visibility = VISIBLE
                        videoLayout.startAnimation(fadeinAnimation)
                    }
                }

                override fun onAnimationStart(p0: Animation?) {
                }
            })

        }, 500)
    }

    override fun onResume() {
        super.onResume()
        makeFullscreen()
    }

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

}
