package no.nav.medlemskap.regler.v1

enum class Eøsland(val land: String) {
    BEL("BELGIA"),
    BGR("BULGARIA"),
    DNK("DANMARK"),
    EST("ESTLAND"),
    FIN("FINLAND"),
    FRA("FRANKRIKE"),
    GRC("HELLAS"),
    IRL("IRLAND"),
    ISL("ISLAND"),
    ITA("ITALIA"),
    HRV("KROATIA"),
    CYP("KYPROS"),
    LVA("LATVIA"),
    LIE("LIECHTENSTEIN"),
    LTU("LITAUEN"),
    LUX("LUXENBURG"),
    MLT("MALTA"),
    NLD("NEDERLAND"),
    NOR("NORGE"),
    POL("POLEN"),
    PRT("PORTUGAL"),
    ROU("ROMANIA"),
    SVK("SLOVAKIA"),
    SVN("SLOVENIA"),
    ESP("SPANIA"),
    SWE("SVERIGE"),
    CZE("TSJEKKIA"),
    DEU("TYSKAND"),
    HUN("UNGARN"),
    AUT("ØSTERRIKE");

    companion object {
        fun erEØSland(landkode: String): Boolean {
            return values().any {it.name == landkode}
        }
    }
}