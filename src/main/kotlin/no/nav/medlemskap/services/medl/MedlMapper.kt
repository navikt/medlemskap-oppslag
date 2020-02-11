package no.nav.medlemskap.services.medl
import no.nav.medlemskap.domene.Medlemskapsunntak


fun mapMedlemskapResultat(medlemskapsunntak: List<MedlMedlemskapsunntak>): List<Medlemskapsunntak> {
    return  medlemskapsunntak.map { Medlemskapsunntak(it.dekning, it.fraOgMed, it.tilOgMed, it.medlem, it.lovvalg, it.lovvalgsland) }

}
