package org.simdjson;

import java.io.IOException;
import java.io.InputStream;
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
public class TwitterBenchmarkV3 {

    private final SimdJsonParser simdJsonParser = new SimdJsonParser();
    private final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private byte[] buffer;

    @Param({"/twitter.json", "/twitter50.json", "/twitter1.json"})
    String fileName;

    @Setup(Level.Trial)
    public void setup() throws IOException {
        try (InputStream is = TwitterBenchmarkV3.class.getResourceAsStream(fileName)) {
            buffer = is.readAllBytes();
        }
    }

    // record
    @Benchmark
    public int recordSimdjson() {
        Set<Object> defaultUsers = new HashSet<>();
        TwitterRecord twitter = simdJsonParser.parse(buffer, buffer.length, TwitterRecord.class);
        for (StatusRecord status : twitter.statuses()) {
            long id = status.id();
            String text = status.text();
            defaultUsers.add(id);
            defaultUsers.add(text);
        }
        return defaultUsers.size();
    }

    @Benchmark
    public int JsonValueSimdjson() {
        JsonValue simdJsonValue = simdJsonParser.parse(buffer, buffer.length);
        Set<Object> defaultUsers = new HashSet<>();
        Iterator<JsonValue> tweets = simdJsonValue.get("statuses").arrayIterator();
        while (tweets.hasNext()) {
            JsonValue tweet = tweets.next();
            JsonValue id = tweet.get("id");
            JsonValue text = tweet.get("text");
            defaultUsers.add(id.asLong());
            defaultUsers.add(text.asString());
        }
        return defaultUsers.size();
    }

    @Benchmark
    public int recordJackson() throws IOException {
        Set<Object> defaultUsers = new HashSet<>();
        TwitterRecord twitter = objectMapper.readValue(buffer, TwitterRecord.class);
        for (StatusRecord status : twitter.statuses()) {
            long id = status.id();
            String text = status.text();
            defaultUsers.add(id);
            defaultUsers.add(text);
        }
        return defaultUsers.size();
    }

    record StatusRecord(long id, String text) {

    }

    record TwitterRecord(List<StatusRecord> statuses) {

    }
}
