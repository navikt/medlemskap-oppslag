package no.nav.medlemskap.services.medl
import no.nav.medlemskap.domene.Lovvalg
import no.nav.medlemskap.domene.Medlemskap
import no.nav.medlemskap.domene.PeriodeStatus

fun mapMedlemskapResultat(medlemskapsunntak: List<MedlMedlemskapsunntak>): List<Medlemskap> {
    return  medlemskapsunntak.map { Medlemskap(it.dekning, it.fraOgMed, it.tilOgMed, it.medlem, Lovvalg.from(it.lovvalg), it.lovvalgsland, PeriodeStatus.from(it.status)) }
}
