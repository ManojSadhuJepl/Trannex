package trannex.ukkoteknik.com.player.fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.verticalLayout
import trannex.ukkoteknik.com.player.PlayerActivity


class BlankFragment : Fragment() {
    lateinit var playerActivity: PlayerActivity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        playerActivity = activity as PlayerActivity


        return UI {
            verticalLayout {

            }
        }.view

    }


    override fun onAttach(context: Context) {
        super.onAttach(context)

    }

    override fun onDetach() {
        super.onDetach()
    }
}
