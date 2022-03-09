package no.nav.medlemskap.cucumber.steps.udi

import no.nav.medlemskap.domene.OppholdstillaelsePaSammeVilkarType
import no.nav.medlemskap.domene.OppholdstillatelsePaSammeVilkar
import no.nav.medlemskap.domene.Periode
import no.nav.medlemskap.domene.SoknadIkkeAvgjort

class OppholdstillatelsePåSammeVilkårBuilder {
    var periodeBuilder = PeriodeBuilder()
    var periode: Periode? = periodeBuilder.build()
    var type: OppholdstillaelsePaSammeVilkarType? = OppholdstillaelsePaSammeVilkarType.PERMANENT
    var harTillatelse: Boolean? = true
    var soknadIkkeAvgjort: SoknadIkkeAvgjort = SoknadIkkeAvgjort(type, periode)

    fun build(): OppholdstillatelsePaSammeVilkar {
        return OppholdstillatelsePaSammeVilkar(
            periode,
            type,
            harTillatelse,
            soknadIkkeAvgjort
        )
    }
}
