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

import io.micrometer.core.instrument.*;
import io.micrometer.core.instrument.step.StepMeterRegistry;
import io.micrometer.core.instrument.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

/**
 * MeterRegistry for Influx by way of Sensu
 * Modified from {@link io.micrometer.influx.InfluxMeterRegistry}.
 *
 * @see <a href="https://github.com/micrometer-metrics/micrometer/tree/v1.3.5/implementations/micrometer-registry-influx">https://github.com/micrometer-metrics/micrometer/tree/v1.3.5/implementations/micrometer-registry-influx</a>
 */
public class SensuInfluxMeterRegistry extends StepMeterRegistry {
    private static final ThreadFactory DEFAULT_THREAD_FACTORY = new NamedThreadFactory("sensu-influx-metrics-publisher");
    private final SensuInfluxConfig config;
    private final Logger logger = LoggerFactory.getLogger(SensuInfluxMeterRegistry.class);

    public SensuInfluxMeterRegistry(SensuInfluxConfig config, Clock clock) {
        this(config, clock, DEFAULT_THREAD_FACTORY);
    }

    private SensuInfluxMeterRegistry(SensuInfluxConfig config, Clock clock, ThreadFactory threadFactory) {
        super(config, clock);
        config().namingConvention(new SensuInfluxNamingConvention());
        this.config = config;
        start(threadFactory);
    }

    public static Builder builder(SensuInfluxConfig config) {
        return new Builder(config);
    }

    @Override
    public void start(ThreadFactory threadFactory) {
        if (config.enabled()) {
            logger.info("publishing metrics to influx every " + TimeUtils.format(config.step()));
        }
        super.start(threadFactory);
    }

    @Override
    protected void publish() {
        try {
            String hostname = config.hostname();
            int port = config.port();
            String sensuName = config.sensuName();

            for (List<Meter> batch : MeterPartition.partition(this, config.batchSize())) {

                String influxLineProtocolData = batch.stream()
                        .flatMap(m -> m.match(
                                gauge -> writeGauge(gauge.getId(), gauge.value()),
                                counter -> writeCounter(counter.getId(), counter.count()),
                                this::writeTimer,
                                this::writeSummary,
                                this::writeLongTaskTimer,
                                gauge -> writeGauge(gauge.getId(), gauge.value(getBaseTimeUnit())),
                                counter -> writeCounter(counter.getId(), counter.count()),
                                this::writeFunctionTimer,
                                this::writeMeter))
                        .collect(joining("\n"));
                SensuEvent data = new SensuEvent(sensuName, influxLineProtocolData);

                long startTime = System.currentTimeMillis();
                try (Socket socket = new Socket(hostname, port)) {

                    try (OutputStreamWriter writer = new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8)) {
                        writer.write(data.getJson(), 0, data.getJson().length());
                        writer.flush();
                        logger.debug("wrote {} bytes of data", data.getJson().length());
                    } catch (IOException e) {
                        logger.error("Unable to send event {}", data, e);
                    }

                } catch (UnknownHostException e) {
                    logger.error("Unknown host: {}:{} {}", hostname, port, e.getMessage());
                } catch (IOException e) {
                    logger.error("Unable to send event to {}:{}", hostname, port, e);
                } catch (Exception e) {
                    logger.error("Unable to send event", e);
                } finally {
                    logger.info("Influx/sensu reporting time: {}", System.currentTimeMillis() - startTime);
                }
            }
        } catch (Throwable e) {
            logger.error("failed to send metrics to influx", e);
        }
    }

    // VisibleForTesting
    Stream<String> writeMeter(Meter m) {
        List<Field> fields = new ArrayList<>();
        for (Measurement measurement : m.measure()) {
            double value = measurement.getValue();
            if (!Double.isFinite(value)) {
                continue;
            }
            String fieldKey = measurement.getStatistic().getTagValueRepresentation()
                    .replaceAll("(.)(\\p{Upper})", "$1_$2").toLowerCase();
            fields.add(new Field(fieldKey, value));
        }
        if (fields.isEmpty()) {
            return Stream.empty();
        }
        Meter.Id id = m.getId();
        return Stream.of(influxLineProtocol(id, id.getType().name().toLowerCase(), fields.stream()));
    }

    private Stream<String> writeLongTaskTimer(LongTaskTimer timer) {
        Stream<Field> fields = Stream.of(
                new Field("active_tasks", timer.activeTasks()),
                new Field("duration", timer.duration(getBaseTimeUnit()))
        );
        return Stream.of(influxLineProtocol(timer.getId(), "long_task_timer", fields));
    }

    // VisibleForTesting
    Stream<String> writeCounter(Meter.Id id, double count) {
        if (Double.isFinite(count)) {
            return Stream.of(influxLineProtocol(id, "counter", Stream.of(new Field("value", count))));
        }
        return Stream.empty();
    }

    // VisibleForTesting
    Stream<String> writeGauge(Meter.Id id, Double value) {
        if (Double.isFinite(value)) {
            return Stream.of(influxLineProtocol(id, "gauge", Stream.of(new Field("value", value))));
        }
        return Stream.empty();
    }

    private Stream<String> writeFunctionTimer(FunctionTimer timer) {
        Stream<Field> fields = Stream.of(
                new Field("sum", timer.totalTime(getBaseTimeUnit())),
                new Field("count", timer.count()),
                new Field("mean", timer.mean(getBaseTimeUnit()))
        );

        return Stream.of(influxLineProtocol(timer.getId(), "histogram", fields));
    }

    private Stream<String> writeTimer(Timer timer) {
        final Stream<Field> fields = Stream.of(
                new Field("sum", timer.totalTime(getBaseTimeUnit())),
                new Field("count", timer.count()),
                new Field("mean", timer.mean(getBaseTimeUnit())),
                new Field("upper", timer.max(getBaseTimeUnit()))
        );

        return Stream.of(influxLineProtocol(timer.getId(), "histogram", fields));
    }

    private Stream<String> writeSummary(DistributionSummary summary) {
        final Stream<Field> fields = Stream.of(
                new Field("sum", summary.totalAmount()),
                new Field("count", summary.count()),
                new Field("mean", summary.mean()),
                new Field("upper", summary.max())
        );

        return Stream.of(influxLineProtocol(summary.getId(), "histogram", fields));
    }

    private String influxLineProtocol(Meter.Id id, String metricType, Stream<Field> fields) {
        String tags = getConventionTags(id).stream()
                .filter(t -> StringUtils.isNotBlank(t.getValue()))
                .map(t -> "," + t.getKey() + "=" + t.getValue())
                .collect(joining(""));

        return getConventionName(id)
                + tags + ",metric_type=" + metricType + " "
                + fields.map(Field::toString).collect(joining(","))
                + " " + clock.wallTime();
    }

    @Override
    protected final TimeUnit getBaseTimeUnit() {
        return TimeUnit.MILLISECONDS;
    }

    public static class Builder {
        private final SensuInfluxConfig config;

        private Clock clock = Clock.SYSTEM;
        private ThreadFactory threadFactory = DEFAULT_THREAD_FACTORY;

        @SuppressWarnings("deprecation")
        Builder(SensuInfluxConfig config) {
            this.config = config;
        }

        public Builder clock(Clock clock) {
            this.clock = clock;
            return this;
        }

        public Builder threadFactory(ThreadFactory threadFactory) {
            this.threadFactory = threadFactory;
            return this;
        }

        public SensuInfluxMeterRegistry build() {
            return new SensuInfluxMeterRegistry(config, clock, threadFactory);
        }
    }

    class Field {
        final String key;
        final double value;

        Field(String key, double value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String toString() {
            return key + "=" + DoubleFormat.decimalOrNan(value);
        }
    }

    static class SensuEvent {

        private final String json;

        SensuEvent(String sensuName, String output) {
            json = "{" +
                    "\"name\":\"" + sensuName + "\"," +
                    "\"type\":\"metric\"," +
                    "\"handlers\":[\"events_nano\"]," +
                    "\"output\":\"" + output + "\"," +
                    "\"status\":0" +
                    "}";
        }

        String getJson() {
            return json;
        }
    }

}
