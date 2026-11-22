package com.nuvio.tv.domain.model

data class UserProfile(
    val id: Int,
    val name: String,
    val avatarColorHex: String,
    val usesPrimaryAddons: Boolean = false,
    val usesPrimaryPlugins: Boolean = false,
    val avatarId: String? = null,
    val avatarUrl: String? = null,
    val profileBackgroundId: String? = null,
    val profileBackgroundUrl: String? = null,
    /**
     * When true, this profile's Continue Watching items are never published to the system
     * launcher (Android TV preview channel / Watch Next row on Google TV, Android TV home,
     * Projectivy, etc.). Device-local: not synced to the account.
     */
    val hideFromLauncherContinueWatching: Boolean = false
) {
    val isPrimary: Boolean get() = id == 1
}
