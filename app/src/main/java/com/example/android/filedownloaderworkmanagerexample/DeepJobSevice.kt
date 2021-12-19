package com.example.android.filedownloaderworkmanagerexample

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Looper
import android.os.Message
import android.util.Log
import java.util.logging.Handler

class DeepJobService: JobService() {
    @SuppressLint("Range")
    override fun onStartJob(p0: JobParameters?): Boolean {
        val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val request = DownloadManager.Request(Uri.parse("https://www.learningcontainer.com/download/sample-mp4-video-file-download-for-testing/?wpdmdl=2727&refresh=617329fb6e8b31634937339"))
            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or(DownloadManager.Request.NETWORK_MOBILE))
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        val downloadId = downloadManager.enqueue(request)
        val query = DownloadManager.Query().setFilterById(downloadId)
        val isDownloading = true
        while (isDownloading){
            val cursor = downloadManager.query(query)
            if (cursor.moveToFirst()){
                val downloadStatus = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                when(downloadStatus){
                    DownloadManager.STATUS_RUNNING->{
                        val totalBytes = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                        Log.i("JobServiceTotal",totalBytes.toString())
                        if (totalBytes>0){
                            val percent = 100L
                            val downloadedBytes = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                            Log.i("JobServiceDownloaded",downloadedBytes.toString())
                            //val progress = (downloadedBytes.div(totalBytes) * percent).toInt()
                            //Log.i("JobServiceProgress",progress.toString())


                            val handler = android.os.Handler(Looper.getMainLooper(),android.os.Handler.Callback {
                                val intent = Intent("DownloadProgressMessage").apply {
                                    putExtra("downloadedBytes",downloadedBytes)
                                    putExtra("totalBytes",totalBytes)
                                }
                                sendBroadcast(intent)
                                return@Callback true
                            })

                            val msg = Message()
                            handler.dispatchMessage(msg)
                        }
                    }
                }
            }
        }
        return true
    }

    override fun onStopJob(p0: JobParameters?): Boolean {
        return true
    }

}
