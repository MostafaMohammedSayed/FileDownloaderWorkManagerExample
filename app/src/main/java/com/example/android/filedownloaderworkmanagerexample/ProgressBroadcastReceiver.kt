package com.example.android.filedownloaderworkmanagerexample

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class ProgressBroadcastReceiver: BroadcastReceiver() {
    var totalBytes = 0
    var downloadedBytes = 0
    override fun onReceive(p0: Context?, intent: Intent?) {
        if (intent?.action == "DownloadProgressMessage") {
            Log.i("ProgressBroadcastRec","receiver Called")
            downloadedBytes = intent?.extras?.get("downloadedBytes") as Int
            totalBytes = intent?.extras?.get("totalBytes") as Int
            Log.i("ProgressBroadcastRec",totalBytes.toString())
            Log.i("ProgressBroadcastRec",downloadedBytes.toString())

        }
    }
}