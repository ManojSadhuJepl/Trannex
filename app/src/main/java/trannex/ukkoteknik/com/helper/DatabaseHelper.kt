package trannex.ukkoteknik.com.helper

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper
import com.j256.ormlite.dao.Dao
import com.j256.ormlite.support.ConnectionSource
import com.j256.ormlite.table.TableUtils
import trannex.ukkoteknik.com.entities.*
import java.sql.SQLException
import java.util.*

class DatabaseHelper(context: Context, name: String) : OrmLiteSqliteOpenHelper(context, name, null, 1) {

    private var attendanceDao: Dao<Attendance, String>? = null
    private var batchesDao: Dao<Batches, String>? = null
    private var feedbackAndTestDao: Dao<FeedbackAndTest, String>? = null
    private var videoAndInteractiveDao: Dao<VideoAndInteractive, String>? = null
    private var assetDao: Dao<Asset, String>? = null
    //private var batchExecutionDao: Dao<BatchExecution, String>? = null

    override fun onCreate(sqLiteDatabase: SQLiteDatabase, connectionSource: ConnectionSource) {
        try {
            sqLiteDatabase.setLocale(Locale.ENGLISH)
            TableUtils.createTable(connectionSource, Attendance::class.java)
            TableUtils.createTable(connectionSource, Batches::class.java)
            TableUtils.createTable(connectionSource, FeedbackAndTest::class.java)
            TableUtils.createTable(connectionSource, VideoAndInteractive::class.java)
            TableUtils.createTable(connectionSource, Asset::class.java)
            //TableUtils.createTable(connectionSource, BatchExecution::class.java)
        } catch (e: SQLException) {
            e.printStackTrace()
        }

    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, connectionSource: ConnectionSource, i: Int, i1: Int) {
        dropTables()
        onCreate(sqLiteDatabase, connectionSource)
    }

    fun dropTables() {
        try {
            TableUtils.dropTable<Attendance, Any>(this.getConnectionSource(), Attendance::class.java, true)
            TableUtils.dropTable<Batches, Any>(this.getConnectionSource(), Batches::class.java, true)
            TableUtils.dropTable<FeedbackAndTest, Any>(this.getConnectionSource(), FeedbackAndTest::class.java, true)
            TableUtils.dropTable<VideoAndInteractive, Any>(this.getConnectionSource(), VideoAndInteractive::class.java, true)
            TableUtils.dropTable<Asset, Any>(this.getConnectionSource(), Asset::class.java, true)
            //TableUtils.dropTable<BatchExecution, Any>(this.getConnectionSource(), BatchExecution::class.java, true)
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    fun clearTables() {
        try {
            TableUtils.clearTable(this.getConnectionSource(), Attendance::class.java)
            TableUtils.clearTable(this.getConnectionSource(), Batches::class.java)
            TableUtils.clearTable(this.getConnectionSource(), FeedbackAndTest::class.java)
            TableUtils.clearTable(this.getConnectionSource(), VideoAndInteractive::class.java)
            TableUtils.clearTable(this.getConnectionSource(), Asset::class.java)
            //TableUtils.clearTable(this.getConnectionSource(), BatchExecution::class.java)
        } catch (e: SQLException) {
            e.printStackTrace()
        }

    }

    fun clearBatches() {
        try {
            TableUtils.clearTable(this.getConnectionSource(), Batches::class.java)
        } catch (e: SQLException) {
            e.printStackTrace()
        }

    }

    fun getAttendanceDao(): Dao<Attendance, String>? {
        if (attendanceDao == null) {
            try {
                attendanceDao = getDao(Attendance::class.java)
            } catch (e: SQLException) {
                e.printStackTrace()
            }

        }
        return attendanceDao
    }

    fun getBatchesDao(): Dao<Batches, String>? {
        if (batchesDao == null) {
            try {
                batchesDao = getDao(Batches::class.java)
            } catch (e: SQLException) {
                e.printStackTrace()
            }

        }
        return batchesDao
    }

    fun getFeedbackAndTestDao(): Dao<FeedbackAndTest, String>? {
        if (feedbackAndTestDao == null) {
            try {
                feedbackAndTestDao = getDao(FeedbackAndTest::class.java)
            } catch (e: SQLException) {
                e.printStackTrace()
            }

        }
        return feedbackAndTestDao
    }

    fun getVideoAndInteractiveDao(): Dao<VideoAndInteractive, String>? {
        if (videoAndInteractiveDao == null) {
            try {
                videoAndInteractiveDao = getDao(VideoAndInteractive::class.java)
            } catch (e: SQLException) {
                e.printStackTrace()
            }
        }
        return videoAndInteractiveDao
    }

    fun getAssetDao(): Dao<Asset, String>? {
        if (assetDao == null) {
            try {
                assetDao = getDao(Asset::class.java)
            } catch (e: SQLException) {
                e.printStackTrace()
            }
        }
        return assetDao
    }

/*
    fun getBatchExecutionDao(): Dao<BatchExecution, String>? {
        if (batchExecutionDao == null) {
            try {
                batchExecutionDao = getDao(BatchExecution::class.java)
            } catch (e: SQLException) {
                e.printStackTrace()
            }
        }
        return batchExecutionDao
    }
*/

    companion object {
        private val TAG = "DatabaseHelper"
    }
}

