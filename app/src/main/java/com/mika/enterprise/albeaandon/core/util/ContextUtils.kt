package com.mika.enterprise.albeaandon.core.util

import android.content.Context
import android.content.ContextWrapper
import android.os.LocaleList
import com.mika.enterprise.albeaandon.core.util.Constant.KEY_LANGUAGE
import com.mika.enterprise.albeaandon.core.util.Constant.PREFERENCES_FILE_KEY
import java.util.Locale

class ContextUtils(base: Context) : ContextWrapper(base) {

    companion object {
        fun updateLocale(context: Context, localeToSwitchTo: Locale): ContextUtils {
            val resources = context.resources
            val configuration = resources.configuration
            val localeList = LocaleList(localeToSwitchTo)
            LocaleList.setDefault(localeList)
            configuration.setLocales(localeList)
            val updatedContext = context.createConfigurationContext(configuration)
            return ContextUtils(updatedContext)
        }

        fun getSavedLanguagePreference(context: Context): String {
            val sharedPreferences = context.getSharedPreferences(PREFERENCES_FILE_KEY, Context.MODE_PRIVATE)
            return sharedPreferences.getString(KEY_LANGUAGE, "en").orEmpty()
        }

    }
}