package com.mika.enterprise.albeaandon

import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.mika.enterprise.albeaandon.core.util.Constant.NOTIFICATION_CHANEL_ID
import com.mika.enterprise.albeaandon.core.util.Constant.UNIQUE_WORK_NAME
import com.mika.enterprise.albeaandon.core.util.Constant.USER_NIK
import com.mika.enterprise.albeaandon.core.worker.MqttBroadcastReceiver
import com.mika.enterprise.albeaandon.core.worker.MqttWorker
import com.mika.enterprise.albeaandon.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import info.mqtt.android.service.MqttAndroidClient
import info.mqtt.android.service.QoS
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mqttAndroidClient: MqttAndroidClient
    private val intentFilter = IntentFilter("MqttMessage")

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        this.registerReceiver(MqttBroadcastReceiver(), intentFilter, Context.RECEIVER_EXPORTED)
        mqttAndroidClient = MqttAndroidClient(applicationContext, SERVER_URI, clientId)
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
            ExistingWorkPolicy.REPLACE, // Replace existing work
            workRequest
        )
    }

    fun stopBackgroundService() {
        WorkManager.getInstance(this).cancelUniqueWork(UNIQUE_WORK_NAME)
    }

    fun setupMqtt() {
        val mqttConnectOptions = MqttConnectOptions()
        mqttConnectOptions.isAutomaticReconnect = true
        mqttConnectOptions.isCleanSession = false
        mqttConnectOptions.keepAliveInterval = 60
        if (mqttAndroidClient.isConnected.not())
            mqttAndroidClient.connect(mqttConnectOptions, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken) {
                    val disconnectedBufferOptions = DisconnectedBufferOptions()
                    disconnectedBufferOptions.isBufferEnabled = true
                    disconnectedBufferOptions.bufferSize = 1000
                    disconnectedBufferOptions.isPersistBuffer = false
                    disconnectedBufferOptions.isDeleteOldestMessages = false
                    mqttAndroidClient.setBufferOpts(disconnectedBufferOptions)
                    subscribeToTopic()
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    exception?.printStackTrace()
                    Log.d("MQTT", "Failed to connect to: $SERVER_URI, exception: $exception")
                }
            })
        mqttAndroidClient.setCallback(object : MqttCallbackExtended {
            override fun connectComplete(reconnect: Boolean, serverURI: String) {
                if (reconnect) subscribeToTopic()
                else Log.d("MQTT", "connectComplete: $serverURI")

            }

            override fun connectionLost(cause: Throwable?) {

            }

            override fun messageArrived(topic: String, message: MqttMessage) {
                Log.d("MQTT", "Receive message: $message from topic: $topic")
                showNotification(topic, message.toString())
            }

            override fun deliveryComplete(token: IMqttDeliveryToken) {

            }
        })
    }

    fun subscribeToTopic() {
        mqttAndroidClient.subscribe(
            getSubscriptionTopic(),
            QoS.AtLeastOnce.value,
            null,
            object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken) {
                    Log.d("MQTT", "Subscribed!")
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.d("MQTT", "Failed to subscribe")
                }
            })
    }

    fun getSubscriptionTopic(): String {
        val userNik = sharedPreferences.getString(USER_NIK, "")
        return "handheld/alert/#"
    }


    fun showNotification(topic: String, message: String) {
        with(NotificationManagerCompat.from(this)) {
            if (ContextCompat.checkSelfPermission(
                    baseContext,
                    POST_NOTIFICATIONS
                ) == PERMISSION_GRANTED
            ) notify(1, buildNotification(topic, message))
        }
    }

    private fun buildNotification(topic: String, message: String): Notification {
        val intent = Intent(baseContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(baseContext, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val builder = NotificationCompat.Builder(this, NOTIFICATION_CHANEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(topic)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
        return builder.build()
    }

    companion object {
        private const val SERVER_URI = "tcp://broker.hivemq.com:1883"
        private var clientId = "ANDON_UAT"
    }
}