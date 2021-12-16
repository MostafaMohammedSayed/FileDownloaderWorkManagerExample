package com.example.android.filedownloaderworkmanagerexample

import android.app.Dialog
import android.app.ProgressDialog
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {
    lateinit var downloadProgress: ProgressDialog

    companion object {
        const val PROGRESS_BAR_TYPE = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        downloadProgress = ProgressDialog(this)
        Log.i("HomeActivity", (this::downloadProgress.isInitialized).toString())

        setUpViews()

    }

    private fun setUpViews() {
        val downloadButton = findViewById<Button>(R.id.downloadButton)
        downloadButton.setOnClickListener {
            startJob()
        }
    }

    private fun startJob() {
        val jobInfo = JobInfo.Builder(123, ComponentName(this, DeepJobService::class.java))

        val job = jobInfo.setRequiresCharging(false)
            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
            .build()

        val scheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        scheduler.schedule(job)
    }

    override fun onCreateDialog(id: Int): Dialog? {
        return if (id == PROGRESS_BAR_TYPE) {
            downloadProgress.apply {
                setMessage("Downloading file. Please wait...")
                isIndeterminate = false
                max = 100
                setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
                setCancelable(true)
            }
            //downloadProgress.show()
            downloadProgress
        } else {
            null
        }
    }
}