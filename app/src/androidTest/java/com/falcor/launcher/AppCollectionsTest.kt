package com.falcor.launcher

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class AppCollectionsTest {

    private val context =
        InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun discoversInstalledTvApps() {
        val apps = AppCatalog(context).discover()
        val packages = apps.map { it.packageName }

        assertTrue(packages.contains("au.com.stan.and"))
        assertTrue(packages.contains("au.net.abc.iview"))
        assertTrue(packages.contains("org.jellyfin.androidtv"))

        assertFalse(packages.contains(context.packageName))
        assertEquals(packages.size, packages.distinct().size)
    }

    @Test
    fun collectionsPreserveConfiguration() {
        val name = "falcor_test_${UUID.randomUUID()}"

        try {
            val store = AppCollectionStore(context, name)

            val defaults = store.load(AppCollection.SPORT)

            assertEquals(2, defaults.size)

            val updated = defaults.reversed() +
                AppEntry("org.smarttube.stable", "SmartTube")

            store.save(AppCollection.SPORT, updated)

            val reopened = AppCollectionStore(context, name)

            assertEquals(
                updated,
                reopened.load(AppCollection.SPORT)
            )

            // An empty collection must remain empty.
            reopened.save(AppCollection.SPORT, emptyList())

            assertTrue(
                AppCollectionStore(context, name)
                    .load(AppCollection.SPORT)
                    .isEmpty()
            )

            // Editing Sport must not affect Free-to-Air.
            assertEquals(
                4,
                reopened.load(AppCollection.FREE_TO_AIR).size
            )

        } finally {
            context.deleteSharedPreferences(name)
        }
    }
}
