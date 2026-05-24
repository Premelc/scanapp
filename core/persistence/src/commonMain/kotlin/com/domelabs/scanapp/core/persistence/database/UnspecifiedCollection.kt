package com.domelabs.scanapp.core.persistence.database

/**
 * The system-managed default collection that holds items scanned without an
 * explicit destination collection.
 *
 * Seeded on first database creation by [UnspecifiedCollectionSeedCallback] and
 * referenced by repositories that need to fall back to "uncategorized".
 *
 * This collection cannot be renamed, recolored, or deleted from any UI flow.
 */
object UnspecifiedCollection {
    const val ID: Long = 1L
    const val NAME: String = "Unspecified"
    const val COLOR_HEX: String = "#D9D9D9"
}
