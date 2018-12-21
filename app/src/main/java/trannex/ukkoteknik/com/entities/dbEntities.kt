package trannex.ukkoteknik.com.entities

import com.google.gson.annotations.SerializedName
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable
import trannex.ukkoteknik.com.singleton.Constants
import trannex.ukkoteknik.com.singleton.MyApp
import trannex.ukkoteknik.com.utils.Exclude
import java.sql.Date
import java.sql.Timestamp

/**
 * Created by  Manoj Sadhu on 11/27/2018.
 *
 */


@DatabaseTable
class Asset() {
    @DatabaseField(id = true)
    var id = 0;
    @DatabaseField
    @SerializedName("file_name")
    var name = ""
    @DatabaseField()
    @SerializedName("file_type")
    var type = ""

}

@DatabaseTable
class Batches() {
    @DatabaseField(generatedId = true)
    var id = 0
    @DatabaseField
    var data = ""

    constructor(data: String) : this() {
        this.data = data
    }
}

/*@DatabaseTable
class BatchExecution() {
    @DatabaseField()
    var batchId = 0
    @DatabaseField(format = Constants.TIMESTAMP_FORMAT)
    var created_at: Date = MyApp.getDate()
    @DatabaseField()
    var timeSpent: String = ""
    @Exclude
    @DatabaseField
    var syncStatus: Int = 1

    constructor(batchId: Int, timeSpent: String) : this() {
        this.batchId = batchId
        this.timeSpent = timeSpent
    }
}*/

@DatabaseTable
data class Attendance(
        @Exclude
        @DatabaseField(generatedId = true)
        var id: Int = 0,
        @DatabaseField
        var trnx_batch_id: Int,
        @DatabaseField
        var trnx_batch_programs_id: Int,
        @DatabaseField
        var attendance_data: String,
        @DatabaseField
        var geo_location: String = "",
        @DatabaseField
        var deviceId: String,
        @Exclude
        @DatabaseField
        var syncStatus: Int,
        @DatabaseField(format = Constants.TIMESTAMP_FORMAT)
        var created_at: Date = MyApp.getDate(),
        @DatabaseField
        var created_by: Int = 1
) {
    constructor() : this(0, 0, 0, "", "", "", 1, MyApp.getDate(), 1)
}

@DatabaseTable
data class FeedbackAndTest(
        @Exclude
        @DatabaseField(generatedId = true)
        var id: Int = 0,
        @DatabaseField
        var trnx_batch_id: Int,
        @DatabaseField
        var trnx_batch_programs_id: Int,
        @DatabaseField
        var data: String,
        @DatabaseField
        var type: String,
        @DatabaseField
        var geo_location: String = "",
        @DatabaseField
        var deviceId: String,
        @Exclude
        @DatabaseField
        var syncStatus: Int,
        @DatabaseField(format = Constants.TIMESTAMP_FORMAT)
        var created_at: Date = MyApp.getDate(),
        @DatabaseField
        var created_by: Int = 1
) {
    constructor() : this(0, 0, 0, "", "", "", "", 1, MyApp.getDate(), 1)
}

@DatabaseTable
data class VideoAndInteractive(
        @Exclude
        @DatabaseField(generatedId = true)
        var id: Int = 0,
        @DatabaseField
        var trnx_batch_id: Int,
        @DatabaseField
        var trnx_batch_programs_id: Int,
        @DatabaseField
        var trnx_content_id: Int?,
        @DatabaseField
        var start_time: Timestamp?,
        @DatabaseField
        var end_time: Timestamp?,
        @DatabaseField
        var geo_location: String = "",
        @DatabaseField
        var deviceId: String,
        @Exclude
        @DatabaseField
        var syncStatus: Int,
        @DatabaseField(format = Constants.TIMESTAMP_FORMAT)
        var created_at: Date = MyApp.getDate(),
        @DatabaseField
        var created_by: Int = 1
) {
    constructor() : this(0, 0, 0, 0, null, null, "", "", 1, MyApp.getDate(), 1)
}
