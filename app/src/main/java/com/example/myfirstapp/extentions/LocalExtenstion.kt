package com.example.myfirstapp.extentions

import android.annotation.TargetApi
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.LocaleList
import android.preference.PreferenceManager
import java.util.*
object LocaleHelper {

    private const val SELECTED_LANGUAGE = "Locale.Helper.Selected.Language"

    fun onAttach(context: Context): Context {
        val lang = getPersistedData(context, Locale.getDefault().language)
        return setLocale(context, lang)
    }

    fun onAttach(context: Context, defaultLanguage: String): Context {
        val lang = getPersistedData(context, defaultLanguage)
        return setLocale(context, lang)
    }

    fun getLanguage(context: Context): String? {
        return getPersistedData(context, Locale.getDefault().language)
    }

    fun setLocale(context: Context, language: String?): Context {
        persist(context, language)

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            updateResources(context, language)
        } else updateResourcesLegacy(context, language)

    }

    private fun getPersistedData(context: Context, defaultLanguage: String): String? {
        val preferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getString(SELECTED_LANGUAGE, defaultLanguage)
    }

    private fun persist(context: Context, language: String?) {
        val preferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
        val editor = preferences.edit()

        editor.putString(SELECTED_LANGUAGE, language)
        editor.apply()
    }

    @TargetApi(Build.VERSION_CODES.N)
    private fun updateResources(context: Context, language: String?): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val resources = context.resources
        val configuration = resources.configuration
//        val configuration: Configuration = context.resources.configuration
        configuration.setLocale(locale)
        configuration.setLayoutDirection(locale)
        resources.updateConfiguration(configuration, resources.displayMetrics)
        return context.createConfigurationContext(configuration)
    }


    @Suppress("DEPRECATION")
    private fun updateResourcesLegacy(context: Context, language: String?): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val resources = context.resources
        val configuration = resources.configuration
        configuration.locale = locale
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLayoutDirection(locale)
        }
        resources.updateConfiguration(configuration, resources.displayMetrics)
        context.createConfigurationContext(configuration)
        return context
    }

    fun getLocalizedText(context: Context, resourceId: Int): String? {
        val savedLanguage = getLanguage(context) ?: return null
        val requestedLocale = Locale(savedLanguage)
        val config = Configuration(context.resources.configuration)
        config.setLocale(requestedLocale)
        return context.createConfigurationContext(config).getText(resourceId).toString()
    }

    fun getLocalizedString(context: Context, resourceId: Int): String? {
        val savedLanguage = getLanguage(context) ?: return null
        val requestedLocale = Locale(savedLanguage)
        val config = Configuration(context.resources.configuration)
        config.setLocale(requestedLocale)
        return context.createConfigurationContext(config).getString(resourceId)
    }

    fun getLocalizedString(context: Context, resourceId: Int, vararg formatArgs: Any): String? {
        val savedLanguage = getLanguage(context) ?: return null
        val requestedLocale = Locale(savedLanguage)
        val config = Configuration(context.resources.configuration)
        config.setLocale(requestedLocale)
        return context.createConfigurationContext(config).getString(resourceId, formatArgs)
    }

    fun getLocalizedResources(context: Context): Resources? {
        val savedLanguage = getLanguage(context) ?: return null
        val requestedLocale = Locale(savedLanguage)
        val config = Configuration(context.resources.configuration)
        config.setLocale(requestedLocale)
        return context.createConfigurationContext(config).resources
    }

    /**
    override fun attachBaseContext(newBase: Context?) {
    super.attachBaseContext(newBase)
    val config = Configuration()
    applyOverrideConfiguration(config)
    }
    override fun applyOverrideConfiguration(overrideConfiguration: Configuration?) {
    overrideConfiguration?.apply {
    val oldUIMode = uiMode
    setTo(baseContext.resources.configuration)
    uiMode = oldUIMode
    }
    super.applyOverrideConfiguration(overrideConfiguration?.let { updateConfigurationIfSupported(it) })
    }
     * @param context Context
     * @param config Configuration
     * @param defaultLanguage String
     * @return Configuration?
     */
    fun updateConfigurationIfSupported(context: Context, config: Configuration, defaultLanguage: String): Configuration? {
        config.setLocale(Locale.forLanguageTag(getLanguage(context) ?: defaultLanguage))

        // Configuration.getLocales is added after 24 and Configuration.locale is deprecated in 24
        if (Build.VERSION.SDK_INT >= 24) {
            if (!config.locales.isEmpty) {
                return config
            }
        } else {
            if (config.locale != null) {
                return config
            }
        }
        return config
    }

}