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
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
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
        val reminderId = intent.getStringExtra(EXTRA_REMINDER_ID)
        val category = intent.getStringExtra(EXTRA_CATEGORY) ?: "Worship"
        val title = intent.getStringExtra(EXTRA_TITLE) ?: "Noor-e-Zindagi Reminder"
        val body = intent.getStringExtra(EXTRA_BODY) ?: "Time for remembrance of Allah and authentic Sunnah."
        val notificationId = intent.getIntExtra(EXTRA_ID, 1001)
        val soundType = intent.getStringExtra(EXTRA_SOUND) ?: "chime"
        val frequency = intent.getStringExtra(EXTRA_FREQUENCY) ?: "daily"
        val hour = intent.getIntExtra(EXTRA_HOUR, -1)
        val minute = intent.getIntExtra(EXTRA_MINUTE, -1)
        val isTest = intent.getBooleanExtra(EXTRA_IS_TEST, false)

        ReminderScheduler.ensureChannels(context)

        val todayMatches = isTest || ReminderScheduler.shouldTriggerOnCalendar(frequency, Calendar.getInstance())
        if (todayMatches) {
            postNotification(context, notificationId, title, body, soundType)
        }

        // Automatically re-arm the next occurrence so recurring reminders never stop after one day
        if (!isTest && !reminderId.isNullOrBlank() && hour in 0..23 && minute in 0..59) {
            ReminderScheduler.scheduleReminder(
                context = context,
                reminder = LocalReminderEntity(
                    id = reminderId,
                    category = category,
                    title = title,
                    hour = hour,
                    minute = minute,
                    enabled = true,
                    frequency = frequency,
                    soundType = soundType
                )
            )
        }
    }

    private fun postNotification(
        context: Context,
        notificationId: Int,
        title: String,
        body: String,
        soundType: String
    ) {
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

        val channelId = if (soundType == "adhan") {
            ReminderScheduler.CHANNEL_ID_ADHAN
        } else {
            ReminderScheduler.CHANNEL_ID_CHIME
        }

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setContentIntent(pendingTap)
            .setAutoCancel(true)
            .setPriority(
                if (soundType == "adhan") NotificationCompat.PRIORITY_HIGH
                else NotificationCompat.PRIORITY_DEFAULT
            )

        when (soundType) {
            "silent" -> builder.setSilent(true)
            "adhan" -> builder.setSound(ReminderScheduler.adhanSoundUri(context))
            else -> builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
        }

        NotificationManagerCompat.from(context).notify(notificationId, builder.build())
    }

    companion object {
        const val EXTRA_REMINDER_ID = "extra_reminder_id"
        const val EXTRA_CATEGORY = "extra_category"
        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_BODY = "extra_body"
        const val EXTRA_ID = "extra_id"
        const val EXTRA_SOUND = "extra_sound"
        const val EXTRA_FREQUENCY = "extra_frequency"
        const val EXTRA_HOUR = "extra_hour"
        const val EXTRA_MINUTE = "extra_minute"
        const val EXTRA_IS_TEST = "extra_is_test"
    }
}

object ReminderScheduler {
    const val CHANNEL_ID_CHIME = "noor_worship_reminders"
    const val CHANNEL_ID_ADHAN = "noor_worship_adhan_v2"

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

    fun adhanSoundUri(context: Context): Uri =
        Uri.parse("android.resource://${context.packageName}/${R.raw.adhan_sound}")

    fun shouldTriggerOnCalendar(frequency: String, cal: Calendar): Boolean {
        val dow = cal.get(Calendar.DAY_OF_WEEK)
        return when (frequency.lowercase()) {
            "fridays" -> dow == Calendar.FRIDAY
            "weekdays" -> dow != Calendar.SATURDAY && dow != Calendar.SUNDAY
            else -> true
        }
    }

    fun computeNextTriggerMillis(
        hour: Int,
        minute: Int,
        frequency: String,
        nowMillis: Long = System.currentTimeMillis()
    ): Long {
        val cal = Calendar.getInstance().apply {
            timeInMillis = nowMillis
            set(Calendar.HOUR_OF_DAY, hour.coerceIn(0, 23))
            set(Calendar.MINUTE, minute.coerceIn(0, 59))
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (timeInMillis <= nowMillis) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }
        repeat(8) {
            if (shouldTriggerOnCalendar(frequency, cal)) {
                return cal.timeInMillis
            }
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }
        return cal.timeInMillis
    }

    fun ensureChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val chimeChannel = NotificationChannel(
                CHANNEL_ID_CHIME,
                "Worship & Sunnah Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Gentle reminders for Morning/Evening Azkar, Quran, and Bedtime Sunnah."
            }
            nm.createNotificationChannel(chimeChannel)

            val adhanChannel = NotificationChannel(
                CHANNEL_ID_ADHAN,
                "Salah Adhan Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Custom Maqam Hijaz Adhan chime for daily Salah reminders."
                setSound(
                    adhanSoundUri(context),
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
            }
            nm.createNotificationChannel(adhanChannel)
        }
    }

    fun scheduleReminder(context: Context, reminder: LocalReminderEntity) {
        ensureChannels(context)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra(ReminderReceiver.EXTRA_ID, reminder.id.hashCode())
            putExtra(ReminderReceiver.EXTRA_REMINDER_ID, reminder.id)
            putExtra(ReminderReceiver.EXTRA_CATEGORY, reminder.category)
            putExtra(ReminderReceiver.EXTRA_TITLE, reminder.title)
            putExtra(
                ReminderReceiver.EXTRA_BODY,
                "Gentle reminder (${reminder.category}): Open Noor-e-Zindagi for verified guidance."
            )
            putExtra(ReminderReceiver.EXTRA_SOUND, reminder.soundType)
            putExtra(ReminderReceiver.EXTRA_FREQUENCY, reminder.frequency)
            putExtra(ReminderReceiver.EXTRA_HOUR, reminder.hour)
            putExtra(ReminderReceiver.EXTRA_MINUTE, reminder.minute)
            putExtra(ReminderReceiver.EXTRA_IS_TEST, false)
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

        val triggerAt = computeNextTriggerMillis(reminder.hour, reminder.minute, reminder.frequency)
        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pi)
    }

    fun showTestNotification(
        context: Context,
        title: String,
        body: String,
        soundType: String = "chime"
    ) {
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra(ReminderReceiver.EXTRA_ID, if (soundType == "adhan") 9998 else 9999)
            putExtra(ReminderReceiver.EXTRA_TITLE, title)
            putExtra(ReminderReceiver.EXTRA_BODY, body)
            putExtra(ReminderReceiver.EXTRA_SOUND, soundType)
            putExtra(ReminderReceiver.EXTRA_IS_TEST, true)
        }
        ReminderReceiver().onReceive(context, intent)
    }
}
