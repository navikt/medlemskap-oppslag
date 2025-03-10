package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.common.Resultat
import no.nav.medlemskap.regler.common.Svar

import no.nav.medlemskap.regler.v1.brukergruppe.ReglerForBrukerGruppe
import no.nav.medlemskap.selvstendignaringsdrivende.HovedreglerForSelvstendigNaringsdrivende

object ReglerService {

    fun kjørRegler(datagrunnlag: Datagrunnlag): Resultat {
        return Hovedregler(datagrunnlag).kjørHovedregler()
    }

    fun kjørReglerForSelvstendigNæringsdrivende(datagrunnlag: Datagrunnlag,resultat: Resultat): Resultat {
        return HovedreglerForSelvstendigNaringsdrivende(datagrunnlag,resultat).kjørHovedregler()
    }

    fun kjørReglerv2(datagrunnlag: Datagrunnlag): Resultat {
        val harBrukerArbeidsforhold = ReglerForBrukerGruppe.fraDatagrunnlag(datagrunnlag).kjørHovedflyt()
        val arbeidstager = harBrukerArbeidsforhold.finnRegelResultat(RegelId.REGEL_17)!!.svar
        if (arbeidstager == Svar.JA) {
            return kjørRegler(datagrunnlag)
        }
        else{
            return kjørReglerForSelvstendigNæringsdrivende(datagrunnlag,harBrukerArbeidsforhold)
        }

    }
}
