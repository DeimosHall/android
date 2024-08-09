/*
 * Nextcloud - Android Client
 *
 * SPDX-FileCopyrightText: 2020 Tobias Kaminsky <tobias@kaminsky.me>
 * SPDX-FileCopyrightText: 2020 Nextcloud GmbH
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package com.owncloud.android.ui.trashbin

import android.accounts.Account
import android.accounts.AccountManager
import android.content.Intent
import androidx.test.core.app.launchActivity
import androidx.test.espresso.IdlingRegistry
import com.nextcloud.utils.EspressoIdlingResource
import com.owncloud.android.AbstractIT
import com.owncloud.android.MainApp
import com.owncloud.android.lib.common.accounts.AccountUtils
import com.owncloud.android.utils.ScreenshotTest
import org.junit.After
import org.junit.Before
import org.junit.Test

class TrashbinActivityIT : AbstractIT() {
    private val testClassName = "com.owncloud.android.ui.trashbin.TrashbinActivityIT"

    enum class TestCase {
        ERROR, EMPTY, FILES
    }

    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    @ScreenshotTest
    fun error() {
        launchActivity<TrashbinActivity>().use { scenario ->
            scenario.onActivity { sut ->
                val trashbinRepository = TrashbinLocalRepository(TestCase.ERROR)
                sut.trashbinPresenter = TrashbinPresenter(trashbinRepository, sut)
                onIdleSync {
                    sut.runOnUiThread { sut.loadFolder() }
                    val screenShotName = createName(testClassName + "_" + "error", "")
                    screenshotViaName(sut, screenShotName)
                }
            }
        }
    }

    @Test
    @ScreenshotTest
    fun files() {
        launchActivity<TrashbinActivity>().use { scenario ->
            scenario.onActivity { sut ->
                val trashbinRepository = TrashbinLocalRepository(TestCase.FILES)
                onIdleSync {
                    sut.trashbinPresenter = TrashbinPresenter(trashbinRepository, sut)
                    sut.runOnUiThread { sut.loadFolder() }
                    val screenShotName = createName(testClassName + "_" + "files", "")
                    screenshotViaName(sut, screenShotName)
                }
            }
        }
    }

    @Test
    @ScreenshotTest
    fun empty() {
        launchActivity<TrashbinActivity>().use { scenario ->
            scenario.onActivity { sut ->
                val trashbinRepository = TrashbinLocalRepository(TestCase.EMPTY)
                sut.trashbinPresenter = TrashbinPresenter(trashbinRepository, sut)
                onIdleSync {
                    sut.runOnUiThread { sut.loadFolder() }
                    val screenShotName = createName(testClassName + "_" + "empty", "")
                    screenshotViaName(sut, screenShotName)
                }
            }
        }
    }

    @Test
    @ScreenshotTest
    fun loading() {
        launchActivity<TrashbinActivity>().use { scenario ->
            scenario.onActivity { sut ->
                val trashbinRepository = TrashbinLocalRepository(TestCase.EMPTY)
                sut.trashbinPresenter = TrashbinPresenter(trashbinRepository, sut)
                onIdleSync {
                    sut.runOnUiThread { sut.showInitialLoading() }
                    val screenShotName = createName(testClassName + "_" + "loading", "")
                    screenshotViaName(sut, screenShotName)
                }
            }
        }
    }

    @Test
    @ScreenshotTest
    fun normalUser() {
        launchActivity<TrashbinActivity>().use { scenario ->
            scenario.onActivity { sut ->
                val trashbinRepository = TrashbinLocalRepository(TestCase.EMPTY)
                sut.trashbinPresenter = TrashbinPresenter(trashbinRepository, sut)
                onIdleSync {
                    sut.runOnUiThread { sut.showUser() }
                    val screenShotName = createName(testClassName + "_" + "normalUser", "")
                    screenshotViaName(sut, screenShotName)
                }
            }
        }
    }

    @Test
    @ScreenshotTest
    fun differentUser() {
        val temp = Account("differentUser@https://nextcloud.localhost", MainApp.getAccountType(targetContext))

        AccountManager.get(targetContext).apply {
            addAccountExplicitly(temp, "password", null)
            setUserData(temp, AccountUtils.Constants.KEY_OC_BASE_URL, "https://nextcloud.localhost")
            setUserData(temp, AccountUtils.Constants.KEY_USER_ID, "differentUser")
        }

        val intent = Intent(targetContext, TrashbinActivity::class.java).apply {
            putExtra(Intent.EXTRA_USER, "differentUser@https://nextcloud.localhost")
        }

        launchActivity<TrashbinActivity>(intent).use { scenario ->
            scenario.onActivity { sut ->
                val trashbinRepository = TrashbinLocalRepository(TestCase.EMPTY)
                sut.trashbinPresenter = TrashbinPresenter(trashbinRepository, sut)
                onIdleSync {
                    sut.runOnUiThread { sut.showUser() }
                    val screenShotName = createName(testClassName + "_" + "differentUser", "")
                    screenshotViaName(sut, screenShotName)
                }
            }
        }
    }
}
