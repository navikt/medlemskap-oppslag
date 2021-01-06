package no.nav.medlemskap.domene.arbeidsforhold

enum class PermisjonPermitteringType(val kodeverdi: String) {
    PERMISJON("permisjon"),
    PERMISJON_MED_FORELDREPENGER("permisjonMedForeldrepenger"),
    PERMISJON_VED_MILITAERTJENESTE("permisjonVedMilitaertjeneste"),
    PERMITTERING("permittering"),
    UTDANNINGSPERMISJON("utdanningspermisjon"),
    VELFERDSPERMISJON("velferdspermisjon"),
    ANNET("Annet")
    ;

    companion object {
        fun fraPermisjonPermitteringVerdi(permisjonPermittering: String): PermisjonPermitteringType {
            return PermisjonPermitteringType.values().first { it.kodeverdi == permisjonPermittering }
        }
    }
}
