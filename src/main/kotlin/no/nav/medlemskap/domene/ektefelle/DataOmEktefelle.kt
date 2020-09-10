package no.nav.medlemskap.domene.ektefelle

import no.nav.medlemskap.domene.Arbeidsforhold

data class DataOmEktefelle(
        val personhistorikkEktefelle: PersonhistorikkEktefelle,
        val arbeidsforholdEktefelle: List<Arbeidsforhold>
)