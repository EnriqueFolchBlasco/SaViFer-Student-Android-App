import android.app.NotificationChannel
import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import android.app.NotificationManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import es.efb.isvf_studentapp.R



//https://developer.android.com/develop/background-work/background-tasks/persistent/getting-started?hl=es-419

//https://github.com/android/sunflower
//https://developer.android.com/codelabs/android-workmanager?hl=es-419#0


class NotificationWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {
    override fun doWork(): Result {
        val title = inputData.getString("taskTitle") ?: "Sense titul"
        val description = inputData.getString("taskDescription") ?: "Sense descripcio"

        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        createNotificationChannel(notificationManager)

        val notification = NotificationCompat.Builder(applicationContext, "task_channel")
            .setContentTitle(title)
            .setContentText(description)
            .setSmallIcon(R.drawable.icon_01d)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notificationManager.notify(1, notification)
        return Result.success()
    }

    private fun createNotificationChannel(notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "task_channel",
                "Task Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Canal per a les notificacions"
            }
            notificationManager.createNotificationChannel(channel)
        }
    }
}
