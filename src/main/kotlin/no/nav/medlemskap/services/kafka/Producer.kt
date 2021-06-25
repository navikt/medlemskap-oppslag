package no.nav.medlemskap.services.kafka

import no.nav.medlemskap.config.Configuration
import org.apache.kafka.clients.CommonClientConfigs
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.common.config.SslConfigs
import java.util.*

class Producer {

    fun createProducer(producerConfig: Configuration.KafkaConfig): KafkaProducer<String, String> {
        val properties = Properties().apply {
            CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG to producerConfig.bootstrapServers
            CommonClientConfigs.SECURITY_PROTOCOL_CONFIG to producerConfig.securityProtocol
            CommonClientConfigs.CLIENT_ID_CONFIG to producerConfig.clientId
            SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG to producerConfig.trustStorePath
            SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG to producerConfig.keystorePassword
            SslConfigs.SSL_KEYSTORE_TYPE_CONFIG to producerConfig.keystoreType
            SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG to producerConfig.keystoreLocation
            SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG to producerConfig.keystorePassword
        }

        return KafkaProducer(properties)
    }
}