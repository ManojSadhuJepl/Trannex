package trannex.ukkoteknik.com.home

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
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
import android.widget.TextView
import com.github.salomonbrys.kotson.*
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.toolbar
import org.jetbrains.anko.design.appBarLayout
import org.jetbrains.anko.sdk27.coroutines.onClick
import trannex.ukkoteknik.com.R
import trannex.ukkoteknik.com.entities.VideoAndInteractive
import trannex.ukkoteknik.com.extensions.padding
import trannex.ukkoteknik.com.helper.SelectedBatchHandler
import trannex.ukkoteknik.com.main.MainActivity
import trannex.ukkoteknik.com.player.PlayerActivity
import trannex.ukkoteknik.com.singleton.Constants
import trannex.ukkoteknik.com.singleton.MyApp
import trannex.ukkoteknik.com.utils.DeviceIdUtils
import java.sql.Timestamp


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

    val breadCrumbList = mutableListOf("Main", "Home")

    val startTime: Timestamp = MyApp.getCurrentTimeStamp()


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
                        //alpha = 0.5f
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


        val layout = breadCrumb.linearLayout {
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
        val textView: TextView = layout.getChildAt(layout.childCount - 1) as TextView
        val text = textView.text.toString().replace("/", "")
        textView.text = text
        textView.textColorResource = R.color.blue
    }

    fun attachPrograms() {
        programLayout.removeAllViews()
        programLayout.verticalLayout {
            padding(all = 10)
            programRow(true).apply {
                //alpha = 0.8f
                backgroundColor = R.color.black_overlay
            }

            val programContentIndexChildren = SelectedBatchHandler.programContents();

            var color = true

            var index = 0
            for (months in programContentIndexChildren) {
                for (day in months["children"].array) {

                    val jsonArray = JsonArray()
                    jsonArray.add(
                            jsonObject(
                                    "name" to "Attendance",
                                    "contentType" to Constants.ATTENDANCE,
                                    "duration" to "NA"
                            )
                    )

                    jsonArray.add(
                            jsonObject(
                                    "name" to "Pre Test",
                                    "contentType" to Constants.PRE_TEST,
                                    "duration" to "NA"
                            )
                    )

                    /*val clonedDayChildren = day["children"].array.deepCopy();
                    clonedDayChildren += jsonObject(
                            "name" to "Pre Test",
                            "contentType" to Constants.PRE_TEST,
                            "duration" to "NA"
                    )*/


                    day["children"].array.forEach { jsonArray.add(it) }

                    jsonArray.add(
                            jsonObject(
                                    "name" to "Post Test",
                                    "contentType" to Constants.POST_TEST,
                                    "duration" to "NA"
                            )
                    )

                    jsonArray.add(
                            jsonObject(
                                    "name" to "Feedback",
                                    "contentType" to Constants.FEEDBACK,
                                    "duration" to "NA"
                            )
                    )

                    val id = index

                    programRow(false, day.asJsonObject, activityStatus(jsonArray)).apply {
                        //alpha = 0.8f
                        backgroundResource = if (color) R.color.secondRow else R.color.white
                    }.onClick {
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
            activityRow("Name", "Type", "Duration", "Status", "Executed date", true).apply {
                //alpha = 0.8f
                backgroundColor = R.color.black_overlay
            }
            var color = true

            for ((index, children) in contentChildren[selectedDay]!!.withIndex()) {
                var contentId: Int? = null
                var content: JsonObject = children.obj
                if (children.obj.has("asset")) {
                    contentId = JsonParser().parse(children["asset"].string)["id"].int
                }
                if (content != null) {

                    val pair = getCompletionData(content["contentType"].string, contentId)

                    activityRow(content["name"].string,
                            content["contentType"].string,
                            if (content.has("duration")) content["duration"].string else "NA",
                            if (pair.first) "Completed" else "Not completed", pair.second).apply {
                        //alpha = 0.8f
                        backgroundResource = if (color) R.color.secondRow else R.color.white
                        color = !color
                    }.onClick {
                        fun startPlayer() {
                            startActivity<PlayerActivity>("index" to index, "contentChildren" to contentChildren[selectedDay]!!.toString())
                            overridePendingTransition(R.anim.bottom_enter, R.anim.no_anim)
                        }

                        if (content["contentType"].string == Constants.ATTENDANCE) {
                            if (!SelectedBatchHandler.isAttendanceTaken()) {
                                startPlayer()
                            } else {
                                alert("Attendance is already taken.") {
                                    yesButton { }
                                }.show()
                            }
                        } else if (SelectedBatchHandler.isAttendanceTaken()) {
                            when (content["contentType"].string) {
                                Constants.PRE_TEST -> {
                                    if (!SelectedBatchHandler.isPreTestTaken()) {
                                        startPlayer()
                                    } else {
                                        alert("Pre test is already taken.") {
                                            yesButton { }
                                        }.show()
                                    }
                                }
                                Constants.POST_TEST -> {
                                    if (!SelectedBatchHandler.isPreTestTaken()) {
                                        alert("Pre test is required.") {
                                            yesButton { }
                                        }.show()
                                        return@onClick
                                    }
                                    if (!SelectedBatchHandler.isPostTestTaken()) {
                                        startPlayer()
                                    } else {
                                        alert("Post test is already taken.") {
                                            yesButton { }
                                        }.show()
                                    }
                                }
                                Constants.FEEDBACK -> {
                                    if (!SelectedBatchHandler.isFeedbackTaken()) {
                                        startPlayer()
                                    } else {
                                        alert("Feedback is already taken.") {
                                            yesButton { }
                                        }.show()
                                    }
                                }
                                else -> startPlayer()
                            }
                        } else {
                            alert("Please take attendance to proceed.") {
                                yesButton { }
                            }.show()
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
        //Log.i("contentId", "" + contentId)
        return when (type) {
            Constants.ATTENDANCE -> if (SelectedBatchHandler.isAttendanceTaken()) "Completed" else "Not Completed"
            Constants.PRE_TEST -> if (SelectedBatchHandler.isPreTestTaken()) "Completed" else "Not Completed"
            Constants.POST_TEST -> if (SelectedBatchHandler.isPostTestTaken()) "Completed" else "Not Completed"
            Constants.FEEDBACK -> if (SelectedBatchHandler.isFeedbackTaken()) "Completed" else "Not Completed"
            else -> if (SelectedBatchHandler.isActivityTaken(contentId!!)) "Completed" else "Not Completed"
        }
    }

    fun getCompletionData(type: String, contentId: Int?): Pair<Boolean, String> {
        //Log.i("contentId", "" + contentId)
        return when (type) {
            Constants.ATTENDANCE -> SelectedBatchHandler.attendanceData()
            Constants.PRE_TEST -> SelectedBatchHandler.preTestData()
            Constants.POST_TEST -> SelectedBatchHandler.postTestData()
            Constants.FEEDBACK -> SelectedBatchHandler.feedbackData()
            else -> SelectedBatchHandler.activityData(contentId!!)
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
        }, 1000)
    }

    override fun onStop() {
        super.onStop()

        MyApp.mDatabaseHelper.getVideoAndInteractiveDao()?.create(VideoAndInteractive(
                trnx_batch_id = SelectedBatchHandler.programData()["batch_id"].int,
                trnx_batch_programs_id = SelectedBatchHandler.programData()["id"].int,
                start_time = startTime,
                trnx_content_id = null,
                end_time = MyApp.getCurrentTimeStamp(),
                deviceId = DeviceIdUtils(this).androidId,
                syncStatus = 0
        ))
/*
        MyApp.mDatabaseHelper.getBatchExecutionDao()
                ?.create(BatchExecution(batchId = SelectedBatchHandler.batch()["id"].int,
                        timeSpent = time.toString()))
*/

    }
}
