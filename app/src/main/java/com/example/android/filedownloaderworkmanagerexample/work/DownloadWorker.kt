package com.example.android.filedownloaderworkmanagerexample.work

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.DownloadManager.STATUS_SUCCESSFUL
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf

class DownloadWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    companion object {
        const val Progress = "Progress"
        private const val delayDuration = 1L
    }


    @SuppressLint("Range")
    override fun doWork(): Result {
        return try {
            val downloadManager =
                getSystemService(applicationContext, DownloadManager::class.java) as DownloadManager

            val request =
                DownloadManager.Request(Uri.parse("https://www.learningcontainer.com/download/sample-mp4-video-file-download-for-testing/?wpdmdl=2727&refresh=617329fb6e8b31634937339"))
                    .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or (DownloadManager.Request.NETWORK_MOBILE))
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

            val downloadId = downloadManager.enqueue(request)
            val query = DownloadManager.Query().setFilterById(downloadId)
            var isDownloading = true
            while (isDownloading) {
                val cursor = downloadManager.query(query)
                if (cursor.moveToFirst()) {
                    val downloadStatus =
                        cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                    when (downloadStatus) {
                        DownloadManager.STATUS_PENDING->{
                            val firstUpdate = workDataOf(Progress to 0)
                            setProgressAsync(firstUpdate)
                        }
                        DownloadManager.STATUS_RUNNING -> {
                             val totalBytes =
                                cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                            Log.i("JobServiceTotal", totalBytes.toString())
                            if (totalBytes > 0) {
                                 val downloadedBytes =
                                    cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                                Log.i("JobServiceDownloaded", downloadedBytes.toString())
                                val downloadPercent = ((downloadedBytes/totalBytes) * 100L)
                                Log.i("DownloadPercent",downloadPercent.toString())
                                val intermediateUpdate = workDataOf(Progress to downloadPercent)
                                setProgressAsync(intermediateUpdate)
                            }
                        }
                        DownloadManager.STATUS_SUCCESSFUL ->{
                            val lastUpdate = workDataOf(Progress to 100)
                            setProgressAsync(lastUpdate)
                            //isDownloading = false
                        }
                    }
                }
            }

            Result.success()
        } catch (exception: Exception) {
            Result.failure()
        }

    }
}