package no.nav.medlemskap.domene

import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.common.Svar
import java.time.LocalDate

data class Request(
    val fnr: String,
    val førsteDagForYtelse: LocalDate?,
    val periode: InputPeriode,
    val brukerinput: Brukerinput,
    val ytelse: Ytelse?,
    val overstyrteRegler: Map<RegelId, Svar> = mapOf()
)

data class RequestV2(
    val fnr: String,
    val førsteDagForYtelse: LocalDate?,
    val periode: InputPeriode,
    val brukerinput: Brukerinput,
    val ytelse: Ytelse?,
    val vurderingID:String,
    val soknadID:String,
    val brukerGruppe:String = "Arbeidstager",
    val overstyrteRegler: Map<RegelId, Svar> = mapOf()
)

data class Brukerinput(
    val arbeidUtenforNorge: Boolean,
    val oppholdstilatelse: Oppholdstilatelse? = null,
    val utfortAarbeidUtenforNorge: UtfortAarbeidUtenforNorge? = null,
    val oppholdUtenforEos: OppholdUtenforEos? = null,
    val oppholdUtenforNorge: OppholdUtenforNorge? = null
)
data class Oppholdstilatelse(
    val id: String,
    val sporsmalstekst: String?,
    val svar: Boolean,
    val vedtaksdato: LocalDate,
    val vedtaksTypePermanent: Boolean,
    val perioder: List<Periode> = mutableListOf()
)
data class UtfortAarbeidUtenforNorge(
    val id: String,
    val sporsmalstekst: String?,
    val svar: Boolean,
    val arbeidUtenforNorge: List<ArbeidUtenforNorge>
)
data class ArbeidUtenforNorge(
    val id: String,
    val arbeidsgiver: String,
    val land: String,
    val perioder: List<Periode>
)
data class OppholdUtenforNorge(
    val id: String,
    val sporsmalstekst: String?,
    val svar: Boolean,
    val oppholdUtenforNorge: List<Opphold>
)

data class OppholdUtenforEos(
    val id: String,
    val sporsmalstekst: String?,
    val svar: Boolean,
    val oppholdUtenforEOS: List<Opphold>
)

data class Opphold(
    val id: String,
    val land: String,
    val grunn: String,
    val perioder: List<Periode>
)

data class InputPeriode(
    val fom: LocalDate,
    val tom: LocalDate
)
