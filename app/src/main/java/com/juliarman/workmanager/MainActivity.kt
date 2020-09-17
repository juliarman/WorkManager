package com.juliarman.workmanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.work.*
import androidx.work.WorkInfo.State

import kotlinx.android.synthetic.main.activity_main.*
import java.security.KeyStore
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(), View.OnClickListener {


    private lateinit var periodicWorkRequest: PeriodicWorkRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_one_time_task.setOnClickListener(this)
        btnPeriodicTask.setOnClickListener(this)
        btnCancelTask.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when(v.id){

            R.id.btn_one_time_task ->{
                startOntimeTask()
            }

            R.id.btnPeriodicTask ->{

                startPeriodicTask()

            }

            R.id.btnCancelTask -> {

                cancelTask()

            }

        }
    }

    private fun cancelTask() {

        WorkManager.getInstance().cancelWorkById(periodicWorkRequest.id)

    }

    private fun startPeriodicTask() {

        text_status.text = getString(R.string.status)
        val data = Data.Builder()
            .putString(MyWorker.EXTRA_CITY, edt_city.text.toString())
            .build()

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        periodicWorkRequest = PeriodicWorkRequest.Builder(MyWorker::class.java, 15, TimeUnit.MINUTES)
            .setInputData(data)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance().enqueue(periodicWorkRequest)
        WorkManager.getInstance().getWorkInfoByIdLiveData(periodicWorkRequest.id).observe(this@MainActivity,
            object : Observer<WorkInfo> {
                override fun onChanged(workInfo: WorkInfo) {

                    val status = workInfo.state.name
                    text_status.append("\n" + status)
                    btnCancelTask.isEnabled = false
                    if (workInfo.state == State.ENQUEUED){

                        btnCancelTask.isEnabled = true
                    }


                }

            })

    }

    private fun startOntimeTask() {

        text_status.text = getString(R.string.status)
        val data = Data.Builder()
            .putString(MyWorker.EXTRA_CITY, edt_city.text.toString())
            .build()


        //di eksekusiki klw terhubung di internet
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val oneTimeWorkRequest = OneTimeWorkRequest.Builder(MyWorker::class.java)
            .setInputData(data)
            .setConstraints(constraints)
            .build()



        WorkManager.getInstance().enqueue(oneTimeWorkRequest)
        WorkManager.getInstance().getWorkInfoByIdLiveData(oneTimeWorkRequest.id).observe(this@MainActivity, object : Observer<WorkInfo>{
            override fun onChanged(t: WorkInfo) {

                val status = t.state.name
                text_status.append("\n" + status)
            }


        })

    }
}