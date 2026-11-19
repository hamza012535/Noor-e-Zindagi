package com.example.notifications

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.R
import com.example.data.local.LocalReminderEntity
import java.util.Calendar

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra(EXTRA_TITLE) ?: "Noor-e-Zindagi Reminder"
        val body = intent.getStringExtra(EXTRA_BODY) ?: "Time for remembrance of Allah and authentic Sunnah."
        val notificationId = intent.getIntExtra(EXTRA_ID, 1001)
        val soundType = intent.getStringExtra(EXTRA_SOUND) ?: "chime"

        ReminderScheduler.ensureChannel(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val tapIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingTap = PendingIntent.getActivity(
            context,
            notificationId,
            tapIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, ReminderScheduler.CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setContentIntent(pendingTap)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        if (soundType == "silent") {
            builder.setSilent(true)
        } else {
            builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
        }

        NotificationManagerCompat.from(context).notify(notificationId, builder.build())
    }

    companion object {
        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_BODY = "extra_body"
        const val EXTRA_ID = "extra_id"
        const val EXTRA_SOUND = "extra_sound"
    }
}

object ReminderScheduler {
    const val CHANNEL_ID = "noor_worship_reminders"

    val defaultReminders: List<LocalReminderEntity> = listOf(
        LocalReminderEntity("rem_fajr", "Salah", "Fajr Prayer & Dawn Sunnah", 5, 15, true, "daily", "adhan"),
        LocalReminderEntity("rem_morning_azkar", "Morning Azkar", "Morning Azkar & Ayat-ul-Kursi", 6, 15, true, "daily", "chime"),
        LocalReminderEntity("rem_quran", "Quran", "Daily Quran Reading & Reflection", 7, 30, true, "daily", "chime"),
        LocalReminderEntity("rem_dhuhr", "Salah", "Dhuhr Prayer", 12, 35, true, "daily", "adhan"),
        LocalReminderEntity("rem_asr", "Salah", "Asr Prayer", 15, 45, true, "daily", "adhan"),
        LocalReminderEntity("rem_evening_azkar", "Evening Azkar", "Evening Azkar & 3 Quls", 17, 0, true, "daily", "chime"),
        LocalReminderEntity("rem_maghrib", "Salah", "Maghrib Prayer", 17, 45, true, "daily", "adhan"),
        LocalReminderEntity("rem_hadith", "Hadith & Sunnah", "Daily Hadith & Sunnah Lesson", 19, 0, false, "daily", "chime"),
        LocalReminderEntity("rem_isha", "Salah", "Isha & Witr Prayer", 19, 30, true, "daily", "adhan"),
        LocalReminderEntity("rem_istighfar", "Dhikr", "Istighfar & Durood Shareef", 20, 30, false, "fridays", "chime"),
        LocalReminderEntity("rem_sleep", "Sleeping", "Bedtime 10-Step Sunnah & Tasbih Fatimi", 22, 0, true, "daily", "chime")
    )

    fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Worship & Sunnah Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Gentle reminders for Salah, Morning/Evening Azkar, Quran, and Bedtime Sunnah."
            }
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.createNotificationChannel(channel)
        }
    }

    fun scheduleReminder(context: Context, reminder: LocalReminderEntity) {
        ensureChannel(context)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra(ReminderReceiver.EXTRA_ID, reminder.id.hashCode())
            putExtra(ReminderReceiver.EXTRA_TITLE, reminder.title)
            putExtra(
                ReminderReceiver.EXTRA_BODY,
                "Gentle reminder (${reminder.category}): Open Noor-e-Zindagi for verified guidance."
            )
            putExtra(ReminderReceiver.EXTRA_SOUND, reminder.soundType)
        }
        val pi = PendingIntent.getBroadcast(
            context,
            reminder.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (!reminder.enabled) {
            alarmManager.cancel(pi)
            return
        }

        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, reminder.hour)
            set(Calendar.MINUTE, reminder.minute)
            set(Calendar.SECOND, 0)
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }
        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.timeInMillis, pi)
    }

    fun showTestNotification(context: Context, title: String, body: String) {
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra(ReminderReceiver.EXTRA_ID, 9999)
            putExtra(ReminderReceiver.EXTRA_TITLE, title)
            putExtra(ReminderReceiver.EXTRA_BODY, body)
            putExtra(ReminderReceiver.EXTRA_SOUND, "chime")
        }
        ReminderReceiver().onReceive(context, intent)
    }
}
