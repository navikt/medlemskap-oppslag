package no.nav.medlemskap.regler.funksjoner

import no.nav.medlemskap.domene.*
import no.nav.medlemskap.regler.common.interval
import no.nav.medlemskap.regler.common.lagInstantStartOfDay
import no.nav.medlemskap.regler.common.lagInterval
import no.nav.medlemskap.regler.funksjoner.AdresseFunksjoner.adressensPeriodeOverlapperKontrollPerioden
import org.threeten.extra.Interval
import java.time.LocalDate

object RelasjonFunksjoner {

    // Todo --> Hva er tom på Sivilstander? Bruke metadata?
    infix fun List<Personhistorikk>.hentEktefellerEllerPartnereIKontrollPeriode(kontrollPeriode: Periode): List<String> =
            this.filter {
                it.sivilstand == it.
            }.map { it.type }


    fun List<Familierelasjon>.hentFnrTilBarn(): List<String> =
            this.filter {
                it.minRolleForPerson == Familierelasjonsrolle.MOR ||
                        it.minRolleForPerson == Familierelasjonsrolle.FAR || it.minRolleForPerson == Familierelasjonsrolle.MEDMOR
            }.map { it.relatertPersonsIdent }

    private fun periodefilter(periodeDatagrunnlag: Interval, periode: Periode): Boolean {
        return periodeDatagrunnlag.overlaps(lagInterval(periode)) || periodeDatagrunnlag.encloses(lagInterval(periode))
    }
}