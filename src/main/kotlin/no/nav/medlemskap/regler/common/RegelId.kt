package no.nav.medlemskap.regler.common

enum class RegelId(val identifikator: String, val avklaring: String) {
    REGEL_1_1("1.1", "Er alle perioder siste 12 mnd avklart (endelig/gyldig)?"),
    REGEL_1_2("1.2", "Er det periode både med og uten medlemskap innenfor 12 mnd?"),
    REGEL_1_3("1.3", "Er det en periode med medlemskap?"),
    REGEL_1_3_1("1.3.1","Er hele perioden uten medlemskap innenfor 12-måneders perioden?"),
    REGEL_1_3_2("1.3.2", "Er bruker uten medlemskap sin situasjon uendret?"),
    REGEL_1_4("1.4", "Er hele perioden med medlemskap innenfor 12-måneders perioden?"),
    REGEL_1_5("1.5", "Er brukers arbeidsforhold uendret?"),
    REGEL_1_6("1.6", "Er brukers dekning uavklart?"),
    REGEL_1_7("1.7", "Har bruker et medlemskap som omfatter ytelse? (Dekning i MEDL)"),
    REGEL_2("2", "Er bruker omfattet av grunnforordningen (EØS)? Dvs er bruker statsborger i et EØS-land inkl. Norge?"),
    REGEL_3("3", "Har bruker hatt et sammenhengende arbeidsforhold i Aa-registeret de siste 12 månedene?"),
    REGEL_4("4", "Er foretaket registrert i foretaksregisteret?"),
    REGEL_5("5", "Har arbeidsgiver sin hovedaktivitet i Norge?"),
    REGEL_6("6", "Er foretaket aktivt?"),
    REGEL_7("7", "Er arbeidsforholdet maritimt?"),
    REGEL_7_1("7.1", "Er bruker ansatt på et NOR-skip?"),
    REGEL_8("8", "Er bruker pilot eller kabinansatt?"),
    REGEL_9("9", "Har bruker utført arbeid utenfor Norge?"),
    REGEL_10("10", "Er bruker folkeregistrert som bosatt i Norge og har vært det i 12 mnd?"),
    REGEL_11("11", "Er bruker norsk statsborger?"),
    REGEL_11_2("11.2", "Har bruker ektefelle i TPS/PDL?"),
    REGEL_11_2_1("11.2.1", "Har bruker barn i TPS/PDL?"),
    REGEL_11_2_2("11.2.2", "Er brukers barn folkeregistrert som bosatt i Norge?"),
    REGEL_11_2_2_1("11.2.2.1", "Har bruker uten ektefelle og folkeregistrerte barn jobbet mer enn 100 prosent?"),
    REGEL_11_2_3("11.2.3", "Har bruker vært i minst 80 % stilling de siste 12 mnd?"),
    REGEL_11_3("11.3", "Har bruker barn i TPS/PDL?"),
    REGEL_11_3_1("11.3.1", "Er brukers ektefelle folkeregistrert som bosatt i Norge?"),
    REGEL_11_3_1_1("11.3.1.1", "Har bruker vært i 100 % stilling eller mer de siste 12 mnd?"),
    REGEL_11_4("11.4", "Er brukers ektefelle folkeregistrert som bosatt i Norge?"),
    REGEL_11_4_1("11.4.1", "Er brukers barn folkeregistrert som bosatt i Norge?"),
    REGEL_11_5("11.5", "Er brukers barn folkeregistrert som bosatt i Norge?"),
    REGEL_11_6("11.6", "Har bruker vært i minst 80 % stilling de siste 12 mnd?"),
    REGEL_12("12", "Har bruker vært i minst 25% stilling de siste 12 mnd?"),
    REGEL_A("A","Finnes det noe på personen i MEDL?"),
    REGEL_B("B", "Finnes det åpne oppgaver i GOSYS på medlemskapsområdet?"),
    REGEL_OPPLYSNINGER("OPPLYSNINGER", "Finnes det registrerte opplysninger på bruker?"),
    REGEL_OPPLYSNINGER_MEDL("OPPLYSNINGER-MEDL", "Finnes det registrerte opplysninger i MEDL?"),
    REGEL_OPPLYSNINGER_JOARK("OPPLYSNINGER-JOARK", "Finnes det dokumenter i JOARK på medlemskapsområdet?"),
    REGEL_OPPLYSNINGER_GOSYS("OPPLYSNINGER-GOSYS", "Finnes det åpne oppgaver i GOSYS på medlemskapsområdet?"),
    REGEL_MEDLEM_KONKLUSJON("LOVME", "Er bruker medlem?")
    ;

    companion object {
        fun fraRegelIdString(regelIdStr: String): RegelId? {
            return values().first { it.identifikator == regelIdStr }
        }
    }
}