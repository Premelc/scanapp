package com.domelabs.scanapp.showcase

import kotlinx.serialization.Serializable

@Serializable
data object ShowcaseHub

@Serializable
data object ShowcaseButtons

@Serializable
data object ShowcaseTextFields

@Serializable
data object ShowcaseSearchBar

@Serializable
data object ShowcaseChips

@Serializable
data object ShowcaseCheckboxes

@Serializable
data object ShowcaseCards

@Serializable
data object ShowcaseCategoryCards

@Serializable
data object ShowcaseFab

@Serializable
data object ShowcaseIconBoxes

@Serializable
data object ShowcaseDashedBox

@Serializable
data object ShowcaseShadows

data class ShowcaseEntry(
    val title: String,
    val description: String,
    val route: Any,
)

val showcaseEntries = listOf(
    ShowcaseEntry("Buttons", "Primary, secondary, pastel & disabled", ShowcaseButtons),
    ShowcaseEntry("Text fields", "Single-line inputs & placeholders", ShowcaseTextFields),
    ShowcaseEntry("Search bar", "Combined field with search action", ShowcaseSearchBar),
    ShowcaseEntry("Chips", "Toggle chips like Heatmap / Radius", ShowcaseChips),
    ShowcaseEntry("Checkboxes", "Square bordered checkboxes", ShowcaseCheckboxes),
    ShowcaseEntry("Cards", "Content containers with hard shadow", ShowcaseCards),
    ShowcaseEntry("Category cards", "Icon, checkbox, label & count row", ShowcaseCategoryCards),
    ShowcaseEntry("FAB", "Floating action button", ShowcaseFab),
    ShowcaseEntry("Icon boxes", "Pastel icon containers", ShowcaseIconBoxes),
    ShowcaseEntry("Dashed box", "Empty / drop-zone state", ShowcaseDashedBox),
    ShowcaseEntry("Shadow modifier", "neoBrutalShadow offsets & colors", ShowcaseShadows),
)
