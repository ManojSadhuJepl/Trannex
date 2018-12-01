package trannex.ukkoteknik.com.player

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import trannex.ukkoteknik.com.R
import trannex.ukkoteknik.com.entities.Attendee
import trannex.ukkoteknik.com.extensions.padding

/**
 * Created by  Manoj Sadhu on 12/1/2018.
 *
 */
class UserList(context: Context?, attendees: List<Attendee>, val userChange: (id: Int) -> Unit) : LinearLayout(context) {
    lateinit var selectedUser: View

    init {
        if (attendees.isNotEmpty()) {
            val layout = verticalLayout {
                attendees.forEach {
                    textView("${it.first_name} ${it.last_name}") {
                        textSize = 20f
                        textColor = Color.WHITE
                        backgroundResource = R.drawable.user
                        padding(all = 10)
                        tag = it.id

                        onClick {
                            if (selectedUser != this@textView) {
                                selectedUser.setBackgroundResource(R.drawable.user)
                                this@textView.setBackgroundResource(R.drawable.user_selected)
                                selectedUser = this@textView
                                userChange(selectedUserId())
                            }
                        }
                    }.lparams(width = ViewGroup.LayoutParams.MATCH_PARENT)
                    view().lparams(height = 10)
                }
            }
            val child = layout.getChildAt(0)
            child.setBackgroundResource(R.drawable.user_selected)
            selectedUser = child
            userChange(selectedUserId())
        }
    }

    fun selectedUserId(): Int = selectedUser.getTag() as Int
}