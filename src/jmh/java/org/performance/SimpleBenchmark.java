package org.simdjson;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
public class SimpleBenchmark {

    private final SimdJsonParser simdJsonParser = new SimdJsonParser();
    private final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private byte[] buffer;

    @Setup(Level.Trial)
    public void setup() throws IOException {
        String jsonString = "{\"value\": \"null\"}";
        buffer = jsonString.getBytes(StandardCharsets.UTF_8);
    }

    // record
    @Benchmark
    public int recordSimdjson() {
        Set<String> defaultUsers = new HashSet<>();
        SimpleRecord data = simdJsonParser.parse(buffer, buffer.length, SimpleRecord.class);
        defaultUsers.add(data.value());

        return defaultUsers.size();
    }

    @Benchmark
    public int JsonValueSimdjson() {
        JsonValue simdJsonValue = simdJsonParser.parse(buffer, buffer.length);
        Set<String> defaultUsers = new HashSet<>();
        defaultUsers.add(simdJsonValue.get("value").asString());

        return defaultUsers.size();
    }

    @Benchmark
    public int recordJackson() throws IOException {
        Set<String> defaultUsers = new HashSet<>();
        SimpleRecord data = objectMapper.readValue(buffer, SimpleRecord.class);
        defaultUsers.add(data.value());

        return defaultUsers.size();
    }

    record SimpleRecord(String value) {

    }
}
