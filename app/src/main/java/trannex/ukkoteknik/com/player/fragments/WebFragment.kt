package trannex.ukkoteknik.com.player.fragments

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import com.github.salomonbrys.kotson.get
import com.github.salomonbrys.kotson.int
import com.github.salomonbrys.kotson.string
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.jetbrains.anko.linearLayout
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.webView
import trannex.ukkoteknik.com.entities.VideoAndInteractive
import trannex.ukkoteknik.com.extensions.margins
import trannex.ukkoteknik.com.helper.SelectedBatchHandler
import trannex.ukkoteknik.com.player.PlayerActivity
import trannex.ukkoteknik.com.singleton.MyApp
import trannex.ukkoteknik.com.utils.DeviceIdUtils
import java.io.File
import java.sql.Timestamp

class WebFragment : Fragment() {
    lateinit var playerActivity: PlayerActivity
    lateinit var web: WebView
    lateinit var content: JsonObject

    val videoAndInteractiveDao = MyApp.mDatabaseHelper.getVideoAndInteractiveDao()
    lateinit var startTime: Timestamp
    var skipRecord: Boolean = false
    var contentId: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        playerActivity = activity as PlayerActivity

        val view = UI {
            linearLayout {
                gravity = Gravity.CENTER
                web = webView {
                    settings.apply {
                        javaScriptEnabled = true
                        webViewClient = WebViewController()
                        allowUniversalAccessFromFileURLs = true

                        try {
                            val intractiveFolder = File(playerActivity.assetsDir, "/" + contentId)
                            val filePath = "file://" + intractiveFolder.path + "/" + intractiveFolder.listFiles()[0].name + "/index_english.html"
                            loadUrl(filePath)
                            //loadUrl("https://www.google.com")
                        } catch (e: Exception) {
                            //playerActivity.onBackPressed()
                        }
                    }
                }.apply {
                    lparams(width = ViewGroup.LayoutParams.MATCH_PARENT, height = ViewGroup.LayoutParams.MATCH_PARENT)
                    margins(bottom = 33)
                }
            }
        }.view


        return view;
    }

    fun data(content: JsonObject, skipRecord: Boolean = false) {
        this.content = content
        this.skipRecord = skipRecord
        this.contentId = JsonParser().parse(content["asset"].string)["id"].int
    }


    inner class WebViewController : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {

            view.loadUrl(url)
            return true
        }

        @TargetApi(Build.VERSION_CODES.M)
        override fun onReceivedError(view: WebView, request: WebResourceRequest,
                                     error: WebResourceError) {
            super.onReceivedError(view, request, error)
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        override fun onReceivedHttpError(view: WebView,
                                         request: WebResourceRequest, errorResponse: WebResourceResponse) {
            super.onReceivedHttpError(view, request, errorResponse)
        }
    }


    override fun onAttach(context: Context) {
        startTime = MyApp.getCurrentTimeStamp()
        super.onAttach(context)

    }

    override fun onDetach() {
        if (!skipRecord) {
            videoAndInteractiveDao?.create(VideoAndInteractive(
                    trnx_batch_id = SelectedBatchHandler.programData()["batch_id"].int,
                    trnx_batch_programs_id = SelectedBatchHandler.programData()["id"].int,
                    start_time = startTime,
                    trnx_content_id = contentId,
                    end_time = MyApp.getCurrentTimeStamp(),
                    deviceId = DeviceIdUtils(playerActivity).androidId,
                    syncStatus = 0
            ))
        }

        super.onDetach()
    }
}
