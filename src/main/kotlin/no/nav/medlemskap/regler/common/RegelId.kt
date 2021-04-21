package no.nav.medlemskap.regler.common

enum class RegelId(val identifikator: String, val avklaring: String, val neiBegrunnelse: String = "", val jaBegrunnelse: String = "", val uavklartBegrunnelse: String = "") {
    REGEL_0_1("0.1", "Er input-dato gyldig?", "Ugyldig input dato"),
    REGEL_0_5("0.5", "Skal regler overstyres?", "", "Svar på brukerspørsmålet er Nei, og regler 3, 5 og 12 skal derfor overstyres."),
    REGEL_1_1("1.1", "Er alle perioder siste 12 mnd avklart (endelig/gyldig)?", "Ikke alle perioder siste 12 mnd er avklart"),
    REGEL_1_2("1.2", "Er det periode både med og uten medlemskap innenfor 12 mnd?", "", "Fant periode både med og uten medlemskap"),
    REGEL_1_3("1.3", "Er det en periode med medlemskap?"),
    REGEL_1_3_1("1.3.1", "Er hele perioden uten medlemskap innenfor 12-måneders perioden?", "Deler av 12-måneders perioden har medlemskap"),
    REGEL_1_3_2("1.3.2", "Er bruker uten medlemskap sin situasjon uendret?", "Brukers arbeidsforhold er uendret"),
    REGEL_1_4("1.4", "Er hele perioden med medlemskap innenfor 12-måneders perioden?", "Det finnes hull i 12-måneders perioden for medlemskap"),
    REGEL_1_5("1.5", "Er brukers arbeidsforhold uendret?", "Brukers arbeidsforhold er uendret"),
    REGEL_1_6("1.6", "Er brukers dekning uavklart?", "Kan ikke avklare dekning til bruker"),
    REGEL_1_7("1.7", "Har bruker et medlemskap som omfatter ytelse? (Dekning i MEDL)", "Bruker har ikke dekning", "Bruker har dekning"),
    REGEL_2("2", "Er bruker omfattet av grunnforordningen (EØS)? Dvs er bruker statsborger i et EØS-land inkl. Norge?", "Brukeren er ikke statsborger i et EØS-land."),
    REGEL_3("3", "Har bruker hatt et sammenhengende arbeidsforhold i Aa-registeret de siste 12 månedene?", "Arbeidstaker har ikke sammenhengende arbeidsforhold siste 12 mnd"),
    REGEL_4("4", "Er foretaket registrert i foretaksregisteret?", "Ikke alle arbeidsgivere er av typen organisasjon"),
    REGEL_5("5", "Har arbeidsgiver sin hovedaktivitet i Norge?", "Ikke alle arbeidsgivere har 6 ansatte eller flere"),
    REGEL_6("6", "Er foretaket aktivt?", "Arbeidstaker har hatt arbeidsforhold til arbeidsgiver som har konkurs-status satt"),
    REGEL_7("7", "Er arbeidsforholdet maritimt?"),
    REGEL_7_1("7.1", "Er bruker ansatt på et NOR-skip eller NIS-skip med innenriks fartsområde?", "Bruker har maritimt arbeidsforhold, men ikke på et NOR-skip eller NIS-skip med innenriks fartsområde"),
    REGEL_8("8", "Er bruker pilot eller kabinansatt?", "", "Bruker er pilot eller kabinansatt"),
    REGEL_9("9", "Har bruker utført arbeid utenfor Norge?"),
    REGEL_10("10", "Er bruker folkeregistrert som bosatt i Norge og har vært det i 12 mnd?", "Ikke alle adressene til bruker er norske, eller bruker mangler bostedsadresse"),
    REGEL_11("11", "Er bruker norsk statsborger?", "Brukeren er ikke norsk statsborger"),
    REGEL_11_2("11.2", "Har bruker ektefelle i PDL?"),
    REGEL_11_2_1("11.2.1", "Har bruker barn i PDL?"),
    REGEL_11_2_2("11.2.2", "Er brukers barn folkeregistrert som bosatt i Norge?", "", "", "Brukers barn er ikke folkregistrert som bosatt I Norge"),
    REGEL_11_2_2_1("11.2.2.1", "Har bruker uten ektefelle og folkeregistrerte barn jobbet mer enn 100 prosent?", "Bruker har ikke jobbet 100% eller mer i løpet av periode."),
    REGEL_11_2_2_2("11.2.2.2", "Har bruker vært i 80 % stilling eller mer de siste 12 mnd?"),
    REGEL_11_2_3("11.2.3", "Har bruker vært i minst 80 % stilling de siste 12 mnd?", "Bruker har ikke jobbet 80% eller mer i løpet av periode."),
    REGEL_11_3("11.3", "Har bruker barn i PDL?"),
    REGEL_11_3_1("11.3.1", "Er brukers ektefelle folkeregistrert som bosatt i Norge?"),
    REGEL_11_3_1_1("11.3.1.1", "Har bruker vært i 100 % stilling eller mer de siste 12 mnd?", "Bruker har ikke jobbet 100% eller mer i løpet av periode."),
    REGEL_11_4("11.4", "Er brukers ektefelle folkeregistrert som bosatt i Norge?"),
    REGEL_11_4_1("11.4.1", "Er brukers barn folkeregistrert som bosatt i Norge?"),
    REGEL_11_4_2("11.4.2", "Har bruker vært i 100 % stilling eller mer de siste 12 mnd?", "Bruker har ikke jobbet 100% eller mer i løpet av periode."),
    REGEL_11_5("11.5", "Er brukers barn folkeregistrert som bosatt i Norge?", "", "", "Det er uavklart om brukers barn er folkeregistrert som bosatt i Norge"),
    REGEL_11_5_1("11.5.1", "Er brukers ektefelle og barnas mor/far samme person?", "Ektefelle er ikke barn/barnas foreldre"),
    REGEL_11_5_2("11.5.2", "Har bruker vært i 80 % stilling eller mer de siste 12 mnd?", "Bruker har ikke jobbet 80% eller mer i løpet av periode."),
    REGEL_11_5_3("11.5.3", "Har bruker vært i 100 % stilling eller mer de siste 12 mnd?", "Bruker har ikke jobbet 100% eller mer i løpet av periode."),
    REGEL_11_6("11.6", "Har bruker vært i minst 80 % stilling de siste 12 mnd?"),
    REGEL_11_6_1("11.6.1", "Har brukers ektefelle  vært i minst 100 % stilling de siste 12 mnd?", "Brukers ektefelle har ikke jobbet 100% eller mer i løpet av periode."),
    REGEL_12("12", "Har bruker vært i minst 25% stilling de siste 12 mnd?", "Bruker har ikke jobbet 25% eller mer i løpet av perioden."),
    REGEL_13("13", "Er bruker død?", "", "Bruker har dødsdato etter inputperiode, men det påvirker ikke medlemskapet", "Bruker er død, men i eller før inputperiode."),
    REGEL_14("14", "Er bruker ansatt i staten eller i en kommune?"),
    REGEL_17("17", "Har bruker arbeidsforhold?", "Bruker har ikke arbeidsforhold"),
    REGEL_17_1("17.1", "Er bruker frilanser?", "Bruker er ikke frilanser"),
    REGEL_18("18", "Er bruker i hovedsak arbeidstaker?", "Bruker er ikke arbeidstaker"),
    REGEL_19_1("19.1", "Er oppholdstillatelsen uavklart?", "", "UDI-tjenesten returnerte uavklart oppholdstillatelse, og må derfor behandles manuelt av UDI"),
    REGEL_19_2("19.2", "Har bruker to ulike oppholdstillatelser som overlapper?", "Bruker har ikke to oppholdstillatelser som overlapper", "Bruker har to oppholdstillatelser som overlapper"),
    REGEL_19_3("19.3", "Har bruker en gyldig oppholdstillatelse 12 måneder bakover og 2 måned fremover i tid?", "Bruker har ikke gyldig oppholdstillatelse"),
    REGEL_19_3_1("19.3.1", "Dekker oppholdstillatelsen arbeidsperioden bakover i tid?", "Oppholdstillatelsen dekker ikke arbeidsperioden"),
    REGEL_19_4("19.4", "Når brukers tillatelse er EØS eller EFTA; er brukers ektefelle statsborger i UK?", "brukers ektefelle er ikke statsborger i UK", "Brukers ektefelle er statsborger i UK"),
    REGEL_19_5("19.5", "Svarer UDI-tjenesten uavklart ift arbeidsadgang", "UDI-tjenesten svarer ikke uavklart", "UDI-tjenesten svarer uavklart"),
    REGEL_19_6("19.6", "Har bruker en gyldig arbeidstillatelse 12 måneder bakover og 2 måned fremover i tid?", "Bruker har ikke gyldig arbeidstillatelse"),
    REGEL_19_6_1("19.6.1", "Dekker arbeidstillatelsen arbeidsperioden bakover i tid?", "arbeidstillatelsen dekker ikke arbeidsperioden"),
    REGEL_19_7("19.7", "Er bruker britisk borger?", "", "Bruker er britisk borger"),
    REGEL_19_8("19.8", "Har bruker opphold på samme vilkår flagg", "Opphold på samme vilkår flagget er ikke satt", "Opphold på samme vilkår flagget er satt"),
    REGEL_20("20", "Har bruker mer enn 80% arbeid i Norge de siste 3 månedene?", "Bruker har ikke utført mer enn 80% arbeid de siste 3 månedene", "Bruker har utført mer enn 80% arbeid de siste 3 månedene"),
    REGEL_22("22", "Er det registrert utenlandsopphold i et arbeidsforhold bruker har vært i siste 12 måneder?", "Det er ikke registrert utenlandsopphold i arbeidsforholdet for siste 12 måneder", "Det er registrert utenlandsopphold i arbeidsforholdet for siste 12 måneder"),
    REGEL_OPPLYSNINGER("OPPLYSNINGER", "Finnes det registrerte opplysninger på bruker?", "Alle de følgende ble NEI"),
    REGEL_A("OPPLYSNINGER-MEDL", "Finnes det registrerte opplysninger i MEDL?", "", "Det finnes registrert opplysninger i Medl"),
    REGEL_B("OPPLYSNINGER-GOSYS", "Finnes det åpne oppgaver i GOSYS på medlemskapsområdet?", "", "Det finnes registrerte opplysninger i GOSYS"),
    REGEL_C("OPPLYSNINGER-JOARK", "Finnes det dokumenter i JOARK på medlemskapsområdet?", "", "Det finnes registrert opplysninger i JOARK"),
    REGEL_MEDLEM_KONKLUSJON("LOVME", "Er bruker medlem?", "Kan ikke konkludere med medlemskap", "Bruker er medlem", "Kan ikke konkludere med medlemskap"),
    REGEL_FLYT_KONKLUSJON("RFK", "Svar på regelflyt", "Regelflyt konkluderer med NEI"),
    REGEL_ARBEIDSFORHOLD("ARBEIDSFORHOLD", "Er arbeidsforhold avklart?"),
    REGEL_EØS_BOSATT("EØS-BOSATT", "Er EØS-borger bosatt i Norge?"),
    REGEL_ANDRE_BORGERE("ANDRE BORGERE", "Er regler for andre borgere avklart?"),
    REGEL_NORSK("NORSK", "Er regler for norske borgere avklart?"),
    REGEL_MEDL("MEDL", "Har bruker avklarte opplysninger i MEDL?"),
    REGEL_STATSBORGERSKAP("STATSBORGERSKAP", "Er statsborgerskap avklart?"),
    REGEL_BOSATT("BOSATT", "Er det avklart om bruker bor i Norge?"),
    REGEL_DOED("DOED", "Er det avklart om brukeren er død eller ikke?"),
    REGEL_REQUEST_VALIDERING("Validering", "Er input-dataene gyldige?"),
    REGEL_OVERSTYRING("OVERSTYRING", "Er overstyringsregler avklart?"),
    REGEL_FELLES_ARBEIDSFORHOLD("FELLES ARBEIDSFORHOLD", "Er felles arbeidsforhold avklart?"),
    REGEL_HOVEDSAKLIG_ARBEIDSTAKER("Hovedsaklig arbeidstaker", "Er arbeidstaker i hovedsak arbeidstaker?"),
    REGEL_OPPHOLDSTILLATELSE("Oppholdstillatelse", "Har bruker gyldig oppholdstillatelse?")
    ;

    fun begrunnelse(svar: Svar): String {
        return when (svar) {
            Svar.JA -> jaBegrunnelse
            Svar.NEI -> neiBegrunnelse
            Svar.UAVKLART -> uavklartBegrunnelse
        }
    }

    companion object {
        fun fraRegelIdString(regelIdStr: String): RegelId {

            return values().firstOrNull { it.identifikator == regelIdStr || it.name == regelIdStr }
                ?: throw RuntimeException("Fant ikke regelId $regelIdStr")
        }

        fun RegelId.metricName(): String = identifikator + ". " + avklaring.replace("?", "")
    }
}
