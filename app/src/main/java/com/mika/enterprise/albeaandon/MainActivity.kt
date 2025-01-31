package com.mika.enterprise.albeaandon

import android.content.Context
import android.content.IntentFilter
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.mika.enterprise.albeaandon.core.util.Constant.UNIQUE_WORK_NAME
import com.mika.enterprise.albeaandon.core.util.Constant.USER_NIK
import com.mika.enterprise.albeaandon.core.util.ContextUtils.Companion.getSavedLanguagePreference
import com.mika.enterprise.albeaandon.core.util.ContextUtils.Companion.updateLocale
import com.mika.enterprise.albeaandon.core.worker.MqttBroadcastReceiver
import com.mika.enterprise.albeaandon.core.worker.MqttWorker
import com.mika.enterprise.albeaandon.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val intentFilter = IntentFilter("MqttMessage")

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        this.registerReceiver(MqttBroadcastReceiver(), intentFilter, Context.RECEIVER_EXPORTED)
        setContentView(binding.root)
    }

    fun startMqttService() {
        backGroundService()
    }

    fun stopMqttService() {
        stopBackgroundService()
    }

    fun backGroundService() {
        val nik = sharedPreferences.getString(USER_NIK, "")
        val workerData = workDataOf(USER_NIK to nik)
        val workRequest = OneTimeWorkRequestBuilder<MqttWorker>()
            .setInputData(workerData)
            .build()
        WorkManager.getInstance(this).enqueueUniqueWork(
            UNIQUE_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }

    private fun stopBackgroundService() {
        WorkManager.getInstance(this).cancelUniqueWork(UNIQUE_WORK_NAME)
    }

    override fun attachBaseContext(newBase: Context) {
        val selectedLanguage = getSavedLanguagePreference(newBase)
        val localeToSwitch = Locale(selectedLanguage)
        val localeUpdatedContext = updateLocale(newBase, localeToSwitch)
        super.attachBaseContext(localeUpdatedContext)
    }
}