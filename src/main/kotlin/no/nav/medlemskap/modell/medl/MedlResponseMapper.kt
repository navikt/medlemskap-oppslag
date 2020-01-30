package no.nav.medlemskap.modell.medl
import no.nav.medlemskap.domene.Medlemskapsunntak


fun mapMedlemskapResultat(medlemskapsunntak: List<no.nav.medlemskap.modell.medl.Medlemskapsunntak>): List<Medlemskapsunntak> {
    return  medlemskapsunntak.map { Medlemskapsunntak(it.dekning, it.fraOgMed, it.tilOgMed, it.medlem, it.lovvalg, it.lovvalgsland) }

}
