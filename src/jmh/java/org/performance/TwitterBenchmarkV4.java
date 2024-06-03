package org.simdjson;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
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
public class TwitterBenchmarkV4 {

    private final SimdJsonParser simdJsonParser = new SimdJsonParser();
    private final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private byte[] buffer;

    @Param({"/twitter0.json"})
    String fileName;

    @Setup(Level.Trial)
    public void setup() throws IOException {
        try (InputStream is = TwitterBenchmarkV4.class.getResourceAsStream(fileName)) {
            buffer = is.readAllBytes();
        }
    }

    // record
    @Benchmark
    public int recordSimdjson() {
        Set<Object> defaultUsers = new HashSet<>();
        TwitterRecord twitter = simdJsonParser.parse(buffer, buffer.length, TwitterRecord.class);
        double completed_in = twitter.completed_in();
        long max_id = twitter.max_id();
        String max_id_str = twitter.max_id_str();
        String next_results = twitter.next_results();
        String query = twitter.query();
        String refresh_url = twitter.refresh_url();
        long count = twitter.count();
        long since_id = twitter.since_id();
        String since_id_str = twitter.since_id_str();
        defaultUsers.add(completed_in);
        defaultUsers.add(max_id);
        defaultUsers.add(max_id_str);
        defaultUsers.add(next_results);
        defaultUsers.add(query);
        defaultUsers.add(refresh_url);
        defaultUsers.add(count);
        defaultUsers.add(since_id);
        defaultUsers.add(since_id_str);
        return defaultUsers.size();
    }

    @Benchmark
    public int JsonValueSimdjson() {
        JsonValue simdJsonValue = simdJsonParser.parse(buffer, buffer.length);
        Set<Object> defaultUsers = new HashSet<>();
        JsonValue completed_in = simdJsonValue.get("completed_in");
        JsonValue max_id = simdJsonValue.get("max_id");
        JsonValue max_id_str = simdJsonValue.get("max_id_str");
        JsonValue next_results = simdJsonValue.get("next_results");
        JsonValue query = simdJsonValue.get("query");
        JsonValue refresh_url = simdJsonValue.get("refresh_url");
        JsonValue count = simdJsonValue.get("count");
        JsonValue since_id = simdJsonValue.get("since_id");
        JsonValue since_id_str = simdJsonValue.get("since_id_str");
        defaultUsers.add(completed_in.asDouble());
        defaultUsers.add(max_id.asLong());
        defaultUsers.add(max_id_str.asString());
        defaultUsers.add(next_results.asString());
        defaultUsers.add(query.asString());
        defaultUsers.add(refresh_url.asString());
        defaultUsers.add(count.asLong());
        defaultUsers.add(since_id.asLong());
        defaultUsers.add(since_id_str.asString());
        return defaultUsers.size();
    }

    @Benchmark
    public int recordJackson() throws IOException {
        Set<Object> defaultUsers = new HashSet<>();
        TwitterRecord twitter = objectMapper.readValue(buffer, TwitterRecord.class);
        double completed_in = twitter.completed_in();
        long max_id = twitter.max_id();
        String max_id_str = twitter.max_id_str();
        String next_results = twitter.next_results();
        String query = twitter.query();
        String refresh_url = twitter.refresh_url();
        long count = twitter.count();
        long since_id = twitter.since_id();
        String since_id_str = twitter.since_id_str();
        defaultUsers.add(completed_in);
        defaultUsers.add(max_id);
        defaultUsers.add(max_id_str);
        defaultUsers.add(next_results);
        defaultUsers.add(query);
        defaultUsers.add(refresh_url);
        defaultUsers.add(count);
        defaultUsers.add(since_id);
        defaultUsers.add(since_id_str);
        return defaultUsers.size();
    }

    record TwitterRecord(double completed_in, long max_id, String max_id_str, String next_results, String query, String refresh_url, long count, long since_id, String since_id_str) {
    }
}
