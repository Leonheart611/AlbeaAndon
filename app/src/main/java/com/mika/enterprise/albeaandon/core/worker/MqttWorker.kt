package com.mika.enterprise.albeaandon.core.worker

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.mika.enterprise.albeaandon.MainActivity
import com.mika.enterprise.albeaandon.R
import com.mika.enterprise.albeaandon.core.util.Constant.NOTIFICATION_CHANEL_ID
import com.mika.enterprise.albeaandon.core.util.Constant.USER_NIK
import info.mqtt.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage


class MqttWorker(val context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    val serverURI = "mqtt://techpack-iot.duckdns.org"
    val clientId = "mqttx_f549a571"

    val testingURI = "tcp://broker.hivemq.com:1883"
    val testClientId = "clientId-7v5PSjEMDr"

    val intent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    val pendingIntent: PendingIntent =
        PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

    private val mqttClient = MqttAndroidClient(context, testingURI, testClientId)

    override fun doWork(): Result {
        if (isStopped) {
            mqttClient.disconnect()
            return Result.success()
        } else {
            createNotificationChannel()
            val mqttConnectOptions = MqttConnectOptions()
            mqttConnectOptions.isAutomaticReconnect = true
            mqttConnectOptions.isCleanSession = false
            mqttConnectOptions.keepAliveInterval = 60
            mqttClient.connect(mqttConnectOptions, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken) {
                    val disconnectedBufferOptions = DisconnectedBufferOptions()
                    disconnectedBufferOptions.isBufferEnabled = true
                    disconnectedBufferOptions.bufferSize = 1000
                    disconnectedBufferOptions.isPersistBuffer = false
                    disconnectedBufferOptions.isDeleteOldestMessages = false
                    mqttClient.setBufferOpts(disconnectedBufferOptions)
                    subscribeTopic()
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    exception?.printStackTrace()
                    Log.d("MQTT", "Failed to connect to: $testingURI, exception: $exception")
                }
            })

            mqttClient.setCallback(object : MqttCallbackExtended {
                override fun connectionLost(cause: Throwable?) {
                    Log.e("Connection Lost", "${cause?.message}")
                }

                override fun messageArrived(topic: String?, message: MqttMessage?) {
                    val payload = message?.payload?.toString(Charsets.UTF_8)
                    Log.e("MQTT Data", payload.toString())
                    showNotification(topic.toString(), payload.toString())
                }

                override fun deliveryComplete(token: IMqttDeliveryToken?) {

                }

                override fun connectComplete(reconnect: Boolean, serverURI: String?) {
                    Log.d("MQTT Connect", "$reconnect $serverURI")
                    if (reconnect) subscribeTopic(false)
                }


            })
            return Result.success()
        }
    }

    fun subscribeTopic(showNotification: Boolean = true) {
        val nik = inputData.getString(USER_NIK)
        mqttClient.subscribe(
            "handheld/alert/$nik",
            0,
            userContext = null,
            object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    if (showNotification)
                        showNotification(
                            "New Notification",
                            "Success Subscribe topic: handheld/alert/$nik"
                        )
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.e("Error Subs", exception.toString())
                }

            })
    }

    fun showNotification(topic: String, message: String) {
        with(NotificationManagerCompat.from(context)) {
            // notificationId is a unique int for each notification that you must define
            if (ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.POST_NOTIFICATIONS
                )
                == android.content.pm.PackageManager.PERMISSION_GRANTED
            ) notify(1, buildNotification(topic, message))
        }
    }

    private fun buildNotification(topic: String, message: String): Notification {
        val builder = NotificationCompat.Builder(context, NOTIFICATION_CHANEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(topic)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            // Set the intent that will fire when the user taps the notification
            .setContentIntent(pendingIntent)
            .setAutoCancel(true) // Automatically remove the notification when the user taps it
        return builder.build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Andon Apps"
            val descriptionText = "Notification"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(NOTIFICATION_CHANEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}