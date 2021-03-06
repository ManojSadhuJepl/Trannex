package trannex.ukkoteknik.com.singleton

/**
 * Created by  Manoj Sadhu on 10/16/2018.
 *
 */
object Constants {
    const val ATTENDANCE = "attendance"
    const val PPT = "ppt"
    const val PDF = "pdf"
    const val INTERACTIVE = "interactive"
    const val VIDEO = "video"
    const val FEEDBACK = "feedback"
    const val PRE_TEST = "preTest"
    const val POST_TEST = "postTest"

    const val TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss"
    const val TIMESTAMP_DEFAULT = "0000-00-00 00:00:00"
    const val DATE_FORMAT = "yyyy-MM-dd"
    const val DATE_DEFAULT = "0000-00-00"

    val displayTypes = mapOf(
            ATTENDANCE to "Attendance",
            PPT to "PPT",
            PDF to "PDF",
            INTERACTIVE to "Interactive",
            VIDEO to "Video",
            PRE_TEST to "Pre Test",
            POST_TEST to "Post Test",
            FEEDBACK to "Feedback",
            "Type" to "Type"
    )
}

/*
Faculty

Content

facility

Usefulness

Overall

 */