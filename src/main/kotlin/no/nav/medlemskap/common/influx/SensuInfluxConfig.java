/**
 * Copyright (c) 2019 NAV
 * <p>
 * MIT License
 * <p>
 * This file incorporates works covered by the following copyright and permission notice:
 * ---
 * <p>
 * Copyright 2017 Pivotal Software, Inc.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package no.nav.medlemskap.common.influx;

import io.micrometer.core.instrument.step.StepRegistryConfig;

/**
 * Configuration for {@link SensuInfluxMeterRegistry}.
 * Modified from {@link io.micrometer.influx.InfluxConfig}.
 *
 * @see <a href="https://github.com/micrometer-metrics/micrometer/tree/v1.3.5/implementations/micrometer-registry-influx">https://github.com/micrometer-metrics/micrometer/tree/v1.3.5/implementations/micrometer-registry-influx</a>
 */
public interface SensuInfluxConfig extends StepRegistryConfig {
    /**
     * Accept configuration defaults
     */
    SensuInfluxConfig DEFAULT = k -> null;

    @Override
    default String prefix() {
        return "sensuinflux";
    }

    /**
     * @return The db to send metrics to. Defaults to "default".
     */
    default String db() {
        return "default";
    }

    /**
     * @return The hostname for the Sensu backend. The default is {@code sensu.nais}.
     */
    default String hostname() {
        return "sensu.nais";
    }

    /**
     * @return The port for the Sensu backend. The default is {@code 3030}.
     */
    default Integer port() {
        return 3030;
    }

    /**
     * @return The name of the metrics for the Sensu backend. Default is "test-events", but should be overriden.
     */
    default String sensuName() {
        return "test-events";
    }
}
