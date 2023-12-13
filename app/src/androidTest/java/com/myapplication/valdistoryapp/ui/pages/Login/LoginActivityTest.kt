package com.myapplication.valdistoryapp.ui.pages.Login

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.myapplication.valdistoryapp.R
import com.myapplication.valdistoryapp.data.remote.retrofit.ApiConfig
import com.myapplication.valdistoryapp.ui.pages.Login.utils.JsonConverter
import com.myapplication.valdistoryapp.ui.pages.Stories.MainActivity
import com.myapplication.valdistoryapp.utils.EspressoIdlingResource
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class LoginActivityTest {

    private val mockWebServer = MockWebServer()

    @get:Rule
    val activity = ActivityScenarioRule(LoginActivity::class.java)

    @Before
    fun setUp() {
        Intents.init()
        mockWebServer.start(8080)
        ApiConfig.BASE_URL = "http://127.0.0.1:8080/"
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun tearDown() {
        Intents.release()
        mockWebServer.shutdown()
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun login_Success() {
//        val mockResponse = MockResponse()
//            .setResponseCode(200)
//            .setBody(JsonConverter.readStringFromFile("login_success_response.json"))
//
//        mockWebServer.enqueue(mockResponse)

        onView(withId(R.id.ed_login_email)).check(matches(isDisplayed()))
        onView(withId(R.id.ed_login_email)).perform(
//            typeText("testemail@mail.com"),
            typeText("testdicoding@mail.com"),
            closeSoftKeyboard()
        )
        onView(withId(R.id.ed_login_password)).check(matches(isDisplayed()))
        onView(withId(R.id.ed_login_password)).perform(
//            typeText("notrealpassword"),
            typeText("dicoding123"),
            closeSoftKeyboard()
        )

        onView(withId(R.id.btn_sign_in)).check(matches(isEnabled()))
        onView(withId(R.id.btn_sign_in)).perform(click())

//        Intents.intended(IntentMatchers.hasComponent(MainActivity::class.java.name))
    }
}