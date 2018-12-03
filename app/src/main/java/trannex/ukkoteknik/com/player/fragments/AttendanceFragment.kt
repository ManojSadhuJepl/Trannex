package trannex.ukkoteknik.com.player.fragments

import android.content.DialogInterface
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.CheckBox
import android.widget.LinearLayout
import com.github.salomonbrys.kotson.*
import com.google.gson.JsonObject
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.toast
import trannex.ukkoteknik.com.R
import trannex.ukkoteknik.com.entities.Attendance
import trannex.ukkoteknik.com.entities.Attendee
import trannex.ukkoteknik.com.extensions.buttonCustom
import trannex.ukkoteknik.com.extensions.margins
import trannex.ukkoteknik.com.extensions.padding
import trannex.ukkoteknik.com.helper.SelectedBatchHandler
import trannex.ukkoteknik.com.player.PlayerActivity
import trannex.ukkoteknik.com.singleton.MyApp
import trannex.ukkoteknik.com.utils.DeviceIdUtils

class AttendanceFragment : Fragment() {
    lateinit var playerActivity: PlayerActivity
    val cbList = mutableListOf<CheckBox>()
    lateinit var content: JsonObject
    val attendanceDao = MyApp.mDatabaseHelper.getAttendanceDao()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        playerActivity = activity as PlayerActivity

        return UI {
            scrollView {
                padding = dip(50)
                verticalLayout {
                    userRow(true).first.apply {
                        //alpha = 0.8f
                        backgroundColor = R.color.black_overlay
                    }
                    var color = true
                    for (attendee in SelectedBatchHandler.attendees()) {
                        val pair = userRow(isTitle = false,
                                attendeeJson = attendee.obj)

                        pair.first.apply {
                            //alpha = 0.8f
                            backgroundResource = if (color) R.color.secondRow else R.color.white
                            color = !color
                        }
                        cbList.add(pair.second!!)
                    }

                    linearLayout {
                        gravity = Gravity.END
                        backgroundColorResource = android.R.color.transparent

                        buttonCustom(R.string.save).apply {
                            gravity = Gravity.CENTER
                            this@verticalLayout.gravity = Gravity.END
                            backgroundResource = R.drawable.button
                            textColor = Color.WHITE
                        }.onClick {
                            save()
                        }
                        buttonCustom(R.string.cancel).apply {
                            gravity = Gravity.CENTER
                            this@verticalLayout.gravity = Gravity.END
                            backgroundResource = R.drawable.button_negative
                            textColor = Color.WHITE
                        }.margins(bottom = 50, left = 20).onClick {
                            playerActivity.onBackPressed()
                        }
                    }.margins(top = 10)
                    backgroundColorResource = android.R.color.transparent
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

        if (attendanceData.size() == 0) {
            toast("Please take Attendance").show()
            return
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
        var dialog: DialogInterface? = null
        var checkBox: CheckBox? = null
        val layout = linearLayout {
            padding = 10
            weightSum = 4f

            textView(if (isTitle) "ID" else attendee.id.toString()) {
                textSize = 20f
                textColor = if (isTitle) Color.WHITE else Color.BLACK
            }.lparams(weight = 1f, width = 0)
            textView(if (isTitle) "Name" else attendee.first_name + attendee.last_name) {
                textSize = 20f
                textColor = if (isTitle) Color.WHITE else Color.BLACK

                onClick {
                    dialog = alert {
                        customView {
                            verticalLayout {
                                textView("Details") {
                                    textSize = 25f
                                    textColor = Color.BLACK
                                    backgroundColor = Color.WHITE
                                    gravity = Gravity.CENTER
                                }.lparams(width = MATCH_PARENT, height = 50)
                                frameLayout {
                                    imageView(R.drawable.alert_background) {
                                        //alpha = 0.3f
                                    }
                                    backgroundColorResource = R.color.popup
                                    scrollView {
                                        verticalLayout {

                                            fun createItem(name: String, value: String): LinearLayout {
                                                return linearLayout {

                                                    textView(name) {
                                                        textColor = Color.WHITE
                                                        textSize = 22f
                                                        gravity = Gravity.CENTER
                                                        padding(all = 10)
                                                    }.lparams(width = 0, weight = 1f)
                                                    textView(":") {
                                                        textColor = Color.WHITE
                                                        textSize = 22f
                                                    }
                                                    textView(value) {
                                                        textColor = Color.WHITE
                                                        textSize = 22f
                                                        gravity = Gravity.CENTER
                                                        padding(all = 10)
                                                    }.lparams(width = 0, weight = 1f)
                                                }
                                            }

                                            attendee.first_name?.let {
                                                createItem("First name: ", attendee.first_name)
                                                view {
                                                    backgroundColor = Color.WHITE
                                                }.lparams(width = MATCH_PARENT, height = 1)
                                            }
                                            attendee.middle_name?.let {
                                                createItem("Middle name: ", attendee.middle_name)
                                                view {
                                                    backgroundColor = Color.WHITE
                                                }.lparams(width = MATCH_PARENT, height = 1)
                                            }
                                            attendee.last_name?.let {
                                                createItem("Last name: ", attendee.last_name)
                                                view {
                                                    backgroundColor = Color.WHITE
                                                }.lparams(width = MATCH_PARENT, height = 1)
                                            }
                                            attendee.gender?.let {
                                                createItem("Gender ", attendee.gender)
                                                view {
                                                    backgroundColor = Color.WHITE
                                                }.lparams(width = MATCH_PARENT, height = 1)
                                            }
                                            attendee.email?.let {
                                                createItem("Email ", attendee.email)
                                                view {
                                                    backgroundColor = Color.WHITE
                                                }.lparams(width = MATCH_PARENT, height = 1)
                                            }
                                            attendee.phone?.let {
                                                createItem("Phone ", attendee.phone)
                                            }

                                            buttonCustom("OK").apply {
                                                textSize = 22f
                                                textColor = Color.BLACK
                                                backgroundResource = R.drawable.button_white
                                                this@verticalLayout.gravity = Gravity.END
                                                margins(right = 10)
                                                onClick {
                                                    dialog!!.dismiss()
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }.show()
                }
            }.lparams(weight = 1f, width = 0)

            if (isTitle) {
                textView("Attendance") {
                    textSize = 20f
                    textColor = Color.WHITE
                }.lparams(weight = 1f, width = 0)
            } else {
                checkBox = checkBox {
                    textSize = 20f
                    tag = attendee.id
                }.lparams(weight = 1f, width = 0) {
                    gravity = Gravity.CENTER
                }
            }
        }
        return layout to checkBox

    }

    fun data(content: JsonObject) {
        this.content = content
    }
}
