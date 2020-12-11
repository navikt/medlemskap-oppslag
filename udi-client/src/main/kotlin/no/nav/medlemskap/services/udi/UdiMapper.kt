package no.nav.medlemskap.services.udi


import no.udi.mt_1067_nav_data.v1.HentPersonstatusResultat

object UdiMapper {

    fun mapTilOppholdstillatelser(oppholdstillatelse: HentPersonstatusResultat): Oppholdstillatelse? {

        //   return Oppholdstillatelse()
        // TODO: 11/12/2020
        return null
    }
}


data class Oppholdstillatelse(
        val fnr : String
)
