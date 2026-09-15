package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.json.JSONArray
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.InputStreamReader

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Chef.M_K", appName)
  }

  @Test
  fun `verify all 1000 recipes have detailed multi-stage instructions`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val inputStream = context.assets.open("recipes.json")
    val reader = InputStreamReader(inputStream, Charsets.UTF_8)
    val jsonString = reader.readText()
    reader.close()
    inputStream.close()

    val jsonArray = JSONArray(jsonString)
    assertEquals("Must contain exactly 1000 recipes", 1000, jsonArray.length())

    for (i in 0 until jsonArray.length()) {
      val obj = jsonArray.getJSONObject(i)
      val uid = obj.getString("uniqueId")
      val steps = obj.getJSONArray("preparationSteps")
      assertTrue("Recipe $uid must have at least 6 steps", steps.length() >= 6)
      for (s in 0 until steps.length()) {
        val stepText = steps.getString(s)
        assertTrue("Step must be detailed in recipe $uid", stepText.length >= 50)
        assertTrue("Step must be numbered with مرحله in recipe $uid", stepText.contains("مرحله"))
      }
    }
  }
}
