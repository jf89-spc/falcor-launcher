package com.falcor.launcher

import android.content.Context
import android.content.Intent
import org.json.JSONArray
import org.json.JSONObject

internal data class AppEntry(
    val packageName: String,
    val label: String
)

internal enum class AppCollection(val key: String) {
    SPORT("sport"),
    FREE_TO_AIR("free_to_air")
}

internal class AppCatalog(context: Context) {

    private val appContext = context.applicationContext
    private val packageManager = appContext.packageManager

    @Suppress("DEPRECATION")
    fun discover(): List<AppEntry> {
        val intent = Intent(Intent.ACTION_MAIN)
            .addCategory(Intent.CATEGORY_LEANBACK_LAUNCHER)

        return packageManager.queryIntentActivities(intent, 0)
            .mapNotNull { result ->
                val activity = result.activityInfo
                    ?: return@mapNotNull null

                if (
                    activity.packageName == appContext.packageName ||
                    !activity.exported ||
                    !activity.enabled
                ) {
                    return@mapNotNull null
                }

                AppEntry(
                    packageName = activity.packageName,
                    label = activity.applicationInfo
                        .loadLabel(packageManager)
                        .toString()
                )
            }
            .distinctBy { it.packageName }
            .sortedBy { it.label.lowercase() }
    }
}

internal class AppCollectionStore(
    context: Context,
    preferencesName: String = "falcor_app_collections"
) {
    private val preferences = context.applicationContext
        .getSharedPreferences(
            preferencesName,
            Context.MODE_PRIVATE
        )

    fun load(collection: AppCollection): List<AppEntry> {
        val saved = preferences.getString(
            collection.key,
            null
        ) ?: return defaults(collection)

        val array = JSONArray(saved)

        return List(array.length()) { index ->
            val item = array.getJSONObject(index)

            AppEntry(
                packageName = item.getString("packageName"),
                label = item.getString("label")
            )
        }
    }

    fun save(
        collection: AppCollection,
        entries: List<AppEntry>
    ) {
        val array = JSONArray()

        entries.distinctBy { it.packageName }.forEach { entry ->
            array.put(
                JSONObject()
                    .put("packageName", entry.packageName)
                    .put("label", entry.label)
            )
        }

        preferences.edit()
            .putString(collection.key, array.toString())
            .apply()
    }

    private fun defaults(
        collection: AppCollection
    ): List<AppEntry> = when (collection) {

        AppCollection.SPORT -> listOf(
            AppEntry("au.com.stan.and", "Stan Sport"),
            AppEntry("com.nbaimd.gametime.nba2011", "NBA")
        )

        AppCollection.FREE_TO_AIR -> listOf(
            AppEntry("au.net.abc.iview", "ABC iview"),
            AppEntry("com.sbs.ondemand.tv", "SBS On Demand"),
            AppEntry("com.swm.live", "7plus"),
            AppEntry("au.com.ninenow.ctv", "9Now")
        )
    }
}
