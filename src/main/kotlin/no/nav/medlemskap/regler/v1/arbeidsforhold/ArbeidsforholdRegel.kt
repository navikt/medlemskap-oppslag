package no.nav.medlemskap.regler.v1.arbeidsforhold

import no.nav.medlemskap.domene.InputPeriode
import no.nav.medlemskap.domene.Kontrollperiode.Companion.kontrollPeriodeForArbeidsforhold
import no.nav.medlemskap.domene.Kontrollperiode.Companion.startDatoForYtelse
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.BasisRegel
import no.nav.medlemskap.regler.common.RegelId
import java.time.LocalDate

abstract class ArbeidsforholdRegel(
    regelId: RegelId,
    ytelse: Ytelse,
    periode: InputPeriode,
    førsteDagForYtelse: LocalDate?
) : BasisRegel(regelId, ytelse) {
    val kontrollPeriodeForArbeidsforhold = kontrollPeriodeForArbeidsforhold(startDatoForYtelse(periode, førsteDagForYtelse))
}
