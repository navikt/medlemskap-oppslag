package no.nav.medlemskap.regler.v1.arbeidsforhold

import no.nav.medlemskap.domene.InputPeriode
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.BasisRegel
import no.nav.medlemskap.regler.common.Datohjelper
import no.nav.medlemskap.regler.common.RegelId
import java.time.LocalDate

abstract class ArbeidsforholdRegel(
    regelId: RegelId,
    ytelse: Ytelse,
    periode: InputPeriode,
    førsteDagForYtelse: LocalDate?
) : BasisRegel(regelId, ytelse) {
    val kontrollPeriodeForArbeidsforhold = Datohjelper(periode, førsteDagForYtelse, ytelse).kontrollPeriodeForArbeidsforhold()
}
