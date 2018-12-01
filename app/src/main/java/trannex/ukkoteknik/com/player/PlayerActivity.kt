package trannex.ukkoteknik.com.player

import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.RatingBar
import android.widget.RelativeLayout
import com.github.salomonbrys.kotson.*
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.android.synthetic.main.activity_player_new.*
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import trannex.ukkoteknik.com.R
import trannex.ukkoteknik.com.commons.Footer
import trannex.ukkoteknik.com.entities.FeedbackAndTest
import trannex.ukkoteknik.com.extensions.margins
import trannex.ukkoteknik.com.extensions.replaceFragment
import trannex.ukkoteknik.com.helper.SelectedBatchHandler
import trannex.ukkoteknik.com.player.fragments.AttendanceFragment
import trannex.ukkoteknik.com.player.fragments.TestFragment
import trannex.ukkoteknik.com.player.fragments.VideoFragment
import trannex.ukkoteknik.com.player.fragments.WebFragment
import trannex.ukkoteknik.com.singleton.Constants
import trannex.ukkoteknik.com.singleton.MyApp
import trannex.ukkoteknik.com.utils.DeviceIdUtils
import java.io.File

class PlayerActivity : AppCompatActivity() {
    var index: Int = 0
    lateinit var contentChildren: JsonArray
    lateinit var assetsDir: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player_new)
        setSupportActionBar(toolbar)
        assetsDir = File(filesDir.path + "/assets")

        index = intent.getIntExtra("index", 0)
        contentChildren = JsonParser().parse(intent.getStringExtra("contentChildren")).array

        SelectedBatchHandler.content()


        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, 0, 0)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.scrollView {
            verticalLayout {
                var length = contentChildren.size()
                for ((index, children) in contentChildren.withIndex()) {
                    var content: JsonObject? = null
                    if (!contentChildren[index].obj.has("contentType"))
                        content = SelectedBatchHandler.getContentFromId(JsonParser().parse(contentChildren[index]["asset"].string)["id"].int);
                    else {
                        content = contentChildren[index].obj
                    }
                    if (content != null) {
                        linearLayout {
                            padding = 20
                            textView(content["name"].string) {
                                textColorResource = R.color.black
                            }
                        }.onClick {

                            fun activeFragment() {
                                drawer_layout.closeDrawer(GravityCompat.START)
                                if (this@PlayerActivity.index != index) {
                                    this@PlayerActivity.index = index
                                    activeCurrentType()
                                    title = content["name"].string
                                }
                            }

                            if (content["contentType"].string == Constants.ATTENDANCE) {
                                if (!SelectedBatchHandler.isAttendanceTaken()) {
                                    activeFragment()
                                } else {
                                    toast("Attendance is already taken.")
                                }
                            } else if (SelectedBatchHandler.isAttendanceTaken()) {
                                when (content["contentType"].string) {
                                    Constants.PRE_TEST -> {
                                        if (!SelectedBatchHandler.isPreTestTaken()) {
                                            activeFragment()
                                        } else {
                                            toast("Pre test is already taken.")
                                        }
                                    }
                                    Constants.POST_TEST -> {
                                        if (!SelectedBatchHandler.isPostTestTaken()) {
                                            activeFragment()
                                        } else {
                                            toast("Post test is already taken.")
                                        }
                                    }
                                    Constants.FEEDBACK -> {
                                        if (!SelectedBatchHandler.isFeedbackTaken()) {
                                            activeFragment()
                                        } else {
                                            toast("Feedback is already taken.")
                                        }
                                    }
                                    else -> activeFragment()
                                }
                            } else {
                                toast("Please take attendance to proceed.")
                            }


                        }
                    }
                    if (index != length - 1)
                        view {
                            backgroundColorResource = R.color.grey_text
                        }.lparams(width = MATCH_PARENT, height = 2)
                }
            }.margins(bottom = 50)
        }

        val footer = Footer(top_parent)
        val params = footer.view.layoutParams as RelativeLayout.LayoutParams
        params.apply {
            addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
            width = ViewGroup.LayoutParams.MATCH_PARENT
            height = dip(50)
        }

        footer.view.layoutParams = params
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimaryDark)
        };
        activeCurrentType()
    }

    private fun activeCurrentType() {
        //val content = SelectedBatchHandler.getContentFromId(contentChildren[index]["contentId"].string);
        var content: JsonObject? = null
        if (!contentChildren[index].obj.has("contentType"))
            content = SelectedBatchHandler.getContentFromId(JsonParser().parse(contentChildren[index]["asset"].string)["id"].int);
        else {
            content = contentChildren[index].obj
        }
        if (content != null)
            when (content["contentType"].string) {
                Constants.VIDEO -> {
                    replaceFragment(VideoFragment().apply { data(content) }, R.id.playerView)
                }
                Constants.ATTENDANCE -> {
                    replaceFragment(AttendanceFragment().apply { data(content) }, R.id.playerView)
                }
                Constants.PPT, Constants.INTERACTIVE, Constants.PDF -> {
                    replaceFragment(WebFragment().apply { data(content) }, R.id.playerView)
                }
                Constants.PRE_TEST, Constants.POST_TEST -> {
                    replaceFragment(TestFragment().apply { data(content, content["contentType"].string) }, R.id.playerView)
                }
                Constants.FEEDBACK -> {
                    var ratingBar: RatingBar? = null
                    alert {
                        isCancelable = false
                        customView {
                            verticalLayout {
                                gravity = Gravity.CENTER
                                ratingBar = ratingBar {
                                    numStars = 5
                                    //rating = 3.4f
                                    stepSize = 0.7f
                                }.lparams(width = WRAP_CONTENT)
                            }
                        }
                        positiveButton("Rate") {
                            val feedbackAndTest = MyApp.mDatabaseHelper.getFeedbackAndTestDao()
                            feedbackAndTest?.create(FeedbackAndTest(
                                    trnx_batch_id = SelectedBatchHandler.programData()["batch_id"].int,
                                    trnx_batch_programs_id = SelectedBatchHandler.programData()["id"].int,
                                    data = ratingBar?.rating.toString(),
                                    type = Constants.FEEDBACK,
                                    deviceId = DeviceIdUtils(this@PlayerActivity).androidId,
                                    syncStatus = 0
                            ))
                            onBackPressed()
                        }
                    }.show()
                }
            }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
            overridePendingTransition(R.anim.no_anim, R.anim.bottom_exit)
        }
    }
}
