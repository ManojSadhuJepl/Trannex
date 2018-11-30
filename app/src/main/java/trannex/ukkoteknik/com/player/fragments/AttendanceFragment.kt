package trannex.ukkoteknik.com.player.fragments

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import com.github.salomonbrys.kotson.*
import com.google.gson.JsonObject
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.support.v4.alert
import trannex.ukkoteknik.com.R
import trannex.ukkoteknik.com.entities.Attendance
import trannex.ukkoteknik.com.entities.Attendee
import trannex.ukkoteknik.com.extensions.buttonCustom
import trannex.ukkoteknik.com.extensions.margins
import trannex.ukkoteknik.com.helper.SelectedBatchHandler
import trannex.ukkoteknik.com.player.PlayerActivity
import trannex.ukkoteknik.com.singleton.MyApp
import trannex.ukkoteknik.com.utils.DeviceIdUtils

class AttendanceFragment : Fragment() {
    lateinit var playerActivity: PlayerActivity
    val cbList = mutableListOf<CheckBox>()
    lateinit var content: JsonObject
    val attendanceDao = MyApp.mDatabaseHelper.getAttendanceDao()
    //var previousAttendance: List<String>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        playerActivity = activity as PlayerActivity

        //previousAttendance = attendanceDao?.queryForId(content["id"].string)?.data?.let { MyApp.gson.fromJson(it) }

        return UI {
            scrollView {
                padding = dip(50)
                verticalLayout {
                    userRow(true).first.margins(bottom = dip(2))
                    for (attendee in SelectedBatchHandler.attendees()) {
                        val pair = userRow(isTitle = false,
                                attendeeJson = attendee.obj)

                        pair.first.margins(bottom = dip(2))
                        cbList.add(pair.second!!)
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
        val attendanceData = jsonArray()
        for (checkBox in cbList) {
            if (checkBox.isChecked) {
                attendanceData.add(checkBox.tag)
            }
        }
        attendanceDao?.create(Attendance(
                trnx_batch_id = SelectedBatchHandler.programData()["batch_id"].int,
                trnx_batch_programs_id = SelectedBatchHandler.programData()["id"].int,
                attendance_data = attendanceData.toString(),
                deviceId = DeviceIdUtils(playerActivity).androidId,
                syncStatus = 0
        ))

        playerActivity.onBackPressed()
    }

    fun ViewGroup.userRow(isTitle: Boolean, attendeeJson: JsonObject = jsonObject()): Pair<ViewGroup, CheckBox?> {
        val attendee: Attendee = MyApp.gson.fromJson(attendeeJson)
        var checkBox: CheckBox? = null
        val layout = linearLayout {
            padding = 10
            backgroundColor = Color.parseColor("#00a7d0")
            weightSum = 4f


            textView(if (isTitle) "ID" else attendee.id.toString())
                    .lparams(weight = 1f, width = 0).textSize = 20f
            textView(if (isTitle) "Name" else attendee.first_name + attendee.last_name)
                    .lparams(weight = 1f, width = 0).textSize = 20f

            if (isTitle) {
                textView("Attendance")
                        .lparams(weight = 1f, width = 0).textSize = 20f
            } else {
                checkBox = checkBox {
                    textSize = 20f
                    tag = attendee.id
                    /*if (previousAttendance != null)
                        isChecked = previousAttendance!!.contains(attendee.id.toString())*/
                }.lparams(weight = 1f, width = 0) {
                    gravity = Gravity.CENTER
                }
            }

            textView(if (isTitle) "Action" else "View Details") {
                onClick {
                    alert {
                        title = "Details"
                        customView {
                            verticalLayout {
                                attendee.first_name?.let { textView("First name: " + attendee.first_name) }
                                attendee.middle_name?.let { textView("Middle name: " + attendee.middle_name) }
                                attendee.last_name?.let { textView("Last name: " + attendee.last_name) }
                                attendee.gender?.let { textView("Gender " + attendee.gender) }
                                attendee.email?.let { textView("Email " + attendee.email) }
                                attendee.phone?.let { textView("phone " + attendee.phone) }
                            }
                        }
                        yesButton {
                            text = "OK"
                        }
                    }.show()
                }
            }
                    .lparams(weight = 1f, width = 0).textSize = 20f
        }
        return layout to checkBox

    }


    override fun onAttach(context: Context) {
        super.onAttach(context)

    }

    override fun onDetach() {
        super.onDetach()
    }

    fun data(content: JsonObject) {
        this.content = content
    }
}
