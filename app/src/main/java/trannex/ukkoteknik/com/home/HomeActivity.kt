package trannex.ukkoteknik.com.home

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.ScrollView
import com.github.salomonbrys.kotson.*
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.toolbar
import org.jetbrains.anko.design.appBarLayout
import org.jetbrains.anko.sdk27.coroutines.onClick
import trannex.ukkoteknik.com.R
import trannex.ukkoteknik.com.extensions.margins
import trannex.ukkoteknik.com.helper.SelectedBatchHandler
import trannex.ukkoteknik.com.main.MainActivity
import trannex.ukkoteknik.com.player.PlayerActivity
import trannex.ukkoteknik.com.singleton.Constants


class HomeActivity : AppCompatActivity() {

    lateinit var programLayout: ScrollView;
    lateinit var activitiesLayout: ScrollView;
    lateinit var toolbar: Toolbar
    lateinit var breadCrumb: LinearLayout
    lateinit var currentActive: View
    lateinit var scrollView: HorizontalScrollView

    lateinit var topEnterAnimation: Animation
    lateinit var leftExitAnimation: Animation
    lateinit var topExitAnimation: Animation
    lateinit var leftEnterAnimation: Animation

    lateinit var contentChildren: JsonArray


    val breadCrumbList = mutableListOf("My Programs", "Home")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        topEnterAnimation = AnimationUtils.loadAnimation(this, R.anim.top_enter)
        leftExitAnimation = AnimationUtils.loadAnimation(this, R.anim.left_exit)
        topExitAnimation = AnimationUtils.loadAnimation(this, R.anim.top_exit)
        leftEnterAnimation = AnimationUtils.loadAnimation(this, R.anim.left_enter)

        setContentView(
                verticalLayout {
                    appBarLayout {
                        toolbar = toolbar {
                            verticalLayout {
                                backgroundColor = Color.parseColor("#3F51B5")
                                textView(intent.getStringExtra("title")) {
                                    textSize = 20f
                                    textColor = Color.parseColor("#ffffff")
                                    padding = 3
                                }

                                breadCrumb = linearLayout {
                                    padding = 3
                                }
                            }
                        }
                    }
                    scrollView = horizontalScrollView {
                        isFillViewport = true
                        linearLayout {
                            //gravity = Gravity.CENTER_VERTICAL
                            programLayout = scrollView {
                            }.lparams {
                                width = MATCH_PARENT
                                height = MATCH_PARENT
                            }

                            activitiesLayout = scrollView {
                                visibility = GONE

                            }.lparams {
                                width = MATCH_PARENT
                                height = MATCH_PARENT
                            }
                        }
                    }.apply {
                        layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)

                    }
                })

        currentActive = programLayout

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = ""

        makeBreadCrumb(0)
    }

    private fun makeBreadCrumb(drop: Int) {
        breadCrumb.removeAllViews()
        breadCrumbList.subList(breadCrumbList.size - drop, breadCrumbList.size).clear()


        breadCrumb.linearLayout {
            for ((index, name) in breadCrumbList.withIndex()) {
                textView("  $name  /") {
                    textSize = 20f
                    textColor = Color.parseColor("#ffffff")
                }.onClick {
                    when (index) {
                        0 -> {
                            startActivity<MainActivity>()
                            finishAffinity()
                        }
                        1 -> {
                            if (currentActive != programLayout) {
                                programLayout.visibility = VISIBLE
                                programLayout.startAnimation(leftEnterAnimation)
                                currentActive.visibility = GONE
                                currentActive.startAnimation(topExitAnimation)

                                currentActive = programLayout

                                makeBreadCrumb(1)
                                //resetScroll()
                            }
                        }
                    }
                }
            }
        }
    }

    fun attachPrograms() {
        programLayout.removeAllViews()
        programLayout.verticalLayout {
            programRow(true).margins(bottom = 2)

            val programContentIndexChildren = SelectedBatchHandler.programContents();

            for (months in programContentIndexChildren) {
                for (day in months["children"].array) {
                    programRow(false, day.asJsonObject).margins(bottom = 2).onClick {
                        contentChildren = day["children"].array
                        programClicked(day["name"].string)
                    }
                }
            }
        }
    }

    fun attachActivities() {
        activitiesLayout.removeAllViews()

        activitiesLayout.verticalLayout {
            activityRow("Name", "Type", "Duration", "Status").margins(bottom = 2)

            val customChildren = jsonArray()

            customChildren.add(
                    jsonObject(
                            "name" to Constants.ATTENDANCE,
                            "contentType" to Constants.ATTENDANCE,
                            "duration" to "NA"
                    )
            )

            customChildren.add(
                    jsonObject(
                            "name" to Constants.PRE_TEST,
                            "contentType" to Constants.PRE_TEST,
                            "duration" to "NA"
                    )
            )

            contentChildren.forEach { customChildren.add(it) }
            //customChildren.add(contentChildren)

            customChildren.add(
                    jsonObject(
                            "name" to Constants.POST_TEST,
                            "contentType" to Constants.POST_TEST,
                            "duration" to "NA"
                    )
            )

            customChildren.add(
                    jsonObject(
                            "name" to Constants.FEEDBACK,
                            "contentType" to Constants.FEEDBACK,
                            "duration" to "NA"
                    )
            )

            for ((index, children) in customChildren.withIndex()) {

                var content: JsonObject? = null
                if (!children.obj.has("contentType"))
                    content = SelectedBatchHandler.getContentFromId(JsonParser().parse(children["asset"].string)["id"].int);
                else {
                    content = children.obj
                }

                //val content = SelectedBatchHandler.getContentFromId(JsonParser().parse(children["asset"].string)["id"].int)
                if (content != null)
                    activityRow(content["name"].string,
                            content["contentType"].string,
                            if (content.has("duration")) content["duration"].string else "NA",
                            "Completed").margins(bottom = 2).onClick {

                        fun startPlayer() {
                            startActivity<PlayerActivity>("index" to index, "contentChildren" to customChildren.toString())
                            overridePendingTransition(R.anim.bottom_enter, R.anim.no_anim)
                        }

                        if (content["contentType"].string == Constants.ATTENDANCE) {
                            if (!SelectedBatchHandler.isAttendanceTaken()) {
                                startPlayer()
                            } else {
                                toast("Attendance is already taken.")
                            }
                        } else if (SelectedBatchHandler.isAttendanceTaken()) {
                            when (content["contentType"].string) {
                                Constants.PRE_TEST -> {
                                    if (!SelectedBatchHandler.isPreTestTaken()) {
                                        startPlayer()
                                    } else {
                                        toast("Pre test is already taken.")
                                    }
                                }
                                Constants.POST_TEST -> {
                                    if (!SelectedBatchHandler.isPostTestTaken()) {
                                        startPlayer()
                                    } else {
                                        toast("Post test is already taken.")
                                    }
                                }
                                Constants.FEEDBACK -> {
                                    if (!SelectedBatchHandler.isFeedbackTaken()) {
                                        startPlayer()
                                    } else {
                                        toast("Feedback is already taken.")
                                    }
                                }
                                else -> startPlayer()
                            }
                        } else {
                            toast("Please take attendance to proceed.")
                        }
                    }
            }

            /*activityRow("Intro ", Constants.ATTENDANCE, "--", "Completed").margins(bottom = 2).onClick {
                startActivity<PlayerActivity>("type" to Constants.ATTENDANCE)
                overridePendingTransition(R.anim.bottom_enter, R.anim.no_anim)
            }
            activityRow("Intro ", Constants.PPT, "--", "Completed").margins(bottom = 2).onClick {
                startActivity<PlayerActivity>("type" to Constants.PPT)
                overridePendingTransition(R.anim.bottom_enter, R.anim.no_anim)
            }
            activityRow("Intro ", Constants.PDF, "--", "Completed").margins(bottom = 2).onClick {
                startActivity<PlayerActivity>("type" to Constants.PDF)
                overridePendingTransition(R.anim.bottom_enter, R.anim.no_anim)
            }
            activityRow("Pre test", Constants.INTERACTIVE, "Duration", "Pending").margins(bottom = 2).onClick {
                startActivity<PlayerActivity>("type" to Constants.INTERACTIVE)
                overridePendingTransition(R.anim.bottom_enter, R.anim.no_anim)
            }
            activityRow("health", Constants.VIDEO, "Duration", "Completed").margins(bottom = 2).onClick {
                startActivity<PlayerActivity>("type" to Constants.VIDEO)
                overridePendingTransition(R.anim.bottom_enter, R.anim.no_anim)
            }
            activityRow("health", Constants.FEEDBACK, "Duration", "Completed").margins(bottom = 2).onClick {
                startActivity<PlayerActivity>("type" to Constants.FEEDBACK)
                overridePendingTransition(R.anim.bottom_enter, R.anim.no_anim)
            }*/
        }
    }

    fun programClicked(dayInfo: String) {
        programLayout.visibility = GONE
        programLayout.startAnimation(leftExitAnimation)

        activitiesLayout.visibility = VISIBLE

        attachActivities()

        activitiesLayout.startAnimation(topEnterAnimation)

        currentActive = activitiesLayout

        breadCrumbList.add(dayInfo)
        makeBreadCrumb(0)
    }


    override fun onBackPressed() {
        if (currentActive == activitiesLayout) {
            programLayout.visibility = VISIBLE
            programLayout.startAnimation(leftEnterAnimation)
            activitiesLayout.visibility = GONE
            activitiesLayout.startAnimation(topExitAnimation)

            currentActive = programLayout
            makeBreadCrumb(1)
            //resetScroll()
        } else {
            super.onBackPressed()
        }
    }

    /*private fun resetScroll() {
        scrollView.postDelayed({
            scrollView.arrowScroll(HorizontalScrollView.FOCUS_LEFT)
        }, 300)
    }*/

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home)
            onBackPressed()
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        attachPrograms()
    }

}
