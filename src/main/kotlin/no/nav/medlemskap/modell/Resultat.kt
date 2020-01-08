package no.nav.medlemskap.modell

import no.nav.medlemskap.domene.Personhistorikk
import no.nav.medlemskap.domene.Regelavklaring
import no.nav.nare.core.evaluations.Evaluering

data class Resultat(val datagrunnlag: Regelavklaring, val evaluering: Evaluering)
