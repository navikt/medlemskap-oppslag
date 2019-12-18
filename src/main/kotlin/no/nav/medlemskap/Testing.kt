package no.nav.medlemskap

import java.net.URI

fun main() {
    val uri = URI("http://webproxy.no:8080")
    println("${uri.host} ${uri.fragment}")
}
