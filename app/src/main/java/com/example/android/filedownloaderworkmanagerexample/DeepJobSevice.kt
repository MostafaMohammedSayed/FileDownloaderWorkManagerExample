package com.example.android.filedownloaderworkmanagerexample

import android.app.DownloadManager
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.net.Uri
import com.example.android.filedownloaderworkmanagerexample.HomeActivity.Companion.PROGRESS_BAR_TYPE

class DeepJobService: JobService() {
    override fun onStartJob(p0: JobParameters?): Boolean {
        val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val request = DownloadManager.Request(Uri.parse("https://www.learningcontainer.com/download/sample-mp4-video-file-download-for-testing/?wpdmdl=2727&refresh=617329fb6e8b31634937339"))
            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or(DownloadManager.Request.NETWORK_MOBILE))
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        val downloadId = downloadManager.enqueue(request)
        //HomeActivity().showDialog(PROGRESS_BAR_TYPE)
        return true
    }

    override fun onStopJob(p0: JobParameters?): Boolean {
        return true
    }

}
