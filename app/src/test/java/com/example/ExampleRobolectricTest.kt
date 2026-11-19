package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.audio.AmbientSoundState
import com.example.audio.QuranReciter
import com.example.data.corpus.QuranCatalogData
import com.example.data.corpus.SunnahRoutineRepository
import com.example.notifications.ReminderScheduler
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.Calendar

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Noor-e-Zindagi", appName)
  }

  @Test
  fun `ambient sound and quran catalog invariants hold`() {
    assertEquals(114, QuranCatalogData.allSurahs.size)
    assertTrue(SunnahRoutineRepository.allActivities.isNotEmpty())
    assertEquals(QuranReciter.SUDAIS, QuranReciter.fromEditionId("ar.abdurrahmaansudais"))
    assertFalse(AmbientSoundState(isEnabled = false, birdsVolume = 0.8f).hasActiveSound)
    assertTrue(AmbientSoundState(isEnabled = true, birdsVolume = 0.3f, rainVolume = 0f, airVolume = 0f).hasActiveSound)
  }

  @Test
  fun `reminder scheduler enforces day of week filters and computes next matching day`() {
    val monday = Calendar.getInstance().apply {
      set(2026, Calendar.NOVEMBER, 16, 10, 0, 0)
      set(Calendar.MILLISECOND, 0)
    }
    val friday = Calendar.getInstance().apply {
      set(2026, Calendar.NOVEMBER, 20, 10, 0, 0)
      set(Calendar.MILLISECOND, 0)
    }
    val sunday = Calendar.getInstance().apply {
      set(2026, Calendar.NOVEMBER, 22, 10, 0, 0)
      set(Calendar.MILLISECOND, 0)
    }

    assertTrue(ReminderScheduler.shouldTriggerOnCalendar("daily", sunday))
    assertTrue(ReminderScheduler.shouldTriggerOnCalendar("weekdays", monday))
    assertFalse(ReminderScheduler.shouldTriggerOnCalendar("weekdays", sunday))
    assertFalse(ReminderScheduler.shouldTriggerOnCalendar("fridays", monday))
    assertTrue(ReminderScheduler.shouldTriggerOnCalendar("fridays", friday))

    val nextFridayMillis = ReminderScheduler.computeNextTriggerMillis(
      hour = 20,
      minute = 30,
      frequency = "fridays",
      nowMillis = monday.timeInMillis
    )
    val scheduledCal = Calendar.getInstance().apply { timeInMillis = nextFridayMillis }
    assertEquals(Calendar.FRIDAY, scheduledCal.get(Calendar.DAY_OF_WEEK))
    assertEquals(20, scheduledCal.get(Calendar.HOUR_OF_DAY))
    assertEquals(30, scheduledCal.get(Calendar.MINUTE))
  }
}
