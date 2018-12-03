package trannex.ukkoteknik.com.home

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
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
import trannex.ukkoteknik.com.extensions.padding
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

    val contentChildren = mutableMapOf<Int, JsonArray>()
    var selectedDay: Int = 0

    val breadCrumbList = mutableListOf("My Programs", "Home")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        topEnterAnimation = AnimationUtils.loadAnimation(this, R.anim.top_enter)
        leftExitAnimation = AnimationUtils.loadAnimation(this, R.anim.left_exit)
        topExitAnimation = AnimationUtils.loadAnimation(this, R.anim.top_exit)
        leftEnterAnimation = AnimationUtils.loadAnimation(this, R.anim.left_enter)

        setContentView(
                verticalLayout {
                    backgroundResource = R.drawable.background
                    appBarLayout {
                        backgroundColor = Color.parseColor("#ffffff")
                        alpha = 0.5f
                        toolbar = toolbar {
                            verticalLayout {
                                //backgroundColor = Color.parseColor("#3F51B5")
                                textView(intent.getStringExtra("title")) {
                                    textSize = 20f
                                    textColor = Color.BLACK
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
                    textColor = Color.BLACK
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
            padding(all = 10)
            programRow(true).margins(bottom = 2).apply {
                alpha = 0.8f
                backgroundColor = R.color.colorPrimary
            }

            val programContentIndexChildren = SelectedBatchHandler.programContents();

            var color = true

            var index = 0
            for (months in programContentIndexChildren) {
                for (day in months["children"].array) {

                    val jsonArray = JsonArray()
                    jsonArray.add(
                            jsonObject(
                                    "name" to Constants.ATTENDANCE,
                                    "contentType" to Constants.ATTENDANCE,
                                    "duration" to "NA"
                            )
                    )

                    jsonArray.add(
                            jsonObject(
                                    "name" to Constants.PRE_TEST,
                                    "contentType" to Constants.PRE_TEST,
                                    "duration" to "NA"
                            )
                    )

                    day["children"].array.forEach { jsonArray.add(it) }

                    jsonArray.add(
                            jsonObject(
                                    "name" to Constants.POST_TEST,
                                    "contentType" to Constants.POST_TEST,
                                    "duration" to "NA"
                            )
                    )

                    jsonArray.add(
                            jsonObject(
                                    "name" to Constants.FEEDBACK,
                                    "contentType" to Constants.FEEDBACK,
                                    "duration" to "NA"
                            )
                    )

                    val id = index

                    programRow(false, day.asJsonObject, activityStatus(jsonArray)).apply {
                        alpha = 0.8f
                        backgroundResource = if (color) R.color.black_opacity else R.color.black
                    }.margins(bottom = 2).onClick {
                        selectedDay = id
                        programClicked(day["name"].string)
                    }
                    contentChildren.put(index++, jsonArray)
                    color = !color
                }
            }
        }
    }

    fun activityStatus(activityChildren: JsonArray): String {
        var isCompleted = true
        for (children in activityChildren) {
            var contentId: Int? = null
            var content: JsonObject = children.obj
            if (children.obj.has("asset")) {
                contentId = JsonParser().parse(children["asset"].string)["id"].int
            }
            isCompleted = isCompleted && (getCompletionStatus(content["contentType"].string, contentId) == "Completed")
        }
        return if (isCompleted) "Completed" else "Not Completed"
    }

    fun attachActivities() {
        activitiesLayout.removeAllViews()

        activitiesLayout.verticalLayout {
            activityRow("Name", "Type", "Duration", "Status").apply {
                alpha = 0.8f
                backgroundColor = R.color.colorPrimary
            }.margins(bottom = 2)
            var color = true

            for ((index, children) in contentChildren[selectedDay]!!.withIndex()) {
                var contentId: Int? = null
                var content: JsonObject = children.obj
                if (children.obj.has("asset")) {
                    contentId = JsonParser().parse(children["asset"].string)["id"].int
                }
                if (content != null) {
                    activityRow(content["name"].string,
                            content["contentType"].string,
                            if (content.has("duration")) content["duration"].string else "NA",
                            getCompletionStatus(content["contentType"].string, contentId)).apply {
                        alpha = 0.8f
                        backgroundResource = if (color) R.color.black_opacity else R.color.black
                        color = !color
                    }.margins(bottom = 2).onClick {
                        fun startPlayer() {
                            startActivity<PlayerActivity>("index" to index, "contentChildren" to contentChildren[selectedDay]!!.toString())
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
            }
        }
    }

/*
    fun getExecutedDate(): String {
        val programData = SelectedBatchHandler.programData()
        val data = MyApp.mDatabaseHelper.getAttendanceDao()
                ?.queryBuilder()
                ?.where()
                ?.eq("trnx_batch_id", programData["batch_id"].int)
                ?.and()
                ?.eq("trnx_batch_programs_id", programData["id"].int)
                ?.queryForFirst()
        return if (data == null) "---" else {
            val dateFormat = SimpleDateFormat(Constants.TIMESTAMP_FORMAT)
            return dateFormat.format(data.created_at).split(" ")[0]
        }
    }
*/


    fun getCompletionStatus(type: String, contentId: Int?): String {
        Log.i("contentId", "" + contentId)
        return when (type) {
            Constants.ATTENDANCE -> if (SelectedBatchHandler.isAttendanceTaken()) "Completed" else "Not Completed"
            Constants.PRE_TEST -> if (SelectedBatchHandler.isPreTestTaken()) "Completed" else "Not Completed"
            Constants.POST_TEST -> if (SelectedBatchHandler.isPostTestTaken()) "Completed" else "Not Completed"
            Constants.FEEDBACK -> if (SelectedBatchHandler.isFeedbackTaken()) "Completed" else "Not Completed"
            else -> if (SelectedBatchHandler.isActivityTaken(contentId!!)) "Completed" else "Not Completed"
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

            Handler().postDelayed({
                attachPrograms()
            }, 500)

            activitiesLayout.visibility = GONE
            activitiesLayout.startAnimation(topExitAnimation)

            currentActive = programLayout
            makeBreadCrumb(1)
        } else {
            super.onBackPressed()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home)
            onBackPressed()
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        Handler().postDelayed({
            if (programLayout.visibility == VISIBLE) {
                attachPrograms()
            } else if (activitiesLayout.visibility == VISIBLE) {
                attachActivities()
            }
        }, 100)
    }
}
