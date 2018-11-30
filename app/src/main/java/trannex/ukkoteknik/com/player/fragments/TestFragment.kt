package trannex.ukkoteknik.com.player.fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.JsonObject
import trannex.ukkoteknik.com.R
import trannex.ukkoteknik.com.extensions.replaceChildFragment
import trannex.ukkoteknik.com.player.PlayerActivity


class TestFragment : Fragment() {
    lateinit var playerActivity: PlayerActivity
    lateinit var content: JsonObject
    lateinit var type: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        playerActivity = activity as PlayerActivity

        val view = inflater.inflate(R.layout.test_fragment, container, false)

        //replaceChildFragment(WebFragment().apply { data(this@TestFragment.content) }, R.id.contentView)
        replaceChildFragment(TestScoreFragment().apply { this.data(this@TestFragment.content,type) }, R.id.testView)

        /* return UI {
            linearLayout {
                scrollView {

                }.lparams(weight = 1f)
                scrollView {

                }.lparams(weight = 1f)
            }
        }.view*/
        return view
    }


    fun data(content: JsonObject, type: String) {
        this.content = content
        this.type = type
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)

    }

    override fun onDetach() {
        super.onDetach()
    }
}
