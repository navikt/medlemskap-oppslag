package no.nav.medlemskap.routes

fun gyldigFnr(fnr: String): Boolean {
    if (fnr.length != 11) {
        return false
    }

    val s = fnr.toCharArray().map { it.toString().toInt() }
    var k1 = 11 - ((3 * s[0] + 7 * s[1] + 6 * s[2] + 1 * s[3] + 8 * s[4] + 9 * s[5] + 4 * s[6] + 5 * s[7] + 2 * s[8]) % 11)
    var k2 = 11 - ((5 * s[0] + 4 * s[1] + 3 * s[2] + 2 * s[3] + 7 * s[4] + 6 * s[5] + 5 * s[6] + 4 * s[7] + 3 * s[8] + 2 * k1) % 11)

    if (k1 == 11) k1 = 0
    if (k2 == 11) k2 = 0

    return k1 < 10 && k2 < 10 && k1 == s[9] && k2 == s[10]
}
