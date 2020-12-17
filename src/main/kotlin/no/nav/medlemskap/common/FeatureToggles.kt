package no.nav.medlemskap.common

enum class FeatureToggles(val enabled: Boolean = false, val description: String = "") {
    FEATURE_UDI(false, "Kan ikke enables før vi får til å kalle UDI-tjenesten")
}
