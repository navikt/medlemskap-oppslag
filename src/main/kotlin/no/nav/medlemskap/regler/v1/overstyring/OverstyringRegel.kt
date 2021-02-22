package no.nav.medlemskap.regler.v1.overstyring

import no.nav.medlemskap.domene.Brukerinput
import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.domene.arbeidsforhold.Arbeidsforhold
import no.nav.medlemskap.domene.arbeidsforhold.Arbeidsforhold.Companion.erArbeidstaker
import no.nav.medlemskap.domene.personhistorikk.Statsborgerskap
import no.nav.medlemskap.domene.personhistorikk.Statsborgerskap.Companion.erNorskBorger
import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.common.Resultat
import no.nav.medlemskap.regler.common.Resultat.Companion.ja
import no.nav.medlemskap.regler.common.Resultat.Companion.nei
import no.nav.medlemskap.regler.common.Svar
import no.nav.medlemskap.regler.v1.lovvalg.LovvalgRegel
import java.time.LocalDate

class OverstyringRegel(
    ytelse: Ytelse,
    private val brukerInput: Brukerinput,
    startDatoForYtelse: LocalDate,
    private val statsborgerskap: List<Statsborgerskap>,
    private val arbeidsforhold: List<Arbeidsforhold>,
    regelId: RegelId = RegelId.REGEL_0_5
) : LovvalgRegel(regelId, ytelse, startDatoForYtelse) {

    override fun operasjon(): Resultat {
        if (!brukerInput.arbeidUtenforNorge &&
            ytelse == Ytelse.SYKEPENGER &&
            statsborgerskap.erNorskBorger(kontrollPeriodeForPersonhistorikk) &&
            arbeidsforhold.erArbeidstaker(kontrollPeriodeForArbeidsforhold)
        ) {
            return ja(regelId)
        }

        return nei(regelId)
    }

    companion object {

        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): OverstyringRegel {
            return OverstyringRegel(
                ytelse = datagrunnlag.ytelse,
                brukerInput = datagrunnlag.brukerinput,
                startDatoForYtelse = datagrunnlag.startDatoForYtelse,
                statsborgerskap = datagrunnlag.pdlpersonhistorikk.statsborgerskap,
                arbeidsforhold = datagrunnlag.arbeidsforhold
            )
        }

        fun reglerSomSkalOverstyres(): Map<RegelId, Svar> {
            val overstyringer = HashMap<RegelId, Svar>()

            overstyringer.put(RegelId.REGEL_3, Svar.JA)
            overstyringer.put(RegelId.REGEL_5, Svar.JA)
            overstyringer.put(RegelId.REGEL_12, Svar.JA)

            return overstyringer
        }
    }
}
