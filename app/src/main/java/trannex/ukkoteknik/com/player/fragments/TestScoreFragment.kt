package trannex.ukkoteknik.com.player.fragments

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.InputType
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
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
import trannex.ukkoteknik.com.singleton.MyApp
import trannex.ukkoteknik.com.utils.DeviceIdUtils

class TestScoreFragment : Fragment() {
    lateinit var playerActivity: PlayerActivity
    val etList = mutableListOf<EditText>()
    lateinit var content: JsonObject
    val feedbackAndTestDao = MyApp.mDatabaseHelper.getFeedbackAndTestDao()
    //var previousAttendance: List<String>? = null
    private lateinit var type: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        playerActivity = activity as PlayerActivity

        return UI {
            scrollView {
                padding = dip(50)
                verticalLayout {
                    userRow(true).first.margins(bottom = dip(2))
                    for (attendee in SelectedBatchHandler.attendees()) {
                        val pair = userRow(isTitle = false,
                                attendeeJson = attendee.obj)

                        pair.first.margins(bottom = dip(2))
                        etList.add(pair.second!!)
                    }

                    buttonCustom(R.string.save).onClick {
                        save()
                    }
                    //buttonCustom(R.string.saveSyncMove)
                    buttonCustom(R.string.cancel).margins(bottom = dip(50)).onClick {
                        playerActivity.onBackPressed()
                    }
                }
            }
        }.view
    }

    fun save() {
        val scoreData = jsonArray()
        for (editText in etList) {
            scoreData.add(jsonObject(
                    "attendeeId" to editText.tag,
                    "score" to editText.text.toString().trim()
            ))


/*
            if (editText.isChecked) {
                attendanceData.add(editText.tag)
            }
*/
        }

        feedbackAndTestDao?.create(FeedbackAndTest(
                trnx_batch_id = SelectedBatchHandler.programData()["batch_id"].int,
                trnx_batch_programs_id = SelectedBatchHandler.programData()["id"].int,
                data = scoreData.toString(),
                type = type,
                deviceId = DeviceIdUtils(playerActivity).androidId,
                syncStatus = 0
        ))

        playerActivity.onBackPressed()
    }

    fun ViewGroup.userRow(isTitle: Boolean, attendeeJson: JsonObject = jsonObject()): Pair<ViewGroup, EditText?> {
        val attendee: Attendee = MyApp.gson.fromJson(attendeeJson)
        var editText: EditText? = null
        val layout = linearLayout {
            padding = 10
            backgroundColor = Color.parseColor("#00a7d0")
            weightSum = 4f


            textView(if (isTitle) "ID" else attendee.id.toString())
                    .lparams(weight = 1f, width = 0).textSize = 20f
            textView(if (isTitle) "Name" else attendee.first_name + attendee.last_name)
                    .lparams(weight = 1f, width = 0).textSize = 20f

            if (isTitle) {
                textView("Score")
                        .lparams(weight = 1f, width = 0).textSize = 20f
            } else {
                editText = editText {
                    textSize = 20f
                    tag = attendee.id
                    inputType = InputType.TYPE_CLASS_NUMBER
                    /*if (previousAttendance != null)
                        isChecked = previousAttendance!!.contains(attendee.id.toString())*/
                }.lparams(weight = 1f, width = 0) {
                    gravity = Gravity.CENTER
                }
            }

        }
        return layout to editText

    }


    override fun onAttach(context: Context) {
        super.onAttach(context)

    }

    override fun onDetach() {
        super.onDetach()
    }


    fun data(content: JsonObject, type: String) {
        this.content = content
        this.type = type
    }
}
