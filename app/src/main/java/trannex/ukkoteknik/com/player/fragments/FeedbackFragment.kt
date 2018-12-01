package trannex.ukkoteknik.com.player.fragments

import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.CheckBox
import android.widget.RatingBar
import com.github.salomonbrys.kotson.*
import com.google.gson.JsonObject
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.support.v4.UI
import trannex.ukkoteknik.com.R
import trannex.ukkoteknik.com.entities.Attendee
import trannex.ukkoteknik.com.entities.FeedbackAndTest
import trannex.ukkoteknik.com.extensions.buttonCustom
import trannex.ukkoteknik.com.extensions.margins
import trannex.ukkoteknik.com.helper.SelectedBatchHandler
import trannex.ukkoteknik.com.player.PlayerActivity
import trannex.ukkoteknik.com.player.UserList
import trannex.ukkoteknik.com.player.question
import trannex.ukkoteknik.com.singleton.Constants
import trannex.ukkoteknik.com.singleton.MyApp
import trannex.ukkoteknik.com.utils.DeviceIdUtils


class FeedbackFragment : Fragment() {
    lateinit var playerActivity: PlayerActivity
    val cbList = mutableListOf<CheckBox>()
    lateinit var content: JsonObject
    val feedbackDao = MyApp.mDatabaseHelper.getFeedbackAndTestDao()

    var selecteduserId = 0

    val feedbackData = mutableMapOf<Int, JsonObject>()

    lateinit var facultyRatingBar: RatingBar
    lateinit var contentRatingBar: RatingBar
    lateinit var facilityRatingBar: RatingBar
    lateinit var usefulnessRatingBar: RatingBar
    lateinit var overallRatingBar: RatingBar

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        playerActivity = activity as PlayerActivity

        val attendees: List<Attendee> = MyApp.gson.fromJson(SelectedBatchHandler.attendees())

        attendees.forEach {
            feedbackData.put(it.id!!, jsonObject(
                    "faculty" to 0.0f,
                    "content" to 0.0f,
                    "facility" to 0.0f,
                    "usefulness" to 0.0f,
                    "overall" to 0.0f
            ))
        }

        //previousAttendance = feedbackDao?.queryForId(content["id"].string)?.data?.let { MyApp.gson.fromJson(it) }

        return UI {
            linearLayout {
                scrollView {
                    padding = dip(50)

                    verticalLayout {
                        addView(UserList(playerActivity, attendees) {
                            if (selecteduserId == 0) {
                                selecteduserId = it
                            } else {
                                selecteduserId = it
                                feedbackData.get(selecteduserId)?.let {
                                    facilityRatingBar.rating = it["faculty"].float
                                    contentRatingBar.rating = it["content"].float
                                    facilityRatingBar.rating = it["facility"].float
                                    usefulnessRatingBar.rating = it["usefulness"].float
                                    overallRatingBar.rating = it["overall"].float
                                }
                            }
                        }.lparams(height = 0, weight = 1f))
                        linearLayout {
                            gravity = Gravity.CENTER
                            backgroundColorResource = android.R.color.transparent

                            buttonCustom(R.string.save).apply {
                                gravity = Gravity.CENTER
                                backgroundResource = R.drawable.button
                                textColor = Color.WHITE
                            }.onClick {
                                save()
                            }
                            //buttonCustom(R.string.saveSyncMove)
                            buttonCustom(R.string.cancel).apply {
                                gravity = Gravity.CENTER
                                backgroundResource = R.drawable.button
                                textColor = Color.WHITE
                            }.onClick {
                                playerActivity.onBackPressed()
                            }
                        }.margins(top = 10)

                    }


                }.lparams(height = MATCH_PARENT, width = 0, weight = 1f)
                scrollView {
                    padding = dip(50)
                    verticalLayout {
                        val faculty = question("Faculty", 0f) {
                            feedbackData.get(selecteduserId)!!["faculty"] = it
                        }
                        faculty.first
                        facultyRatingBar = faculty.second
                        val content = question("Content", 0f) {
                            feedbackData.get(selecteduserId)!!["content"] = it
                        }
                        content.first
                        contentRatingBar = content.second
                        val facility = question("Facility", 0f) {
                            feedbackData.get(selecteduserId)!!["facility"] = it
                        }
                        facility.first
                        facilityRatingBar = facility.second
                        val usefulness = question("Usefulness", 0f) {
                            feedbackData.get(selecteduserId)!!["usefulness"] = it
                        }
                        usefulness.first
                        usefulnessRatingBar = usefulness.second
                        val overall = question("Overall", 0f) {
                            feedbackData.get(selecteduserId)!!["overall"] = it
                        }
                        overall.first
                        overallRatingBar = overall.second
                    }
                }.lparams(height = MATCH_PARENT, width = 0, weight = 1f)
            }
        }.view
    }

    fun save() {
        val data = jsonArray()
        feedbackData.keys.forEach {
            data.add(jsonObject(
                    "attendeeId" to it,
                    "feedback" to feedbackData.get(it)
            ))
        }

        Log.i("saveddata", data.toString())

        feedbackDao?.create(FeedbackAndTest(
                trnx_batch_id = SelectedBatchHandler.programData()["batch_id"].int,
                trnx_batch_programs_id = SelectedBatchHandler.programData()["id"].int,
                data = data.toString(),
                type = Constants.FEEDBACK,
                deviceId = DeviceIdUtils(playerActivity).androidId,
                syncStatus = 0
        ))

        playerActivity.onBackPressed()
    }

    fun data(content: JsonObject) {
        this.content = content
    }
}
