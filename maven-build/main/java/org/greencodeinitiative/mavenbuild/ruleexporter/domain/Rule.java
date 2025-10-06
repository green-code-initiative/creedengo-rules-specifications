package org.greencodeinitiative.mavenbuild.ruleexporter.domain;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Optional.empty;
import static java.util.Optional.of;

public class Rule {
	/**
	 * Resources to include
	 */
	private static final Pattern TARGET_RESOURCES = Pattern.compile("^.{1,1000}/(?<ruleKey>GCI\\d{1,50})/(?<language>[^/]{1,50})/GCI\\d{1,50}\\.html$");

	public static Optional<Rule> createFromHtmlDescription(Path htmlDescription) {
		final Matcher matcher = TARGET_RESOURCES.matcher(htmlDescription.toString().replace('\\', '/'));
		if (!matcher.find()) {
			return empty();
		}
		final String ruleKey = matcher.group("ruleKey");
		final Path metadata = htmlDescription.getParent().getParent().resolve(ruleKey + ".json");
		final Path specificMetadata = htmlDescription.getParent().resolve(ruleKey + ".json");

		if (!Files.isRegularFile(htmlDescription) || !Files.isRegularFile(metadata)) {
			return empty();
		}

		return of(new Rule(
			matcher.group("language"),
			htmlDescription,
			metadata,
			specificMetadata
		));
	}

	private final String language;
	private final Path htmlDescription;
	private final Path metadata;
	private final Path specificMetadata;

	public Rule(String language, Path htmlDescription, Path metadata, Path specificMetadata) {
		this.language = language;
		this.htmlDescription = htmlDescription;
		this.metadata = metadata;
		this.specificMetadata = specificMetadata;
	}

	public Path getHtmlDescriptionTargetPath(Path targetDir) {
		return targetDir.resolve(language).resolve(htmlDescription.getFileName());
	}

	public Path getMetadataTargetPath(Path targetDir) {
		return targetDir.resolve(language).resolve(metadata.getFileName());
	}

	public String language() {
		return language;
	}

	public Path htmlDescription() {
		return htmlDescription;
	}

	public Path metadata() {
		return metadata;
	}

	public Path specificMetadata() {
		return specificMetadata;
	}
}
