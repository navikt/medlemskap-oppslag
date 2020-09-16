package no.nav.medlemskap.regler.common

class PdlMapperTest {

    private val inputMedTomFørFomIJson =
        """
        {
          "data": {
            "hentPerson": {
              "navn": [
                {
                  "fornavn": "Ola",
                  "mellomnavn": null,
                  "etternavn": "Normann"
                }
              ]
            }
          }
        }
        """.trimIndent()
}
