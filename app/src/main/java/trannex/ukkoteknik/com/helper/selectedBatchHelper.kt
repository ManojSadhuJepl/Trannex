package trannex.ukkoteknik.com.helper

import com.github.salomonbrys.kotson.*
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import trannex.ukkoteknik.com.entities.Attendance
import trannex.ukkoteknik.com.entities.FeedbackAndTest
import trannex.ukkoteknik.com.extensions.equalString
import trannex.ukkoteknik.com.singleton.Constants
import trannex.ukkoteknik.com.singleton.MyApp
import java.text.SimpleDateFormat

/**
 * Created by  Manoj Sadhu on 11/26/2018.
 *
 */

object SelectedBatchHandler {
    var index: Int = 0

    var batches = jsonArray()

    fun batch(): JsonObject = batches[index].asJsonObject

    fun roles(): JsonArray = batches[index]["batchRoles"].array

    fun attendees(): JsonArray = batches[index]["attendees"].array

    fun programData(): JsonObject = batches[index]["programData"].obj

    fun programContents(): JsonArray = JsonParser().parse(batches[index]["programData"]["contents"].string)["children"].array

    fun content(position: Int = this.index): JsonArray {
        val contents = jsonArray()
        val programContents = JsonParser().parse(batches[position]["programData"]["contents"].string)["children"].array
        programContents.forEach {
            it["children"].array.forEach {
                it["children"].array.forEach {
                    contents.add(JsonParser().parse(it["asset"].string))
                }
            }
        }
        return contents
    }

    fun getContentFromId(id: Int): JsonObject? {
        val content = SelectedBatchHandler.content()

        val contentList = content.filter {
            it.obj["id"].int == id
        }
        return if (contentList.isEmpty()) {
            null
        } else {
            contentList[0].obj
        }
    }


    fun attendanceToday(): Attendance? {
        val programData = programData()
        val list = MyApp.mDatabaseHelper.getAttendanceDao()
                ?.queryBuilder()
                ?.where()
                ?.eq("trnx_batch_id", programData["batch_id"].int)
                ?.and()
                ?.eq("trnx_batch_programs_id", programData["id"].int)
                ?.query()
                ?.filter {
                    it.created_at.equalString(MyApp.getDate())
                }
        if (list!!.isEmpty())
            return null
        else {
            return list[0]
        }
    }

    fun preTestToday(): FeedbackAndTest? {
        val programData = programData()
        val list = MyApp.mDatabaseHelper.getFeedbackAndTestDao()
                ?.queryBuilder()
                ?.where()
                ?.eq("trnx_batch_id", programData["batch_id"].int)
                ?.and()
                ?.eq("trnx_batch_programs_id", programData["id"].int)
                ?.and()
                ?.eq("type", Constants.PRE_TEST)
                ?.query()
                ?.filter {
                    it.created_at.equalString(MyApp.getDate())
                }
        if (list!!.isEmpty())
            return null
        else {
            return list[0]
        }
    }


    fun attendanceData(): Pair<Boolean, String> {
        val programData = programData()
        val list = MyApp.mDatabaseHelper.getAttendanceDao()
                ?.queryBuilder()
                ?.where()
                ?.eq("trnx_batch_id", programData["batch_id"].int)
                ?.and()
                ?.eq("trnx_batch_programs_id", programData["id"].int)
                ?.query()
                ?.filter {
                    it.created_at.equalString(MyApp.getDate())
                }
        if (list!!.isEmpty())
            return false to "---"
        else {
            val dateFormat = SimpleDateFormat(Constants.TIMESTAMP_FORMAT)
            return true to dateFormat.format(list[0].created_at)
        }
    }

    fun preTestData(): Pair<Boolean, String> {
        val programData = programData()
        val list = MyApp.mDatabaseHelper.getFeedbackAndTestDao()
                ?.queryBuilder()
                ?.where()
                ?.eq("trnx_batch_id", programData["batch_id"].int)
                ?.and()
                ?.eq("trnx_batch_programs_id", programData["id"].int)
                ?.and()
                ?.eq("type", Constants.PRE_TEST)
                ?.query()
                ?.filter {
                    it.created_at.equalString(MyApp.getDate())
                }
        if (list!!.isEmpty())
            return false to "---"
        else {
            val dateFormat = SimpleDateFormat(Constants.TIMESTAMP_FORMAT)
            return true to dateFormat.format(list[0].created_at)
        }
    }

    fun postTestData(): Pair<Boolean, String> {
        val programData = programData()
        val list = MyApp.mDatabaseHelper.getFeedbackAndTestDao()
                ?.queryBuilder()
                ?.where()
                ?.eq("trnx_batch_id", programData["batch_id"].int)
                ?.and()
                ?.eq("trnx_batch_programs_id", programData["id"].int)
                ?.and()
                ?.eq("type", Constants.POST_TEST)
                ?.query()
                ?.filter {
                    it.created_at.equalString(MyApp.getDate())
                }
        if (list!!.isEmpty())
            return false to "---"
        else {
            val dateFormat = SimpleDateFormat(Constants.TIMESTAMP_FORMAT)
            return true to dateFormat.format(list[0].created_at)
        }
    }

    fun feedbackData(): Pair<Boolean, String> {
        val programData = programData()
        val list = MyApp.mDatabaseHelper.getFeedbackAndTestDao()
                ?.queryBuilder()
                ?.where()
                ?.eq("trnx_batch_id", programData["batch_id"].int)
                ?.and()
                ?.eq("trnx_batch_programs_id", programData["id"].int)
                ?.and()
                ?.eq("type", Constants.FEEDBACK)
                ?.query()
                ?.filter {
                    it.created_at.equalString(MyApp.getDate())
                }
        if (list!!.isEmpty())
            return false to "---"
        else {
            val dateFormat = SimpleDateFormat(Constants.TIMESTAMP_FORMAT)
            return true to dateFormat.format(list[0].created_at)
        }
    }

    fun activityData(contentId: Int): Pair<Boolean, String> {
        val programData = programData()
        val list = MyApp.mDatabaseHelper.getVideoAndInteractiveDao()
                ?.queryBuilder()
                ?.where()
                ?.eq("trnx_batch_id", programData["batch_id"].int)
                ?.and()
                ?.eq("trnx_batch_programs_id", programData["id"].int)
                ?.and()
                ?.eq("trnx_content_id", contentId)
                ?.query()

        if (list!!.isEmpty())
            return false to "---"
        else {
            val dateFormat = SimpleDateFormat(Constants.TIMESTAMP_FORMAT)
            return true to dateFormat.format(list[0].created_at)
        }
    }

    fun isAttendanceTaken(): Boolean {
        val programData = programData()
        return MyApp.mDatabaseHelper.getAttendanceDao()
                ?.queryBuilder()
                ?.where()
                ?.eq("trnx_batch_id", programData["batch_id"].int)
                ?.and()
                ?.eq("trnx_batch_programs_id", programData["id"].int)
                ?.query()
                ?.filter {
                    it.created_at.equalString(MyApp.getDate())
                }!!.isNotEmpty()
        /*?.and()
        ?.eq("created_at", MyApp.getDate())*/
    }

    fun isPreTestTaken(): Boolean {
        val programData = programData()
        return MyApp.mDatabaseHelper.getFeedbackAndTestDao()
                ?.queryBuilder()
                ?.where()
                ?.eq("trnx_batch_id", programData["batch_id"].int)
                ?.and()
                ?.eq("trnx_batch_programs_id", programData["id"].int)
                ?.and()
                ?.eq("type", Constants.PRE_TEST)
                ?.query()
                ?.filter {
                    it.created_at.equalString(MyApp.getDate())
                }!!.isNotEmpty()
    }

    fun isPostTestTaken(): Boolean {
        val programData = programData()
        return MyApp.mDatabaseHelper.getFeedbackAndTestDao()
                ?.queryBuilder()
                ?.where()
                ?.eq("trnx_batch_id", programData["batch_id"].int)
                ?.and()
                ?.eq("trnx_batch_programs_id", programData["id"].int)
                ?.and()
                ?.eq("type", Constants.POST_TEST)
                ?.query()
                ?.filter {
                    it.created_at.equalString(MyApp.getDate())
                }!!.isNotEmpty()
    }

    fun isFeedbackTaken(): Boolean {
        val programData = programData()
        return MyApp.mDatabaseHelper.getFeedbackAndTestDao()
                ?.queryBuilder()
                ?.where()
                ?.eq("trnx_batch_id", programData["batch_id"].int)
                ?.and()
                ?.eq("trnx_batch_programs_id", programData["id"].int)
                ?.and()
                ?.eq("type", Constants.FEEDBACK)
                ?.query()
                ?.filter {
                    it.created_at.equalString(MyApp.getDate())
                }!!.isNotEmpty()
    }

    fun isActivityTaken(contentId: Int): Boolean {
        val programData = programData()
        return MyApp.mDatabaseHelper.getVideoAndInteractiveDao()
                ?.queryBuilder()
                ?.where()
                ?.eq("trnx_batch_id", programData["batch_id"].int)
                ?.and()
                ?.eq("trnx_batch_programs_id", programData["id"].int)
                ?.and()
                ?.eq("trnx_content_id", contentId)
                ?.countOf()!! > 0
    }
}