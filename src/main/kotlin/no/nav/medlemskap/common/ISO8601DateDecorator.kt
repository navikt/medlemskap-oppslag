package no.nav.medlemskap.common

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.util.StdDateFormat
import net.logstash.logback.decorate.JsonFactoryDecorator

class ISO8601DateDecorator : JsonFactoryDecorator {

    override fun decorate(factory: JsonFactory): JsonFactory {
        if (factory is ObjectMapper) {
//            factory.dateFormat = ISO8601DateFormat()
            factory.dateFormat = StdDateFormat()
        }

        return factory
    }
}
