package com.example.campusconnect

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

object EventStorage {

    private const val EVENTS_FILE = "rso_events.json"
    private const val SAVED_FILE = "saved_events.json"

    fun saveEvents(
        context: Context,
        events: List<RSOEvent>
    ) {
        writeEvents(context, EVENTS_FILE, events)
    }

    fun loadEvents(context: Context): List<RSOEvent>? {
        if (!context.getFileStreamPath(EVENTS_FILE).exists()) {
            return null
        }

        return readEvents(context, EVENTS_FILE)
    }

    fun saveBookmarks(
        context: Context,
        events: List<RSOEvent>
    ) {
        writeEvents(context, SAVED_FILE, events)
    }

    fun loadBookmarks(context: Context): List<RSOEvent> {
        if (!context.getFileStreamPath(SAVED_FILE).exists()) {
            return emptyList()
        }

        return readEvents(context, SAVED_FILE)
    }

    private fun writeEvents(
        context: Context,
        fileName: String,
        events: List<RSOEvent>
    ) {
        val jsonArray = JSONArray()

        events.forEach { event ->
            val json = JSONObject().apply {
                put("name", event.name)
                put("organization", event.organization)
                put("date", event.date)
                put("time", event.time)
                put("location", event.location)
                put("category", event.category)
            }

            jsonArray.put(json)
        }

        context.openFileOutput(
            fileName,
            Context.MODE_PRIVATE
        ).bufferedWriter().use {
            it.write(jsonArray.toString())
        }
    }

    private fun readEvents(
        context: Context,
        fileName: String
    ): List<RSOEvent> {

        val jsonString = context.openFileInput(fileName)
            .bufferedReader().use { it.readText() }

        val jsonArray = JSONArray(jsonString)
        val events = mutableListOf<RSOEvent>()

        for (i in 0 until jsonArray.length()) {
            val json = jsonArray.getJSONObject(i)

            events.add(
                RSOEvent(
                    name = json.getString("name"),
                    organization = json.getString("organization"),
                    date = json.getString("date"),
                    time = json.getString("time"),
                    location = json.getString("location"),
                    category = json.getString("category")
                )
            )
        }

        return events
    }
}
