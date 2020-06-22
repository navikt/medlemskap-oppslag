package no.nav.medlemskap.regler

import no.nav.medlemskap.common.objectMapper
import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.regler.common.Resultat
import no.nav.medlemskap.regler.common.Svar
import no.nav.medlemskap.regler.v1.Hovedregler
import org.junit.jupiter.api.Assertions

fun evaluer(datagrunnlag: Datagrunnlag): Resultat {
    val regelsett = Hovedregler(datagrunnlag)
    return regelsett.kjørHovedregler()
}

fun assertSvar(regelIdentifikator: String, forventetSvarFraRegel: Svar, resultat: Resultat, konklusjon: Svar) {
    println(objectMapper.writeValueAsString(resultat))
    val find = resultat.delresultat.find { it.identifikator == regelIdentifikator }

    Assertions.assertNotNull(find, "Fant ikke regel $regelIdentifikator i delsvar i Resultat. Regel det testes på ble ikke kjørt. Følgende regler ble kjørt: " + resultat.delresultat.map { it.identifikator })
    Assertions.assertEquals(forventetSvarFraRegel, find!!.svar,"Fikk feil svar regel: $regelIdentifikator")
    Assertions.assertEquals(konklusjon, resultat.svar)
}

fun assertDelresultat(regelIdentifikator: String, forventetSvarFraRegel: Svar, resultat: Resultat) {
    println(objectMapper.writeValueAsString(resultat))
    val find = resultat.delresultat.find { it.identifikator == regelIdentifikator }

    Assertions.assertNotNull(find, "Fant ikke regel $regelIdentifikator i delsvar i Resultat. Regel det testes på ble ikke kjørt. Følgende regler ble kjørt: " + resultat.delresultat.map { it.identifikator })
    Assertions.assertEquals(forventetSvarFraRegel, find!!.svar,"Fikk feil svar regel: $regelIdentifikator")
}

