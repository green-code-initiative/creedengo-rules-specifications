package org.greencodeinitiative.mavenbuild.ruleexporter.domain;

import java.nio.file.Path;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Optional.empty;
import static java.util.Optional.of;

public class RuleDescriptionFile {

    /**
     * Resources to include
     */
    private static final Pattern TARGET_RESOURCES = Pattern.compile(
            "^.{1,1000}/(?<ruleKey>GCI\\d{1,50})/(?<language>[^/]{1,50})/GCI\\d{1,50}\\.html$"
    );

    public static Optional<RuleDescriptionFile> createFromPath(String path) {
        final Matcher matcher = TARGET_RESOURCES.matcher(path.replace('\\', '/'));
        if (!matcher.find()) {
            return empty();
        }

        final String ruleKey = matcher.group("ruleKey");
        final String language = matcher.group("language");

        return of(new RuleDescriptionFile(path, ruleKey, language));
    }

    private final Path path;
    private final RuleKey ruleKey;
    private final String language;

    public RuleDescriptionFile(String path, String key, String language) {
        this.path = Path.of(path);
        this.ruleKey = new RuleKey(key);
        this.language = language;
    }

    public Path getPath() {
        return path;
    }

    public RuleKey getRuleKey() {
        return ruleKey;
    }

    public String getMetadataFileName() {
        return ruleKey + ".json";
    }

    public String getLanguage() {
        return language;
    }

    public Path getMetadataPath() {
        return path.getParent().getParent().resolve(getMetadataFileName());
    }

    public Path getSpecificMetadataPath() {
        return path.getParent().resolve(getMetadataFileName());
    }

    public Path getHtmlDescriptionTargetPath(Path targetDir) {
        return targetDir.resolve(language).resolve(ruleKey + ".html");
    }

    public Path getMetadataTargetPath(Path targetDir) {
        return targetDir.resolve(language).resolve(getMetadataFileName());
    }
}
