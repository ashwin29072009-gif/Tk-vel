package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.repository.TkVelRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("TK VEL", appName)
    }

    @Test
    fun `repository provides authentic products and combos`() {
        val repo = TkVelRepository.get()
        val products = repo.products.value
        val combos = repo.combos.value
        assertTrue("Products catalog should not be empty", products.isNotEmpty())
        assertTrue("Combos catalog should not be empty", combos.isNotEmpty())
    }

    @Test
    fun `validate coupon codes`() {
        val repo = TkVelRepository.get()
        val (success, msg) = repo.applyCoupon("WELCOME10")
        assertTrue(success)
        assertNotNull(repo.appliedCoupon.value)
    }

    @Test
    fun `validate shiprocket pin serviceability`() {
        val repo = TkVelRepository.get()
        val result = repo.checkPinServiceability("600017")
        assertTrue(result.isServiceable)
        assertEquals("Chennai", result.city)
        assertEquals("Tamil Nadu", result.state)
    }
}
