package trannex.ukkoteknik.com.helper

import com.github.salomonbrys.kotson.*
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import trannex.ukkoteknik.com.extensions.equalString
import trannex.ukkoteknik.com.singleton.Constants
import trannex.ukkoteknik.com.singleton.MyApp

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
                ?.query()
                ?.filter {
                    it.created_at.equalString(MyApp.getDate())
                }!!.isNotEmpty()
    }
}