package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import com.udacity.project4.locationreminders.data.dto.ReminderDTO

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import kotlinx.coroutines.ExperimentalCoroutinesApi;
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Test

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)

//Unit test the DAO
@SmallTest
class RemindersDaoTest {

//    TODO: Add testing implementation to the RemindersDao.kt

    private lateinit var database: RemindersDatabase

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun initDb() {
        database = Room.inMemoryDatabaseBuilder(
            getApplicationContext(),
            RemindersDatabase::class.java
        ).build()
    }

    @After
    fun closeDb() = database.close()


    private fun getReminder(): ReminderDTO {
        return ReminderDTO(
            title = "title",
            description = "desc",
            location = "loc",
            latitude = 47.5456551,
            longitude = 122.0101731)
    }

    @Test
    fun insertReminderAndFindById() = runBlockingTest {
        val reminder = getReminder()
        database.reminderDao().saveReminder(reminder)
        val loaded = database.reminderDao().getReminderById(reminder.id)
        assertThat<ReminderDTO>(loaded as ReminderDTO, notNullValue())
        assertThat(loaded.id, `is`(reminder.id))
        assertThat(loaded.title, `is`(reminder.title))
        assertThat(loaded.description, `is`(reminder.description))
        assertThat(loaded.latitude, `is`(reminder.latitude))
        assertThat(loaded.longitude, `is`(reminder.longitude))
        assertThat(loaded.location, `is`(reminder.location))
    }
    @Test
    fun getAllRemindersAndDelete() = runBlockingTest {
        //GIVEN - Insert two  reminder
        val reminder = ReminderDTO("Titel1", "Description1", "Location1", 50.0, 100.0)
        val reminder2 = ReminderDTO("Titel12", "Description2", "Location2", 150.0, 200.0)
        database.reminderDao().saveReminder(reminder)
        database.reminderDao().saveReminder(reminder2)

        //WHEN - Get all reminders from database
        val loadedList = database.reminderDao().getReminders()

        //THEN - the size should be two
        assertThat(loadedList, notNullValue())
        assertThat(loadedList.size, `is`(2))

        //WHEN - All reminders deleted
        database.reminderDao().deleteAllReminders()

        //THEN - get all reminders is empty
        val loadedEmptyList = database.reminderDao().getReminders()
        assertThat(loadedEmptyList, `is`(emptyList()))
    }
}
