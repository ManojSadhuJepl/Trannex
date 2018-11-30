package trannex.ukkoteknik.com.player.fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.VideoView
import com.github.salomonbrys.kotson.get
import com.github.salomonbrys.kotson.int
import com.github.salomonbrys.kotson.string
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.linearLayout
import org.jetbrains.anko.sdk27.coroutines.onCompletion
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.videoView
import trannex.ukkoteknik.com.entities.VideoAndInteractive
import trannex.ukkoteknik.com.helper.SelectedBatchHandler
import trannex.ukkoteknik.com.player.PlayerActivity
import trannex.ukkoteknik.com.singleton.MyApp
import trannex.ukkoteknik.com.utils.DeviceIdUtils
import java.sql.Timestamp

class VideoFragment : Fragment() {
    lateinit var playerActivity: PlayerActivity
    lateinit var mediaController: MediaController
    lateinit var video: VideoView
    val logger = AnkoLogger<VideoFragment>()
    lateinit var content: JsonObject
    val videoAndInteractiveDao = MyApp.mDatabaseHelper.getVideoAndInteractiveDao()

    lateinit var startTime: Timestamp
    var contentId: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        playerActivity = activity as PlayerActivity
        mediaController = MediaController(playerActivity)

        val view = UI {
            linearLayout {
                gravity = Gravity.CENTER
                video = videoView {
                    setMediaController(mediaController)
                    setVideoPath(playerActivity.assetsDir.path + "/$contentId")
                    start()
                    onCompletion {
                    }
                }.lparams(width = ViewGroup.LayoutParams.MATCH_PARENT, height = ViewGroup.LayoutParams.MATCH_PARENT)
            }
        }.view

        return view;
    }


    override fun onAttach(context: Context) {
        startTime = MyApp.getCurrentTimeStamp()
        super.onAttach(context)

    }

    override fun onDetach() {
        videoAndInteractiveDao?.create(VideoAndInteractive(
                trnx_batch_id = SelectedBatchHandler.programData()["batch_id"].int,
                trnx_batch_programs_id = SelectedBatchHandler.programData()["id"].int,
                start_time = startTime,
                trnx_content_id = contentId,
                end_time = MyApp.getCurrentTimeStamp(),
                deviceId = DeviceIdUtils(playerActivity).androidId,
                syncStatus = 0
        ))

        super.onDetach()
    }


    fun data(content: JsonObject) {
        this.content = content
        this.contentId = JsonParser().parse(content["asset"].string)["id"].int

    }
}
