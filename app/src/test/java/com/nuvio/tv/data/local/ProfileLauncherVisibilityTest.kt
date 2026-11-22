package com.nuvio.tv.data.local

import android.content.Context
import com.nuvio.tv.core.profile.ProfileManager
import com.nuvio.tv.domain.model.UserProfile
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ProfileLauncherVisibilityTest {

    private fun profile(id: Int, hidden: Boolean = false) = UserProfile(
        id = id,
        name = "Profile $id",
        avatarColorHex = "#1E88E5",
        hideFromLauncherContinueWatching = hidden
    )

    @Test
    fun `remote profile list keeps the device-local launcher flag`() {
        val local = listOf(profile(1), profile(2, hidden = true))
        val remote = listOf(
            profile(1).copy(name = "Renamed"),
            profile(2).copy(name = "Late night"),
            profile(3)
        )

        val merged = mergeDeviceLocalProfileFlags(remote, local)

        assertEquals(listOf("Renamed", "Late night", "Profile 3"), merged.map { it.name })
        assertFalse(merged[0].hideFromLauncherContinueWatching)
        assertTrue(merged[1].hideFromLauncherContinueWatching)
        assertFalse(merged[2].hideFromLauncherContinueWatching)
    }

    @Test
    fun `profile json round-trips the launcher flag`() {
        val json = ProfileJson.fromDomain(profile(2, hidden = true))
        assertTrue(json.toDomain().hideFromLauncherContinueWatching)
    }

    @Test
    fun `active profile flag follows profile switches`() = runTest {
        val profiles = MutableStateFlow(listOf(profile(1), profile(2, hidden = true)))
        val activeId = MutableStateFlow(1)
        val store = mockk<ProfileDataStore>(relaxed = true) {
            every { profilesList } returns profiles
            every { activeProfileId } returns activeId
            every { hasEverSelectedProfile } returns MutableStateFlow(true)
            every { rememberLastProfileEnabled } returns MutableStateFlow(false)
            every { confirmExitEnabled } returns MutableStateFlow(false)
            every { startupSplashEnabled } returns MutableStateFlow(true)
        }
        val manager = ProfileManager(store, mockk(relaxed = true), emptySet(), mockk<Context>(relaxed = true))

        assertFalse(manager.isLauncherContinueWatchingHiddenForActiveProfile())
        activeId.value = 2
        assertTrue(manager.isLauncherContinueWatchingHiddenForActiveProfile())
    }
}
