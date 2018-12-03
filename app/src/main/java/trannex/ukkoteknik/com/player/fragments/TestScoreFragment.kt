package trannex.ukkoteknik.com.player.fragments

import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.InputType
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import com.github.salomonbrys.kotson.*
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.toast
import trannex.ukkoteknik.com.R
import trannex.ukkoteknik.com.entities.Attendee
import trannex.ukkoteknik.com.entities.FeedbackAndTest
import trannex.ukkoteknik.com.extensions.buttonCustom
import trannex.ukkoteknik.com.extensions.margins
import trannex.ukkoteknik.com.extensions.padding
import trannex.ukkoteknik.com.helper.SelectedBatchHandler
import trannex.ukkoteknik.com.player.PlayerActivity
import trannex.ukkoteknik.com.singleton.Constants
import trannex.ukkoteknik.com.singleton.MyApp
import trannex.ukkoteknik.com.utils.DeviceIdUtils

class TestScoreFragment : Fragment() {
    lateinit var playerActivity: PlayerActivity
    val etList = mutableListOf<EditText>()
    lateinit var content: JsonObject
    val feedbackAndTestDao = MyApp.mDatabaseHelper.getFeedbackAndTestDao()
    //var previousAttendance: List<String>? = null
    private lateinit var type: String

    var preTestData: List<String> = listOf()

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

                    val attendees: List<Attendee> = MyApp.gson.fromJson(SelectedBatchHandler.attendees())

                    val presentIds: List<String> = MyApp.gson.fromJson(SelectedBatchHandler.attendanceToday()!!.attendance_data)
                    Log.i("presentIds", presentIds.toString())
                    var presentAttendance: List<Attendee>

                    if (type == Constants.PRE_TEST) {
                        presentAttendance = attendees.filter {
                            presentIds.contains(it.id!!.toString())
                        }
                    } else {
                        preTestData = JsonParser().parse(SelectedBatchHandler.preTestToday()!!.data).array.map {
                            it["attendeeId"].string
                        }

                        presentAttendance = attendees.filter {
                            preTestData.contains(it.id!!.toString())
                        }

                    }

                    for (attendee in presentAttendance) {

                        var score = ""

                        if (type == Constants.POST_TEST) {
                            score = preTestData.filter {
                                it == attendee!!.id.toString()
                            }[0]
                        }

                        val pair = userRow(isTitle = false, score = score,
                                attendee = attendee)

                        pair.first.apply {
                            //alpha = 0.8f
                            backgroundResource = if (color) R.color.secondRow else R.color.white
                            color = !color
                        }
                        etList.add(pair.second!!)
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
                        //buttonCustom(R.string.saveSyncMove)
                        buttonCustom(R.string.cancel).apply {
                            gravity = Gravity.CENTER
                            this@verticalLayout.gravity = Gravity.END
                            backgroundResource = R.drawable.button_negative
                            textColor = Color.WHITE
                        }.margins(bottom = 50, left = 20).onClick {
                            playerActivity.onBackPressed()
                        }
                    }.margins(top = 10)
                }
            }
        }.view
    }

    fun save() {
        val scoreData = jsonArray()
        for (editText in etList) {
            if (editText.text.toString().trim().isNotEmpty()) {
                scoreData.add(jsonObject(
                        "attendeeId" to editText.tag,
                        "score" to editText.text.toString().trim()
                ))
            }
        }
        if (scoreData.size() == 0) {
            toast("Please give Score")
            return
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

    fun ViewGroup.userRow(isTitle: Boolean, score: String = "", attendee: Attendee? = null): Pair<ViewGroup, EditText?> {
        var editText: EditText? = null
        var dialog: DialogInterface? = null
        val layout = linearLayout {
            padding = 10
            //backgroundColor = Color.parseColor("#00a7d0")
            weightSum = 4f

            gravity = Gravity.CENTER_VERTICAL

            textView(if (isTitle) "ID" else attendee!!.id.toString()) {
                textSize = 20f
                textColor = if (isTitle) Color.WHITE else Color.BLACK
            }.lparams(weight = 1f, width = 0)
            textView(if (isTitle) "Name" else attendee!!.first_name + attendee!!.last_name) {
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
                                }.lparams(width = ViewGroup.LayoutParams.MATCH_PARENT, height = 50)
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

                                            attendee!!.first_name?.let {
                                                createItem("First name: ", attendee!!.first_name!!)
                                                view {
                                                    backgroundColor = Color.WHITE
                                                }.lparams(width = ViewGroup.LayoutParams.MATCH_PARENT, height = 1)
                                            }
                                            attendee!!.middle_name?.let {
                                                createItem("Middle name: ", attendee!!.middle_name!!)
                                                view {
                                                    backgroundColor = Color.WHITE
                                                }.lparams(width = ViewGroup.LayoutParams.MATCH_PARENT, height = 1)
                                            }
                                            attendee!!.last_name?.let {
                                                createItem("Last name: ", attendee!!.last_name!!)
                                                view {
                                                    backgroundColor = Color.WHITE
                                                }.lparams(width = ViewGroup.LayoutParams.MATCH_PARENT, height = 1)
                                            }
                                            attendee!!.gender?.let {
                                                createItem("Gender ", attendee!!.gender!!)
                                                view {
                                                    backgroundColor = Color.WHITE
                                                }.lparams(width = ViewGroup.LayoutParams.MATCH_PARENT, height = 1)
                                            }
                                            attendee!!.email?.let {
                                                createItem("Email ", attendee!!.email!!)
                                                view {
                                                    backgroundColor = Color.WHITE
                                                }.lparams(width = ViewGroup.LayoutParams.MATCH_PARENT, height = 1)
                                            }
                                            attendee!!.phone?.let {
                                                createItem("Phone ", attendee!!.phone!!)
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

            if (type == Constants.POST_TEST) {
                textView(if (isTitle) "Pre test Score" else score) {
                    textSize = 20f
                    textColor = if (isTitle) Color.WHITE else Color.BLACK
                }.lparams(weight = 1f, width = 0)
            }



            if (isTitle) {
                textView("Score") {
                    textSize = 20f
                    textColor = if (isTitle) Color.WHITE else Color.BLACK
                }.lparams(weight = 1f, width = 0)
            } else {
                editText = editText {
                    textSize = 20f
                    tag = attendee!!.id
                    inputType = InputType.TYPE_CLASS_NUMBER

                    //background.mutate().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                    //textColor = Color.WHITE
                    /*if (previousAttendance != null)
                        isChecked = previousAttendance!!.contains(attendee!!.id.toString())*/
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
