package com.company.carryon.i18n

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

class LanguageCoverageTest {
    @Test
    fun supportsOnlyLaunchLanguages() {
        assertEquals(setOf("en", "ms", "ta", "zh"), SupportedLanguages.codes)
        assertEquals(4, SupportedLanguages.all.size)
    }

    @Test
    fun unsupportedLanguageFallsBackToEnglish() {
        assertEquals("en", SupportedLanguages.normalize("hi"))
        assertSame(EnStrings, getStringsForLanguage("hi"))
    }

    @Test
    fun everySupportedLanguageResolvesStrings() {
        SupportedLanguages.codes.forEach { code ->
            val strings = getStringsForLanguage(code)
            assertTrue(strings.appName.isNotBlank())
            assertTrue(strings.continueText.isNotBlank())
            assertTrue(strings.selectYourLanguage.isNotBlank())
            assertTrue(strings.languageSettingsDescription.isNotBlank())
            assertTrue(strings.globalConnectivityTitle.isNotBlank())
            assertTrue(strings.globalConnectivityDescription.isNotBlank())
            assertTrue(strings.profile.isNotBlank())
            assertTrue(strings.activeProfile.isNotBlank())
            assertTrue(strings.deliveryPreferences.isNotBlank())
            assertTrue(strings.defaultVehicle.isNotBlank())
            assertTrue(strings.dataAndSecurity.isNotBlank())
            assertTrue(strings.welcomeBack.isNotBlank())
            assertTrue(strings.sendAPackage.isNotBlank())
            assertTrue(strings.enterPickupLocation.isNotBlank())
            assertTrue(strings.recentDeliveries.isNotBlank())
            assertTrue(strings.personalInfo.isNotBlank())
            assertTrue(strings.paymentsAndWallet.isNotBlank())
            assertTrue(strings.privacyAndSecurity.isNotBlank())
        }
    }
}
