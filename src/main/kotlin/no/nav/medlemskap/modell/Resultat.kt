package no.nav.medlemskap.modell

import no.nav.medlemskap.domene.Personhistorikk
import no.nav.nare.core.evaluations.Evaluering

data class Resultat(val datagrunnlag: Personhistorikk, val evaluering: Evaluering)
