package trannex.ukkoteknik.com.main

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import com.github.salomonbrys.kotson.*
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.toolbar
import org.jetbrains.anko.design.appBarLayout
import org.quanqi.circularprogress.CircularProgressView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import trannex.ukkoteknik.com.R
import trannex.ukkoteknik.com.commons.Footer
import trannex.ukkoteknik.com.entities.Asset
import trannex.ukkoteknik.com.entities.Batches
import trannex.ukkoteknik.com.extensions.asyncExtension
import trannex.ukkoteknik.com.extensions.margins
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
                alpha = 0.5f
                toolbar {
                    verticalLayout {
                        gravity = Gravity.CENTER
                        textView("Trannex") {
                            textSize = 25f
                            textColor = Color.parseColor("#000000")
                            padding = 3
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
                    addView(CircularProgressView(this@MainActivity).lparams(width = 40, height = 40))
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

        val progress = indeterminateProgressDialog(message = "Please Wait") {
            setCancelable(false)
        }
        progress.show()

        assetCall.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    val assets = MyApp.gson.fromJson<List<Asset>>(response.body()!!["data"].array)

                    val assetDao = MyApp.mDatabaseHelper.getAssetDao()
                    //assets.forEach { assetDao?.createOrUpdate(it) }

                    asyncExtension(backgroundHandler = {
                        assets.forEach {
                            if (it.type.contains("zip")) {
                                downloadZip(it.name, it.id)
                            } else {
                                downloadVideo(it.name, it.id)
                            }
                        }

                    }, completeHandler = {
                        progress.dismiss()
                        complete()
                    }).execute()

                    /*downloadZip(assetsList) {
                        SelectedBatchHandler.index = index
                        startActivity<IntroActivity>("title" to batches["programData"]["name"].string)
                    }*/
                } else {
                    progress.dismiss()
                    toast("Error occurred")
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                progress.dismiss()
                toast("Error occurred")
            }
        })
    }

    private fun downloadVideo(fileName: String, assetId: Int) {
        val videoResponse = MyApp.apiInterface.downloadZip(fileName)
        val response = videoResponse.execute()
        if (response.isSuccessful) {
            writeResponseBodyToDisk(response.body()!!, File(assetsDir, assetId.toString()))
        }
    }


    private fun downloadZip(fileName: String, assetId: Int) {
        val videoResponse = MyApp.apiInterface.downloadZip(fileName)
        val response = videoResponse.execute()
        if (response.isSuccessful) {
            Decompress().init(response.body()!!.byteStream(), assetId.toString(), assetsDir.path)
        }
    }

    override fun onResume() {
        super.onResume()
        showBatches()
        sync()
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
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {

                }


            })

            logger.info("syncObject: $syncObject")


        }
    }
}
