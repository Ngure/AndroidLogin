package com.pesapata.agents.utils

import android.util.Log
import com.crashlytics.android.Crashlytics
import com.facebook.FacebookAuthorizationException
import com.pesapata.agents.BuildConfig
import java.net.ConnectException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLException

class LogUtils {
    companion object {
        private val MAX_LOG_TAG_LENGTH = 23

        fun makeLogTag(str: String): String {
            return if (str.length > MAX_LOG_TAG_LENGTH) {
                str.substring(0, MAX_LOG_TAG_LENGTH - 1)
            } else str

        }

        /**
         * Don't use this when obfuscating class names!
         */
        fun makeLogTag(cls: Class<*>): String {
            return makeLogTag(cls.simpleName)
        }

        fun LOGD(tag: String, message: String) {
            //noinspection PointlessBooleanExpression,ConstantConditions
            if (BuildConfig.DEBUG || Log.isLoggable(tag, Log.DEBUG)) {
                Log.d(tag, message)
            }
        }

        fun LOGD(tag: String, message: String, cause: Throwable) {
            //noinspection PointlessBooleanExpression,ConstantConditions
            if (BuildConfig.DEBUG || Log.isLoggable(tag, Log.DEBUG)) {
                Log.d(tag, message, cause)
            }
            sendToCrashlytics(cause)
        }

        fun LOGV(tag: String, message: String) {
            //noinspection PointlessBooleanExpression,ConstantConditions
            if (BuildConfig.DEBUG && Log.isLoggable(tag, Log.VERBOSE)) {
                Log.v(tag, message)
            }
        }

        fun LOGV(tag: String, message: String, cause: Throwable) {
            //noinspection PointlessBooleanExpression,ConstantConditions
            if (BuildConfig.DEBUG && Log.isLoggable(tag, Log.VERBOSE)) {
                Log.v(tag, message, cause)
            }
            sendToCrashlytics(cause)
        }

        fun LOGI(tag: String, message: String) {
            Log.i(tag, message)
        }

        fun LOGI(tag: String, message: String, cause: Throwable) {
            Log.i(tag, message, cause)
            sendToCrashlytics(cause)
        }

        fun LOGW(tag: String, message: String) {
            Log.w(tag, message)
        }

        fun LOGW(tag: String, message: String, cause: Throwable) {
            Log.w(tag, message, cause)
            sendToCrashlytics(cause)
        }

        fun LOGE(tag: String, message: String) {
            Log.e(tag, message)
        }

        fun LOGE(tag: String, message: String, cause: Throwable) {
            Log.e(tag, message, cause)
            sendToCrashlytics(cause)
        }

        private fun sendToCrashlytics(cause: Throwable) {
            if (BuildConfig.DEBUG) return  // do not send to FireBase when in debug mode

            // ignore some exceptions
            if (cause is SSLException // sometimes is a result of networking issues by the service provider

                || cause is UnknownHostException // result of sudden changes in DNS or the server pointed to was down for maintenance

                || cause is FacebookAuthorizationException
                || cause is SocketTimeoutException
                || cause is ConnectException || cause is SocketException
            ) {
                return
            }

            Crashlytics.logException(cause)
        }
    }
}