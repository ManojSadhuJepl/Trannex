package trannex.ukkoteknik.com.main

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import com.github.salomonbrys.kotson.*
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.toolbar
import org.jetbrains.anko.design.appBarLayout
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.quanqi.circularprogress.CircularProgressView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import trannex.ukkoteknik.com.R
import trannex.ukkoteknik.com.commons.Footer
import trannex.ukkoteknik.com.entities.Asset
import trannex.ukkoteknik.com.entities.Batches
import trannex.ukkoteknik.com.extensions.asyncExtension
import trannex.ukkoteknik.com.extensions.buttonCustom
import trannex.ukkoteknik.com.extensions.margins
import trannex.ukkoteknik.com.helper.ProgressDialogHelper
import trannex.ukkoteknik.com.helper.SelectedBatchHandler
import trannex.ukkoteknik.com.intro.IntroActivity
import trannex.ukkoteknik.com.singleton.MyApp
import trannex.ukkoteknik.com.utils.Decompress
import trannex.ukkoteknik.com.utils.DeviceIdUtils
import trannex.ukkoteknik.com.utils.writeResponseBodyToDisk
import java.io.File


class MainActivity : AppCompatActivity() {
    lateinit var rootView: LinearLayout
    lateinit var contentView: HorizontalScrollView
    val logger = AnkoLogger<MainActivity>()
    lateinit var assetsDir: File
    lateinit var loading: CircularProgressView
    lateinit var retry: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        assetsDir = File(filesDir.path + "/assets")
        if (!assetsDir.exists()) {
            assetsDir.mkdir()
        }

/*        val actionBar = supportActionBar
        actionBar!!.alp
        actionBar!!.setBackgroundDrawable(resources.getDrawable(R.drawable.appbar))*/

        rootView = verticalLayout {
            gravity = Gravity.CENTER_HORIZONTAL

            appBarLayout {
                backgroundColor = Color.parseColor("#ffffff")
                //alpha = 0.5f
                toolbar {
                    linearLayout {
                        gravity = Gravity.CENTER
                        textView("Trannex") {
                            textSize = 25f
                            textColor = Color.parseColor("#000000")
                            padding = 3
                        }
                        view().lparams(width = 0, height = 0, weight = 1f)
                        imageView(R.drawable.sync) {
                            onClick {
                                sync()
                            }
                        }

                    }
                }
            }

            backgroundResource = R.drawable.background

            /*val appbar = AppBar(this@verticalLayout)
            val params1 = appbar.view.layoutParams as LinearLayout.LayoutParams
            params1.apply {
                width = ViewGroup.LayoutParams.MATCH_PARENT
                height = dip(50)
            }
            appbar.view.layoutParams = params1*/

            contentView = horizontalScrollView {
                isFillViewport = true
                linearLayout {
                    gravity = Gravity.CENTER
                    loading = CircularProgressView(this@MainActivity).lparams(width = 40, height = 40)
                    addView(loading)
                    retry = buttonCustom("Retry").apply {
                        visibility = GONE
                        onClick {
                            loading.visibility = VISIBLE
                            retry.visibility = GONE
                            getBatches()
                        }
                    }
                }
            }.lparams(height = 0, weight = 1f)
            val footer = Footer(this@verticalLayout)
            val params2 = footer.view.layoutParams as LinearLayout.LayoutParams
            params2.apply {
                width = ViewGroup.LayoutParams.MATCH_PARENT
                height = dip(50)
            }

            footer.view.layoutParams = params2
        }


        setContentView(rootView)
        getBatches()

    }

    fun getBatches() {
        val batchesCall = MyApp.apiInterface.getBatches("1")
        batchesCall.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                try {
                    val responseObject = response.body()
                    if (responseObject != null) {
                        if (responseObject["api_message"].string == "success") {
                            SelectedBatchHandler.batches = responseObject["data"].array
                            MyApp.mDatabaseHelper.clearBatches()
                            MyApp.mDatabaseHelper.getBatchesDao()?.create(Batches(responseObject["data"].toString()))
                        }
                        showBatches()
                    }
                } catch (e: Exception) {
                    showBatches()
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                val batchesList = MyApp.mDatabaseHelper.getBatchesDao()?.queryForAll()
                if (batchesList!!.isEmpty()) {
                    loading.visibility = GONE
                    retry.visibility = VISIBLE
                    toast("Please check your internet connection and click on retry button.").show()
                }
                showBatches()
            }
        })
    }

    fun showBatches() {
        val batchesList = MyApp.mDatabaseHelper.getBatchesDao()?.queryForAll()
        if (batchesList?.size!! > 0) {
            SelectedBatchHandler.batches = JsonParser().parse(batchesList[0].data).array
            contentView.removeAllViews()

            contentView.linearLayout {
                gravity = Gravity.CENTER

                val fileIdsInAppDir = assetsDir.listFiles().map { it.name.toInt() }

                for ((index, batches) in SelectedBatchHandler.batches.withIndex()) {
                    if (batches["programData"].nullObj != null) {

                        val contentIds = SelectedBatchHandler.content(index).map {
                            it["id"].int
                        }.toMutableSet()
                        contentIds.removeAll(fileIdsInAppDir)
                        val isContainsAssets = contentIds.isEmpty()


                        logger.info("contentIds $index  :  ${contentIds.toString()}")

                        programCard(activity = this@MainActivity,
                                program = batches["programData"]["name"].string,
                                batchName = batches["batch_name"].string,
                                duration = if (batches["duration"].nullString == null) "---" else batches["duration"].string,
                                location = if (batches["location"].nullString == null) "---" else batches["location"].string,
                                index = index,
                                isContainsAssets = isContainsAssets,
                                click = {
                                    getAssets(contentIds, index, batches) {
                                        SelectedBatchHandler.index = index
                                        startActivity<IntroActivity>("title" to batches["programData"]["name"].string)
                                    }
                                }).margins(left = 20)
                    }
                }
            }
        }
    }

    fun getAssets(assetsList: MutableSet<Int>, index: Int, batches: JsonElement, complete: () -> Unit) {
        val assetCall = MyApp.apiInterface.getAssets(assetsList.toString())

        val progress = ProgressDialogHelper.getInstance(this)
        progress.showProgress()

        assetCall.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                try {
                    if (response.isSuccessful) {
                        val assets = MyApp.gson.fromJson<List<Asset>>(response.body()!!["data"].array)

                        val assetDao = MyApp.mDatabaseHelper.getAssetDao()
                        //assets.forEach { assetDao?.createOrUpdate(it) }

                        asyncExtension(backgroundHandler = {
                            assets.forEach {
                                if (it.type.contains("zip")) {
                                    if (!downloadZip(it.name, it.id))
                                        return@asyncExtension false
                                } else {
                                    if (!downloadVideo(it.name, it.id))
                                        return@asyncExtension false
                                }
                            }
                            return@asyncExtension true
                        }, completeHandler = {
                            progress.closeProgress()
                            if (it)
                                complete()
                            else
                                toast("Error occurred")

                        }).execute()

                        /*downloadZip(assetsList) {
                        SelectedBatchHandler.index = index
                        startActivity<IntroActivity>("title" to batches["programData"]["name"].string)
                    }*/
                    } else {
                        progress.closeProgress()
                        toast("Error occurred")
                    }
                } catch (e: Exception) {
                    progress.closeProgress()
                    toast("Error occurred")
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                progress.closeProgress()
                toast("Error occurred")
            }
        })
    }

    private fun downloadVideo(fileName: String, assetId: Int): Boolean {
        try {
            val videoResponse = MyApp.apiInterface.downloadZip(fileName)
            val response = videoResponse.execute()
            if (response.isSuccessful) {
                writeResponseBodyToDisk(response.body()!!, File(assetsDir, assetId.toString()))
            }
            return response.isSuccessful
        } catch (e: Exception) {
            return false
        }
    }


    private fun downloadZip(fileName: String, assetId: Int): Boolean {
        try {
            val videoResponse = MyApp.apiInterface.downloadZip(fileName)
            val response = videoResponse.execute()
            if (response.isSuccessful) {
                Decompress().init(response.body()!!.byteStream(), assetId.toString(), assetsDir.path)
            }
            return response.isSuccessful
        } catch (e: Exception) {
            return false
        }
    }

    override fun onResume() {
        super.onResume()
        showBatches()
        //sync()
    }


    fun sync() {
        val attendanceList = MyApp.mDatabaseHelper.getAttendanceDao()
                ?.queryBuilder()
                ?.where()
                ?.eq("syncStatus", 0)
                ?.query()
                ?.toList()
        val feedbackAndTestList = MyApp.mDatabaseHelper.getFeedbackAndTestDao()
                ?.queryBuilder()
                ?.where()
                ?.eq("syncStatus", 0)
                ?.query()
                ?.toList()
        val videoAndInteractiveList = MyApp.mDatabaseHelper.getVideoAndInteractiveDao()
                ?.queryBuilder()
                ?.where()
                ?.eq("syncStatus", 0)
                ?.query()
                ?.toList()

        if (attendanceList!!.isNotEmpty() || feedbackAndTestList!!.isNotEmpty() || videoAndInteractiveList!!.isNotEmpty()) {
            val syncObject = jsonObject(
                    "device_id" to DeviceIdUtils(this).androidId,
                    "data" to jsonObject(
                            "attendance" to MyApp.gson.toJson(attendanceList),
                            "feedbackAndTest" to MyApp.gson.toJson(feedbackAndTestList),
                            "videoAndInteractive" to MyApp.gson.toJson(videoAndInteractiveList)
                    ).toString()
            )

            val progress = indeterminateProgressDialog(message = "Please Wait,Sync in process.") {
                setCancelable(false)
            }
            progress.show()

            val syncCall = MyApp.apiInterface.postData(syncObject)
            syncCall.enqueue(object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if (response.isSuccessful) {
                        if (response.body()?.get("api_status")?.int == 1) {
                            attendanceList.forEach {
                                it.syncStatus = 1
                                MyApp.mDatabaseHelper.getAttendanceDao()?.update(it)
                            }
                            feedbackAndTestList!!.forEach {
                                it.syncStatus = 1
                                MyApp.mDatabaseHelper.getFeedbackAndTestDao()?.update(it)
                            }
                            videoAndInteractiveList!!.forEach {
                                it.syncStatus = 1
                                MyApp.mDatabaseHelper.getVideoAndInteractiveDao()?.update(it)
                            }
                        }
                        progress.dismiss()
                        alert("Sync Successful") {
                            yesButton { }
                        }.show()
                    } else {
                        progress.dismiss()
                        alert("Sync Failed.") {
                            yesButton { }
                        }.show()
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    progress.dismiss()
                    alert("Please check internet and retry.") {
                        yesButton { }
                    }.show()
                }


            })

            //logger.info("syncObject: $syncObject")


        } else {
            alert("Already Synced.") {
                yesButton { }
            }.show()
        }
    }
}
