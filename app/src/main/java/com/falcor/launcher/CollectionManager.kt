package com.falcor.launcher

import android.app.AlertDialog
import android.content.Context

internal class CollectionManager(
    private val context: Context,
    private val collection: AppCollection
) {
    private val store = AppCollectionStore(context)
    private val catalog = AppCatalog(context)

    private val title = when (collection) {
        AppCollection.SPORT -> "Manage Sport"
        AppCollection.FREE_TO_AIR -> "Manage Free-to-Air"
    }

    fun show() {
        val entries = store.load(collection)
        val installed = catalog.discover()
            .map { it.packageName }
            .toSet()

        val actions = mutableListOf<Pair<String, () -> Unit>>()

        actions.add("Add application" to { showPicker() })

        entries.forEach { entry ->
            val label = if (entry.packageName in installed) {
                entry.label
            } else {
                "${entry.label} (unavailable)"
            }

            actions.add(label to { showEntry(entry) })
        }

        showList(title, actions)
    }

    private fun showPicker() {
        val existing = store.load(collection)
        val existingPackages = existing
            .map { it.packageName }
            .toSet()

        val available = catalog.discover().filter {
            it.packageName !in existingPackages
        }

        if (available.isEmpty()) {
            AlertDialog.Builder(context)
                .setTitle("Add application")
                .setMessage("No additional TV applications found.")
                .setPositiveButton("Back") { _, _ -> show() }
                .setOnCancelListener { show() }
                .show()

            return
        }

        val duplicateLabels = available
            .groupingBy { it.label }
            .eachCount()

        val actions = available.map { entry ->
            val label = if (
                (duplicateLabels[entry.label] ?: 0) > 1
            ) {
                "${entry.label} (${entry.packageName})"
            } else {
                entry.label
            }

            label to {
                store.save(
                    collection,
                    store.load(collection) + entry
                )
                show()
            }
        }

        showList(
            title = "Add application",
            actions = actions,
            onBack = { show() }
        )
    }

    fun showEntry(
        entry: AppEntry,
        onDone: () -> Unit = { show() }
    ) {
        val entries = store.load(collection)
        val index = entries.indexOfFirst {
            it.packageName == entry.packageName
        }

        if (index == -1) {
            onDone()
            return
        }

        val actions = mutableListOf<Pair<String, () -> Unit>>()

        if (index > 0) {
            actions.add("Move earlier" to {
                move(index, index - 1, onDone)
            })
        }

        if (index < entries.lastIndex) {
            actions.add("Move later" to {
                move(index, index + 1, onDone)
            })
        }

        actions.add("Remove from collection" to {
            store.save(
                collection,
                entries.filterNot {
                    it.packageName == entry.packageName
                }
            )
            onDone()
        })

        showList(
            title = entry.label,
            actions = actions,
            onBack = onDone
        )
    }

    private fun move(
        from: Int,
        to: Int,
        onDone: () -> Unit
    ) {
        val entries = store.load(collection).toMutableList()

        if (from in entries.indices && to in entries.indices) {
            val entry = entries.removeAt(from)
            entries.add(to, entry)
            store.save(collection, entries)
        }

        onDone()
    }

    private fun showList(
        title: String,
        actions: List<Pair<String, () -> Unit>>,
        onBack: (() -> Unit)? = null
    ) {
        val builder = AlertDialog.Builder(context)
            .setTitle(title)
            .setItems(
                actions.map { it.first }.toTypedArray()
            ) { _, index ->
                actions[index].second.invoke()
            }
            .setNegativeButton(
                if (onBack == null) "Close" else "Back"
            ) { _, _ ->
                onBack?.invoke()
            }

        if (onBack != null) {
            builder.setOnCancelListener {
                onBack.invoke()
            }
        }

        builder.show()
    }
}
