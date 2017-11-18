package com.morhpt.dertist.help

import java.sql.Timestamp
import java.util.*

/**
 * Created by nbura on 2017-11-17.
 */


data class User (
        val displayName: String? = null,
        val photoUrl: String? = null,
        val email: String? = null,
        val country: String? = null,
        val lastSeen: Date? = null
)

data class Dert (
        val dert: String? = null,
        val title: String? = null,
        val user: String? = null,
        val timestamp: Date? = null,
        val to: String? = null,
        val country: String? = null
)

data class Smoke (
        val user: String? = null,
        val timestamp: Date? = null
)

data class Answer (
        val answer: String? = null,
        val dert: String? = null,
        val user: String? = null
)