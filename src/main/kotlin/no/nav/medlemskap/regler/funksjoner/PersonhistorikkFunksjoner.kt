package no.nav.medlemskap.regler.funksjoner

import no.nav.medlemskap.domene.InputPeriode
import java.time.LocalDate

object PersonhistorikkFunksjoner {
    fun List<LocalDate?>.erBrukerDoedEtterPeriode(periode: InputPeriode): Boolean =
        this.stream().anyMatch { it?.isAfter(periode.fom)!! }
}
