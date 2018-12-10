package trannex.ukkoteknik.com.player

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
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
import trannex.ukkoteknik.com.extensions.margins
import trannex.ukkoteknik.com.extensions.replaceFragment
import trannex.ukkoteknik.com.helper.SelectedBatchHandler
import trannex.ukkoteknik.com.player.fragments.*
import trannex.ukkoteknik.com.singleton.Constants
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

    fun createNavBar() {
        nav_view.removeAllViews()
        nav_view.scrollView {
            verticalLayout {
                var length = contentChildren.size()
                for ((index, children) in contentChildren.withIndex()) {
                    var contentId: Int? = null
                    var content: JsonObject = children.obj
                    if (children.obj.has("asset")) {
                        contentId = JsonParser().parse(children["asset"].string)["id"].int
                        //content = SelectedBatchHandler.getContentFromId(contentId)
                    }
                    if (content != null) {
                        linearLayout {
                            padding = 20
                            textView(content["name"].string) {
                                textColorResource = R.color.black

                                if (getCompletionStatus(content["contentType"].string, contentId)) {
                                    setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.tick, 0)
                                    compoundDrawablePadding = 10
                                }
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
                                    alert("Attendance is already taken.") {
                                        yesButton { }
                                    }.show()
                                }
                            } else if (SelectedBatchHandler.isAttendanceTaken()) {
                                when (content["contentType"].string) {
                                    Constants.PRE_TEST -> {
                                        if (!SelectedBatchHandler.isPreTestTaken()) {
                                            activeFragment()
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
                                            activeFragment()
                                        } else {
                                            alert("Post test is already taken.") {
                                                yesButton { }
                                            }.show()
                                        }
                                    }
                                    Constants.FEEDBACK -> {
                                        if (!SelectedBatchHandler.isFeedbackTaken()) {
                                            activeFragment()
                                        } else {
                                            alert("Feedback is already taken.") {
                                                yesButton { }
                                            }.show()
                                        }
                                    }
                                    else -> activeFragment()
                                }
                            } else {
                                alert("Please take attendance to proceed.") {
                                    yesButton { }
                                }.show()
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
    }

    private fun getCompletionStatus(type: String, contentId: Int?): Boolean {
        Log.i("contentId", "" + contentId)
        return when (type) {
            Constants.ATTENDANCE -> SelectedBatchHandler.isAttendanceTaken()
            Constants.PRE_TEST -> SelectedBatchHandler.isPreTestTaken()
            Constants.POST_TEST -> SelectedBatchHandler.isPostTestTaken()
            Constants.FEEDBACK -> SelectedBatchHandler.isFeedbackTaken()
            else -> SelectedBatchHandler.isActivityTaken(contentId!!)
        }
    }

    private fun activeCurrentType() {
        var content: JsonObject? = null
        if (!contentChildren[index].obj.has("contentType"))
            content = SelectedBatchHandler.getContentFromId(JsonParser().parse(contentChildren[index]["asset"].string)["id"].int);
        else {
            content = contentChildren[index].obj
        }
        if (content != null) {
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
                    replaceFragment(FeedbackFragment().apply { data(content) }, R.id.playerView)
                }
            }

            Handler().postDelayed({
                createNavBar()
            }, 100)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.cross, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.cross) {
            if (drawer_layout.isDrawerOpen(GravityCompat.START))
                drawer_layout.closeDrawer(GravityCompat.START)
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
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
