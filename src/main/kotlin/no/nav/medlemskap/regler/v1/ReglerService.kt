package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.common.Resultat
import no.nav.medlemskap.regler.common.Svar

import no.nav.medlemskap.regler.v1.brukergruppe.ReglerForBrukerGruppe
import no.nav.medlemskap.selvstendignaringsdrivende.HovedreglerForSelvstendigNaringsdrivende

object ReglerService {

    fun kjørRegler(datagrunnlag: Datagrunnlag,resultat: Resultat? = null): Resultat {
        return Hovedregler(datagrunnlag,resultat).kjørHovedregler()
    }

    fun kjørReglerForSelvstendigNæringsdrivende(datagrunnlag: Datagrunnlag,resultat: Resultat): Resultat {
        return HovedreglerForSelvstendigNaringsdrivende(datagrunnlag,resultat).kjørHovedregler()
    }

    fun kjørReglerv2(datagrunnlag: Datagrunnlag): Resultat {
        val brukerGruppRegelkjøring = ReglerForBrukerGruppe.fraDatagrunnlag(datagrunnlag).kjørHovedflyt()
        when (avklarBrukerGruppe(brukerGruppRegelkjøring)){
            BrukerGruppe.Arbeidstager -> return kjørRegler(datagrunnlag,brukerGruppRegelkjøring)
            BrukerGruppe.Selvstendig_næringsdrivende -> return kjørReglerForSelvstendigNæringsdrivende(datagrunnlag,brukerGruppRegelkjøring)
            BrukerGruppe.Barn -> TODO()
            BrukerGruppe.Annet -> TODO()
        }

    }
    private fun avklarBrukerGruppe(resultat: Resultat): BrukerGruppe{
        if (resultat.finnRegelResultat(RegelId.REGEL_17)!!.svar == Svar.JA) {
            return BrukerGruppe.Arbeidstager
        }
        else{
            return BrukerGruppe.Selvstendig_næringsdrivende
        }

    }
    enum class BrukerGruppe{
        Arbeidstager,
        Selvstendig_næringsdrivende,
        Barn,
        Annet
    }
}
