package no.nav.medlemskap.clients.udi

import no.nav.medlemskap.services.udi.UdiMapper
import no.udi.mt_1067_nav_data.v1.GjeldendeOppholdsstatus
import no.udi.mt_1067_nav_data.v1.HentPersonstatusResultat
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class UtvistMedInnreiseForbudNullTest {
    @Test
    fun `utvistMedInnreiseForbud er null`() {
        val utvistMedInnreiseforbudNull =
            HentPersonstatusResultat()
                .withGjeldendeOppholdsstatus(
                    GjeldendeOppholdsstatus()
                        .withIkkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisum(null)
                )

        val mappetUtvistMedInnreiseForbud = UdiMapper.mapTilOppholdstillatelse(utvistMedInnreiseforbudNull)
        val verdi = mappetUtvistMedInnreiseForbud
            .gjeldendeOppholdsstatus
            ?.ikkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisum
            ?.utvistMedInnreiseForbud

        assertNull(verdi)
    }
}
