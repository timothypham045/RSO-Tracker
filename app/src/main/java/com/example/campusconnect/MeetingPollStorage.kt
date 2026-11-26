package com.example.campusconnect

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

object MeetingPollStorage {

    private const val PREFS_NAME = "meeting_polls"
    private const val KEY = "created_polls"

    fun save(
        context: Context,
        polls: List<MeetingPoll>
    ) {
        val jsonArray = JSONArray()

        polls.forEach { poll ->
            val json = JSONObject().apply {
                put("id", poll.id)
                put("organizationId", poll.organizationId)
                put("title", poll.title)
                put("startDate", poll.startDate)
                put("endDate", poll.endDate)
                put("startHour", poll.startHour)
                put("endHour", poll.endHour)
            }

            jsonArray.put(json)
        }

        context.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        ).edit()
            .putString(KEY, jsonArray.toString())
            .apply()
    }

    fun load(context: Context): List<MeetingPoll> {
        val preferences = context.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )

        val saved = preferences.getString(KEY, "[]") ?: "[]"

        return try {
            val jsonArray = JSONArray(saved)
            val polls = mutableListOf<MeetingPoll>()

            for (i in 0 until jsonArray.length()) {
                val json = jsonArray.getJSONObject(i)

                polls.add(
                    MeetingPoll(
                        id = json.getString("id"),
                        organizationId =
                            json.getString("organizationId"),
                        title = json.getString("title"),
                        startDate = json.optString("startDate"),
                        endDate = json.optString("endDate"),
                        startHour = json.optInt("startHour", 16),
                        endHour = json.optInt("endHour", 20)
                    )
                )
            }

            polls
        } catch (e: Exception) {
            emptyList()
        }
    }
}
