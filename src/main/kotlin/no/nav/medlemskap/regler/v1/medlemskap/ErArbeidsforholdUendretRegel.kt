package no.nav.medlemskap.regler.v1.medlemskap

import no.nav.medlemskap.common.regelUendretCounterMidlertidig
import no.nav.medlemskap.domene.*
import no.nav.medlemskap.regler.common.Datohjelper
import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.common.Resultat
import no.nav.medlemskap.regler.common.Resultat.Companion.ja
import no.nav.medlemskap.regler.common.Resultat.Companion.nei
import no.nav.medlemskap.regler.common.Svar
import no.nav.medlemskap.regler.funksjoner.ArbeidsforholdFunksjoner.arbeidsforholdForDato
import no.nav.medlemskap.regler.funksjoner.MedlFunksjoner.tidligsteFraOgMedDatoForMedl
import java.time.LocalDate

class ErArbeidsforholdUendretRegel(
    ytelse: Ytelse,
    private val periode: InputPeriode,
    private val førsteDagForYtelse: LocalDate?,
    private val medlemskap: List<Medlemskap>,
    private val arbeidsforhold: List<Arbeidsforhold>,
    regelId: RegelId
) : MedlemskapRegel(regelId, ytelse, periode, førsteDagForYtelse, medlemskap) {
    private val datohjelper = Datohjelper(periode, førsteDagForYtelse, ytelse)

    override fun operasjon(): Resultat {
        return erBrukersArbeidsforholdUendret(regelId)
    }

    private fun erBrukersArbeidsforholdUendret(regelId: RegelId): Resultat {
        if (harSammeArbeidsforholdSidenFomDatoFraMedl()) {
            regelUendretCounterMidlertidig(regelId, Svar.JA, ytelse).increment()
            return ja()
        } else {
            regelUendretCounterMidlertidig(regelId, Svar.NEI, ytelse).increment()
            return nei()
        }
        /* Tas inn igjen når man ikke lenger trenger egen Grafana-counter for denne (når dekning kan gis i response)
        when {
            harSammeArbeidsforholdSidenFomDatoFraMedl() -> ja()
            else -> nei()
        }
         */
    }

    private fun harSammeArbeidsforholdSidenFomDatoFraMedl(): Boolean =
        arbeidsforhold.arbeidsforholdForDato(medlemskap.tidligsteFraOgMedDatoForMedl(kontrollPeriodeForMedl)).isNotEmpty() &&
            (
                ulikeArbeidsforholdMenSammeArbeidsgiver() ||
                    arbeidsforhold.arbeidsforholdForDato(medlemskap.tidligsteFraOgMedDatoForMedl(kontrollPeriodeForMedl)) ==
                    arbeidsforhold.arbeidsforholdForDato(datohjelper.tilOgMedDag().plusDays(1))
                )

    private fun ulikeArbeidsforholdMenSammeArbeidsgiver() =
        (
            arbeidsforhold.arbeidsforholdForDato(medlemskap.tidligsteFraOgMedDatoForMedl(kontrollPeriodeForMedl)) != arbeidsforhold.arbeidsforholdForDato(datohjelper.tilOgMedDag()) &&
                arbeidsforhold.arbeidsforholdForDato(medlemskap.tidligsteFraOgMedDatoForMedl(kontrollPeriodeForMedl)).map { it.arbeidsgiver.organisasjonsnummer } ==
                arbeidsforhold.arbeidsforholdForDato(datohjelper.tilOgMedDag().plusDays(1)).map { it.arbeidsgiver.organisasjonsnummer }
            )

    companion object {

        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag, regelId: RegelId): ErArbeidsforholdUendretRegel {
            return ErArbeidsforholdUendretRegel(
                ytelse = datagrunnlag.ytelse,
                periode = datagrunnlag.periode,
                førsteDagForYtelse = datagrunnlag.førsteDagForYtelse,
                medlemskap = datagrunnlag.medlemskap,
                arbeidsforhold = datagrunnlag.arbeidsforhold,
                regelId = regelId
            )
        }
    }
}
