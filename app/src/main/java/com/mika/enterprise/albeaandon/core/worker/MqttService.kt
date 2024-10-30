package com.mika.enterprise.albeaandon.core.worker

import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.ConnectivityManager
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.mika.enterprise.albeaandon.MainActivity
import com.mika.enterprise.albeaandon.R
import com.mika.enterprise.albeaandon.core.util.Constant.NOTIFICATION_CHANEL_ID
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.android.service.MqttService
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage

class MqttService : MqttService() {

    val testingURI = "tcp://broker.hivemq.com:1883"
    val testClientId = "ANDON TEST"

    private val myBroadcastReceiver = MqttBroadcastReceiver()
    private val intentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
    private lateinit var mqttClient: MqttAndroidClient


    override fun onCreate() {
        super.onCreate()
        mqttClient = MqttAndroidClient(baseContext, testingURI, testClientId)
        setupMqtt()
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        mqttClient =
            MqttAndroidClient(baseContext, testingURI, testClientId, MqttAndroidClient.Ack.AUTO_ACK)
        setupMqtt()
        return START_STICKY
    }


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    fun setupMqtt() {
        this.registerReceiver(myBroadcastReceiver, intentFilter)
        val mqttConnectOptions = MqttConnectOptions()
        mqttConnectOptions.isAutomaticReconnect = true
        mqttConnectOptions.isCleanSession = false
        mqttConnectOptions.keepAliveInterval = 60
        mqttClient.connect(
            mqttConnectOptions,
            null,
            object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    val disconnectedBufferOptions = DisconnectedBufferOptions()
                    disconnectedBufferOptions.isBufferEnabled = true
                    disconnectedBufferOptions.bufferSize = 1000
                    disconnectedBufferOptions.isPersistBuffer = false
                    disconnectedBufferOptions.isDeleteOldestMessages = false
                    mqttClient.setBufferOpts(disconnectedBufferOptions)
                    mqttClient.subscribe("handheld/alert/#", 0)
                    Log.e("MQTT Connect", "Success")
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.e("MQTT Connect", "Failure: $asyncActionToken $exception")
                }

            }
        )
        mqttClient.setCallback(object : MqttCallbackExtended {
            override fun connectionLost(cause: Throwable?) {
                Log.e("Connection Lost", "${cause?.message}")
            }

            override fun messageArrived(topic: String?, message: MqttMessage?) {
                Log.e("MQTT Data", topic.toString() + message.toString())
                //showNotification()
            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {

            }

            override fun connectComplete(reconnect: Boolean, serverURI: String?) {
                Log.d("MQTT Connect", "$reconnect $serverURI")
            }

        })

        Log.e("isClientConnected", mqttClient.isConnected.toString())
        // Subscribe to a topic
    }

    fun showNotification(message: String) {
        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define
            if (ContextCompat.checkSelfPermission(
                    baseContext,
                    POST_NOTIFICATIONS
                ) == PERMISSION_GRANTED
            ) notify(1, buildNotification(message))
        }
    }

    private fun buildNotification(message: String): Notification {
        val intent = Intent(baseContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(baseContext, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val builder = NotificationCompat.Builder(this, NOTIFICATION_CHANEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("New Notification")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            // Set the intent that will fire when the user taps the notification
            .setContentIntent(pendingIntent)
            .setAutoCancel(true) // Automatically remove the notification when the user taps it
        return builder.build()
    }

}