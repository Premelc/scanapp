package com.domelabs.scanapp.feature.collections.impl.domain.error

/**
 * Typed error surface for collections operations. Use cases and repositories
 * throw or return these so presentation can react with inline UI feedback.
 */
sealed class CollectionsError(message: String) : Throwable(message) {

    /** A collection with the same name already exists (case-insensitive). */
    data object NameAlreadyExists :
        CollectionsError("A collection with that name already exists.")

    /** Attempted to rename, recolor, or delete the Unspecified collection. */
    data object CannotEditUnspecified :
        CollectionsError("The Unspecified collection cannot be modified.")

    /** The requested collection id does not exist. */
    data object CollectionNotFound :
        CollectionsError("Collection not found.")

    /** The requested item id does not exist. */
    data object ItemNotFound :
        CollectionsError("Item not found.")
}
