import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public final class PassStore {
    private final Path file;
    private final Consumer<String> warningSink;

    public PassStore(Path file) {
        this(file, System.err::println);
    }

    public PassStore(Path file, Consumer<String> warningSink) {
        this.file = file;
        this.warningSink = warningSink;
    }

    public void add(PassRecord pass) throws IOException {
        if (find(pass.passId()) != null) {
            throw new IllegalArgumentException("Duplicate pass ID: " + pass.passId());
        }
        Path parent = file.toAbsolutePath().getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            writer.write(pass.toDelimitedLine());
            writer.newLine();
        }
    }

    public PassRecord find(String targetId) throws IOException {
        if (!Files.exists(file)) {
            return null;
        }
        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.isBlank() || line.startsWith("#")) {
                    continue;
                }
                try {
                    PassRecord pass = PassRecord.parse(line);
                    if (pass.passId().equals(targetId)) {
                        return pass;
                    }
                } catch (IllegalArgumentException error) {
                    warningSink.accept("Skipped damaged line " + lineNumber + ": " + error.getMessage());
                }
            }
        }
        return null;
    }

    public List<PassRecord> listByRoute(String routeNumber) throws IOException {
        List<PassRecord> matches = new ArrayList<>();
        if (!Files.exists(file)) {
            return matches;
        }
        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.isBlank() || line.startsWith("#")) {
                    continue;
                }
                try {
                    PassRecord pass = PassRecord.parse(line);
                    if (pass.routeNumber().equals(routeNumber)) {
                        matches.add(pass);
                    }
                } catch (IllegalArgumentException error) {
                    warningSink.accept("Skipped damaged line " + lineNumber + ": " + error.getMessage());
                }
            }
        }
        return matches;
    }
}
