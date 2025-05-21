/*
 * creedengo Rules Specifications repository - Repository that contains the specifications of every static-analysis rules available in creedengo plugins.
 * Copyright © 2024 Green Code Initiative (https://green-code-initiative.org)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.greencodeinitiative;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.sonar.api.SonarRuntime;
import org.sonar.api.issue.impact.SoftwareQuality;
import org.sonar.api.rule.RuleScope;
import org.sonar.api.rules.CleanCodeAttribute;
import org.sonar.api.rules.RuleType;
import org.sonar.api.server.rule.RuleDescriptionSection;
import org.sonar.api.server.rule.RuleDescriptionSection.RuleDescriptionSectionKeys;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.api.server.rule.RulesDefinition.Repository;
import org.sonar.api.server.rule.RulesDefinition.Rule;
import org.sonar.api.server.rule.internal.DefaultNewRepository;
import org.sonar.api.utils.Version;
import org.sonarsource.analyzer.commons.RuleMetadataLoader;

// Still using RuleType.CODE_SMELL to support old SonarQube instances
@SuppressWarnings("deprecation")
class RulesTest {
  private static final Version SONAR_API_VERSION = Version.create(10, 10);
  private static final String RESOURCE_DIR = "rules";
  private static final String RESOURCE_BASE_PATH =
      "/org/green-code-initiative/" + RESOURCE_DIR + "/";
  private static final String[] LANGUAGES =
      new String[] {"csharp", "html", "java", "javascript", "php", "python", "swift", "xml"};

  private static final List<String> MANDATORY_TAGS = Arrays.asList("creedengo");

  private static final EnumMap<CleanCodeAttribute, List<String>>
      MANDATORY_CLEAN_CODE_ATTRIBUTE_TO_TAGS = new EnumMap<>(CleanCodeAttribute.class);

  static {
    // performance and sustainability are already used by Sonar standard rules:
    // https://rules.sonarsource.com/java/tag/sustainability/
    // https://rules.sonarsource.com/java/tag/performance/
    MANDATORY_CLEAN_CODE_ATTRIBUTE_TO_TAGS.put(
        CleanCodeAttribute.EFFICIENT, Arrays.asList("performance", "sustainability"));
    MANDATORY_CLEAN_CODE_ATTRIBUTE_TO_TAGS.put(
        CleanCodeAttribute.TRUSTWORTHY, Arrays.asList("social"));
  }

  private static final EnumMap<CleanCodeAttribute, List<String>>
      OPTIONAL_CLEAN_CODE_ATTRIBUTE_TO_TAGS = new EnumMap<>(CleanCodeAttribute.class);

  static {
    OPTIONAL_CLEAN_CODE_ATTRIBUTE_TO_TAGS.put(
        CleanCodeAttribute.EFFICIENT, Collections.emptyList());
    OPTIONAL_CLEAN_CODE_ATTRIBUTE_TO_TAGS.put(
        CleanCodeAttribute.TRUSTWORTHY, Arrays.asList("privacy"));
  }

  private static final List<String> AREA_TAGS =
      Arrays.asList(
          "audio", "cache", "error-handling", "image", "leak", "memory", "network", "video");

  private static final List<String> TECHNOLOGY_TAGS =
      Arrays.asList(
          "android", "ios", "jsx", "nestjs", "react", "react-native", "regex", "spring", "sql");

  /**
   * Built-in rule tags from
   * https://docs.sonarsource.com/sonarqube/latest/user-guide/rules/built-in-rule-tags/
   */
  private static final List<String> BUILT_IN_RULE_TAGS =
      Arrays.asList("bad-practice", "user-experience");

  /** Tags from https://github.com/cnumr/best-practices-mobile */
  private static final List<String> BEST_PRACTISES_MOBILE_TAGS =
      Arrays.asList(
          "optimized-api",
          "leakage",
          "bottleneck",
          "idleness",
          "power",
          "batch",
          "release",
          "sobriety",
          "longevity");

  /**
   * Let's not have too many tags, especially tags redundant with sustainability or performance,
   * e.g. do not allow environment or eco-design.
   */
  private static final List<String> ALL_TAGS =
      Stream.concat(
              MANDATORY_CLEAN_CODE_ATTRIBUTE_TO_TAGS.values().stream(),
              Stream.concat(
                  OPTIONAL_CLEAN_CODE_ATTRIBUTE_TO_TAGS.values().stream(),
                  Stream.of(
                      MANDATORY_TAGS,
                      BUILT_IN_RULE_TAGS,
                      AREA_TAGS,
                      TECHNOLOGY_TAGS,
                      BEST_PRACTISES_MOBILE_TAGS)))
          .flatMap(List::stream)
          .collect(Collectors.toList());

  private static final Pattern NONCOMPLIANT_COMMENT_PATTERN =
      Pattern.compile(".*(// Noncompliant|!-- Noncompliant|# Noncompliant).*", Pattern.DOTALL);

  private static SonarRuntime sonarRuntime;

  @BeforeAll
  static void init() {
    sonarRuntime = mock(SonarRuntime.class);
    when(sonarRuntime.getApiVersion()).thenReturn(SONAR_API_VERSION);
  }

  private static Path[] findAllLangs() throws URISyntaxException, IOException {
    URI uri = RuleMetadataLoader.class.getResource(RESOURCE_BASE_PATH).toURI();
    Path path = Paths.get(uri);
    try (Stream<Path> walk = Files.walk(path, 1)) {
      return walk.filter(p -> !RESOURCE_DIR.equals(p.toFile().getName())).toArray(Path[]::new);
    }
  }

  private static SortedSet<String> findAllRules(Path langPath) throws IOException {
    SortedSet<String> rules = new TreeSet<>();
    try (Stream<Path> langWalk = Files.walk(langPath, 1)) {
      for (Iterator<Path> it = langWalk.iterator(); it.hasNext(); ) {
        String ruleFile = it.next().toFile().getName();
        int extensionIndex = ruleFile.lastIndexOf('.');
        if (extensionIndex != -1) {
          String rule = ruleFile.substring(0, extensionIndex);
          rules.add(rule);
        }
      }
    }
    return rules;
  }

  @Test
  void testRules() throws URISyntaxException, IOException {
    RulesDefinition.Context context = new RulesDefinition.Context();
    Path[] langPaths = findAllLangs();
    assertArrayEquals(
        LANGUAGES, Stream.of(langPaths).map(p -> p.toFile().getName()).sorted().toArray());
    addRules(context, langPaths);
    assertEquals(LANGUAGES.length, context.repositories().size());
    for (Repository repo : context.repositories()) {
      assertFalse(repo.rules().isEmpty(), repo.key() + " repository should have rules");
      for (Rule rule : repo.rules()) {
        assertFalse(rule.activatedByDefault(), rule + " should not be activated by default");
        assertEquals(RuleScope.MAIN, rule.scope(), rule + " should have MAIN scope");
        assertEquals(RuleType.CODE_SMELL, rule.type(), rule + " should have CODE_SMELL type");
        assertEquals(
            Collections.singleton(SoftwareQuality.RELIABILITY),
            rule.defaultImpacts().keySet(),
            rule + " should have RELIABILITY Software Quality");
        assertTrue(
            MANDATORY_CLEAN_CODE_ATTRIBUTE_TO_TAGS.keySet().contains(rule.cleanCodeAttribute()),
            rule
                + " should have Clean Code Attribute in "
                + MANDATORY_CLEAN_CODE_ATTRIBUTE_TO_TAGS.keySet());
        checkTags(rule.key(), rule.cleanCodeAttribute(), rule.tags());
        checkName(rule.key(), rule.name());
        checkDescription(rule.key(), rule.ruleDescriptionSections());
      }
    }
  }

  private void addRules(RulesDefinition.Context context, Path[] langPaths) throws IOException {
    for (Path langPath : langPaths) {
      String lang = langPath.toFile().getName();
      DefaultNewRepository repository = new DefaultNewRepository(context, lang, lang, false);
      RuleMetadataLoader ruleMetadataLoader =
          new RuleMetadataLoader(RESOURCE_BASE_PATH + lang, sonarRuntime);
      for (String key : findAllRules(langPath)) {
        try {
          ruleMetadataLoader.addRulesByRuleKey(repository, List.of(key));
        } catch (IllegalStateException e) {
          throw new AssertionError("Error with rule key " + key + " of language " + lang, e);
        }
      }
      context.registerRepository(repository);
    }
  }

  private void checkDescription(
      String ruleKey, List<RuleDescriptionSection> ruleDescriptionSections) {
    List<String> sections =
        ruleDescriptionSections.stream()
            .map(RuleDescriptionSection::getKey)
            .collect(Collectors.toList());
    // see https://docs.asciidoctor.org/asciidoc/latest/sections/auto-ids/
    assertFalse(
        sections.isEmpty(),
        ruleKey
            + " should have standard description sections but got none"
            + " (is :!sectids: present?)");
    // Introduction is optional
    if (RuleDescriptionSectionKeys.INTRODUCTION_SECTION_KEY.equals(sections.get(0))) {
      sections.remove(0);
    }
    assertIterableEquals(
        Arrays.asList(
            RuleDescriptionSectionKeys.ROOT_CAUSE_SECTION_KEY,
            RuleDescriptionSectionKeys.HOW_TO_FIX_SECTION_KEY,
            RuleDescriptionSectionKeys.RESOURCES_SECTION_KEY),
        // Using set since "how to fix" sections might be repeated
        new LinkedHashSet<String>(sections),
        ruleKey + " should have standard description sections but got " + sections);
    String howToFix =
        ruleDescriptionSections.stream()
            .filter(s -> RuleDescriptionSectionKeys.HOW_TO_FIX_SECTION_KEY.equals(s.getKey()))
            .findFirst()
            .orElse(null)
            .getHtmlContent();
    assertTrue(
        NONCOMPLIANT_COMMENT_PATTERN.matcher(howToFix).matches(),
        ruleKey + " must have Noncompliant comment in the How to fix section: " + howToFix);
    assertTrue(
        howToFix.contains("<h3>Noncompliant code example</h3>"),
        ruleKey
            + " must have <h3>Noncompliant code example</h3> in How to fix"
            + " section: "
            + howToFix);
    assertTrue(
        howToFix.contains("<h3>Compliant solution</h3>"),
        ruleKey + " must have <h3>Compliant solution</h3> in How to fix section: " + howToFix);
  }

  private void checkName(String ruleKey, String name) {
    // See guidelines in
    // https://docs.sonarsource.com/sonarqube/latest/extension-guide/adding-coding-rules/#coding-rule-guidelines
    assertTrue(
        name.contains(" should "),
        ruleKey + " title should match the pattern \"X should [not] Y\": " + name);
    assertFalse(name.endsWith("."), ruleKey + " title should not end with a point: " + name);
    // Avoid rules such as "Idleness: Keep Screen On (addFlags)"
    // or "Accessibility - Image tags should have an alternate text attribute"
    assertFalse(
        name.contains(":") || name.contains(" - "),
        ruleKey + " title should not have a category/tag prefix: " + name);
    assertTrue(
        name.length() < 80, ruleKey + " title length should be less than 80 characters: " + name);
  }

  private void checkTags(String ruleKey, CleanCodeAttribute cleanCodeAttribute, Set<String> tags) {
    assertTrue(tags.containsAll(MANDATORY_TAGS), ruleKey + " should have tags " + MANDATORY_TAGS);
    assertTrue(
        ALL_TAGS.containsAll(tags), ruleKey + " should only have allowed tags but got: " + tags);
    assertTrue(
        tags.containsAll(MANDATORY_CLEAN_CODE_ATTRIBUTE_TO_TAGS.get(cleanCodeAttribute)),
        ruleKey
            + " should have tags "
            + MANDATORY_CLEAN_CODE_ATTRIBUTE_TO_TAGS.get(cleanCodeAttribute));
    assertFalse(
        Stream.concat(
                MANDATORY_CLEAN_CODE_ATTRIBUTE_TO_TAGS.entrySet().stream(),
                OPTIONAL_CLEAN_CODE_ATTRIBUTE_TO_TAGS.entrySet().stream())
            .filter(e -> !cleanCodeAttribute.equals(e.getKey()))
            .anyMatch(e -> new ArrayList<>(tags).removeAll(e.getValue())),
        ruleKey + " should not have a tag from another Clean Code Attribute");
  }
}
