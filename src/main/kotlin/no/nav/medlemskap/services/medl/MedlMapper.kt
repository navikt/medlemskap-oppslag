package no.nav.medlemskap.services.medl
import no.nav.medlemskap.domene.Medlemskap


fun mapMedlemskapResultat(medlemskapsunntak: List<MedlMedlemskapsunntak>): List<Medlemskap> {
    return  medlemskapsunntak.map { Medlemskap(it.dekning, it.fraOgMed, it.tilOgMed, it.medlem, it.lovvalg, it.lovvalgsland, it.status) }
}
