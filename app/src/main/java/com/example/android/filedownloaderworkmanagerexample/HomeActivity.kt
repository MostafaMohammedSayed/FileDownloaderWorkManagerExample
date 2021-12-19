package com.example.android.filedownloaderworkmanagerexample

import android.app.Dialog
import android.app.ProgressDialog
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.android.filedownloaderworkmanagerexample.work.DownloadWorker
import com.example.android.filedownloaderworkmanagerexample.work.DownloadWorker.Companion.Progress

class HomeActivity : AppCompatActivity() {
    lateinit var downloadProgress: ProgressDialog

    val progressLiveData = MutableLiveData<Int>()

    private val receiver = ProgressBroadcastReceiver()


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

//    override fun onResume() {
//        super.onResume()
//        setUpBroadcastReceiver()
//    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(receiver)
    }

    private fun setUpViews() {
        val downloadButton = findViewById<Button>(R.id.downloadButton)
        downloadButton.setOnClickListener {
            //startJob()
            startDownloadWork()
            showDialog(PROGRESS_BAR_TYPE)
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
                val downloaded = receiver.downloadedBytes
                val total = receiver.totalBytes
                progressLiveData.observe(this@HomeActivity, Observer {
                    progress = it
                })
                Log.i("HomeActivity", progress.toString())
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

//    fun setUpBroadcastReceiver(){
//        val intentFilter = IntentFilter("DownloadProgressMessage")
//        applicationContext.registerReceiver(receiver,intentFilter)
//    }

    private fun startDownloadWork(){
        val workManager = WorkManager.getInstance(this)
        val downloadWorker = OneTimeWorkRequestBuilder<DownloadWorker>().build()

        workManager.enqueue(downloadWorker)

        workManager.getWorkInfoByIdLiveData(downloadWorker.id)
            .observe(this, Observer { workInfo->
            val status = workInfo.state
            if (status == WorkInfo.State.RUNNING){
                Toast.makeText(this,"running",Toast.LENGTH_SHORT).show()
                val progress = workInfo.progress
                val value = progress.getInt(Progress,0)
                Log.i("HomeActivityProgValue",value.toString())
                progressLiveData.value = value
            }
        })
    }
}