package trannex.ukkoteknik.com.utils

import android.util.Log
import okhttp3.ResponseBody
import java.io.*


/**
 * Created by  Manoj Sadhu on 11/29/2018.
 *
 */
fun writeResponseBodyToDisk(body: ResponseBody, assetFile: File): Boolean {
    try {

        var inputStream: InputStream? = null
        var outputStream: OutputStream? = null

        try {
            val fileReader = ByteArray(4096)

            val fileSize = body.contentLength()
            var fileSizeDownloaded: Long = 0

            inputStream = body.byteStream()
            outputStream = FileOutputStream(assetFile)

            while (true) {
                val read = inputStream!!.read(fileReader)

                if (read == -1) {
                    break
                }

                outputStream!!.write(fileReader, 0, read)

                fileSizeDownloaded += read.toLong()

                Log.d("aaa", "file download: $fileSizeDownloaded of $fileSize")
            }

            outputStream!!.flush()

            return true
        } catch (e: IOException) {
            return false
        } finally {
            if (inputStream != null) {
                inputStream!!.close()
            }

            if (outputStream != null) {
                outputStream!!.close()
            }
        }
    } catch (e: IOException) {
        return false
    }

}