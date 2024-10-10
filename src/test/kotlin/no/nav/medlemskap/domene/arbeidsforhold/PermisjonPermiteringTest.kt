package no.nav.medlemskap.domene.arbeidsforhold

import no.nav.medlemskap.clients.aareg.Entitet
import no.nav.medlemskap.domene.Periode
import no.nav.medlemskap.regler.v1.arbeidsforhold.HarPermisjonSiste12Måneder
import no.nav.medlemskap.regler.v1.arbeidsforhold.finnOverlappendePerioder
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.time.LocalDate

class PermisjonPermiteringTest {

    @Test
    fun finnOverlappendePerioderTest_medLikKontrollperiodeSomPermisjonspermittering() {
        val kontrollperiode = Periode(fom = LocalDate.now(), tom = LocalDate.now())
        val permisjonPermittering = PermisjonPermittering(
            Periode(fom = LocalDate.now(),
                tom = LocalDate.now()),
                permisjonPermitteringId = "",
                prosent = 1.0,
                PermisjonPermitteringType.PERMISJON,
                varslingskode = "",
            )
        val funnet = finnOverlappendePerioder(mutableListOf(permisjonPermittering), kontrollperiode)
        Assertions.assertTrue(funnet.isNotEmpty())
    }

    @Test
    fun finnOverlappendePerioderTest_DerPermisjonpermitteringErForKontrollperiode() {
        val kontrollperiode = Periode(fom = LocalDate.now(), tom = LocalDate.now())
        val permisjonPermittering = PermisjonPermittering(
            Periode(fom = LocalDate.now().minusYears(1),
                tom = LocalDate.now().minusDays(1)),
            permisjonPermitteringId = "",
            prosent = 1.0,
            PermisjonPermitteringType.PERMISJON,
            varslingskode = "",
        )
        val funnet = finnOverlappendePerioder(mutableListOf(permisjonPermittering), kontrollperiode)
        Assertions.assertTrue(funnet.isEmpty())
    }
    /*Sjekker overlappende eller samme dato*/
    @Test
    fun finnOverlappendePerioderTest_DerPermisjonpermitteringStarterForKontrollperiodeOgEnderI() {
        val kontrollperiode = Periode(fom = LocalDate.now(), tom = LocalDate.now())
        val permisjonPermittering = PermisjonPermittering(
            Periode(fom = LocalDate.now().minusYears(1),
                tom = LocalDate.now()),
            permisjonPermitteringId = "",
            prosent = 1.0,
            PermisjonPermitteringType.PERMISJON,
            varslingskode = "",
        )
        val funnet = finnOverlappendePerioder(mutableListOf(permisjonPermittering), kontrollperiode)
        Assertions.assertTrue(funnet.isNotEmpty())
    }

    @Test
    fun finnOverlappendePerioderTest_DerPermisjonpermitteringStarterForKontrollperiodeOgEnderEtter() {
        val kontrollperiode = Periode(fom = LocalDate.now(), tom = LocalDate.now())
        val permisjonPermittering = PermisjonPermittering(
            Periode(fom = LocalDate.now().minusYears(1),
                tom = LocalDate.now().plusYears(1)),
            permisjonPermitteringId = "",
            prosent = 1.0,
            PermisjonPermitteringType.PERMISJON,
            varslingskode = "",
        )
        val funnet = finnOverlappendePerioder(mutableListOf(permisjonPermittering), kontrollperiode)
        Assertions.assertTrue(funnet.isNotEmpty())
    }

}