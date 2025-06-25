package com.beardytop.mitzmode.util

import io.sentry.Sentry
import io.sentry.SentryLevel

object SentryUtil {
    fun logError(error: Throwable, extras: Map<String, Any>? = null) {
        Sentry.withScope { scope ->
            extras?.forEach { (key, value) ->
                scope.setExtra(key, value.toString())
            }
            Sentry.captureException(error)
        }
    }

    fun logMessage(message: String, level: SentryLevel = SentryLevel.INFO) {
        Sentry.captureMessage(message, level)
    }

    fun setUser(userId: String) {
        Sentry.setUser(io.sentry.protocol.User().apply {
            this.id = userId
        })
    }

    fun clearUser() {
        Sentry.setUser(null)
    }
} 