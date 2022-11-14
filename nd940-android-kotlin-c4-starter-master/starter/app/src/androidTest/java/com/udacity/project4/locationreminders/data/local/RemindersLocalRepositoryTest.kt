 package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.locationreminders.data.dto.succeeded
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsNull
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@MediumTest
class RemindersLocalRepositoryTest {

    // TODO: Add testing implementation to the RemindersLocalRepository.kt
    private lateinit var database: RemindersDatabase
    private lateinit var repository: RemindersLocalRepository
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private fun getReminder(): ReminderDTO {
        return ReminderDTO(
            title = "title",
            description = "desc",
            location = "loc",
            latitude = 47.5456551,
            longitude = 122.0101731)
    }

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).allowMainThreadQueries().build()

        repository = RemindersLocalRepository(database.reminderDao(), Dispatchers.Main)
    }

    @After
    fun cleanUp() {
        database.close()
    }
    @Test
    fun retrieveUnknownReminder() = runBlocking {
        //GIVEN - An Empty repository

        //WHEN - A Reminder is retrieved by id
        val result = repository.getReminder("Test1")

        //THEN - Error is returned with message
        assertThat(result.succeeded, `is`(false))
        result as Result.Error
        assertThat(result.message, `is`("Reminder not found!"))
    }

    @Test
    fun getAllReminders_deleteAllReminders() = runBlocking {
        //GIVEN - Two new reminders are saved in the repository
        val reminder = ReminderDTO("Titel1", "Description1", "Location1", 50.0, 100.0)
        val reminder2 = ReminderDTO("Titel2", "Description2", "Location2", 150.0, 200.0)
        repository.saveReminder(reminder)
        repository.saveReminder(reminder2)

        //WHEN all reminders are retrieved
        val remindersList = repository.getReminders()

        //THEN this size of returned list is 2
        assertThat(remindersList.succeeded, `is`(true))
        remindersList as Result.Success
        assertThat(remindersList.data, IsNull.notNullValue())
        assertThat(remindersList.data.size, `is`(2))

        //WHEN - all reminders are deleted
        repository.deleteAllReminders()

        //THEN - getAllReminders returns emptyList
        val remindersEmptyList = repository.getReminders()
        assertThat(remindersEmptyList.succeeded, `is`(true))
        remindersEmptyList as Result.Success
        assertThat(remindersEmptyList.data, `is`(emptyList()))
    }
    @Test
    fun saveReminder_retrievesReminder() = runBlocking {
        val reminder = getReminder()
        repository.saveReminder(reminder)
        val result = repository.getReminder(reminder.id)
        assertThat(result is Result.Success, `is`(true))
        result as Result.Success
        assertThat(result.data.title, `is`(reminder.title))
        assertThat(result.data.description, `is`(reminder.description))
        assertThat(result.data.latitude, `is`(reminder.latitude))
        assertThat(result.data.longitude, `is`(reminder.longitude))
        assertThat(result.data.location, `is`(reminder.location))
    }
}