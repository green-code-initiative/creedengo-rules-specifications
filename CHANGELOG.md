# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added

- Add specifications for rule GCI98, this rule is specific to Python because it's based on the `pandas` library, a library used for data.

### Changed

- Correction of various typos in rules documentations

### Deleted

## [2.2.2] - 2025-03-13

### Added

- [#333](https://github.com/green-code-initiative/creedengo-rules-specifications/pull/333) Add JS variant of rule GCI505 - Idleness: Keep Screen On (addFlags)

### Changed

- [#320](https://github.com/green-code-initiative/creedengo-rules-specifications/pull/320) Doc improvement : some method calls are legitimate in a for loop expression.

## [2.2.1] - 2025-01-27

### Changed

- Move JavaScript rule GCI36 to appropriate folder

## [2.2.0] - 2025-01-26

### Added

- GCI90 C#: Use `Cast` instead of `Select` to cast.
- [#361](https://github.com/green-code-initiative/creedengo-rules-specifications/pull/361) correction of GCI72 Java rule example
- [#370](https://github.com/green-code-initiative/creedengo-rules-specifications/pull/370) Add new JS rule GCI36 - Avoid autoplay for videos and audio content

## [2.1.0] - 2025-01-03

### Added

- Add new Java rule GCI82 - Make non reassigned variables constants

### Changed

- update developers list + update maven plugins versions
- correction of RULES.md file (renaming ECxx rules to GCIxx rules + reformating arrays)

## [2.0.0] - 2024-12-10

### Changed

- First official release of creedengo-rules-specifications component (renaming ECxx rule ids to GCIxx rule ids, refactoring)

## [1.6.5] - 2024-10-18

### Changed

- [#208](https://github.com/green-code-initiative/creedengo-rules-specifications/issues/208) Improves build process with [Maven Wrapper](https://maven.apache.org/wrapper/) and simplifies release management (now, just need to do a build on a tag checkout).

  If you have difficulties on some IDEs (like IntelliJ IDEA), you can follow the procedure: [configuration for Multi Modules Project](https://github.com/qoomon/maven-git-versioning-extension?tab=readme-ov-file#intellij---multi-modules-projects)

## [1.6.4] - 2024-10-04

### Deleted

- deletion of deprecated python EC34 and EC69 rules

## [1.6.3] - 2024-07-26

### Added

- [#308](https://github.com/green-code-initiative/creedengo-rules-specifications/issues/308) Implementing EC24 in Java

## [1.6.2] - 2024-07-17

### Added

- [C# #54](https://github.com/green-code-initiative/creedengo-csharp/pull/54) [EC92] [C#] Use string.Length instead of comparison with empty string
- [C# #61](https://github.com/green-code-initiative/creedengo-csharp/issues/61) [EC91] [C#] Use Where before OrderBy

### Changed

- [Python #282](https://github.com/green-code-initiative/creedengo-rules-specifications/pull/282) Depreciate unrelevant EC69 rule for Python

### Deleted

- [Python #282](https://github.com/green-code-initiative/creedengo-rules-specifications/pull/282) Remove deprecated Python rule EC66

## [1.6.1] - 2024-06-13

### Added

- [C# #66](https://github.com/green-code-initiative/creedengo-csharp/pull/66) [EC93] [C#] Return Task directly
- [JAVA #323](https://github.com/green-code-initiative/creedengo-rules-specifications/pull/323) [EC94] [Java] Use orElseGet instead of orElse

## [1.6.0] - 2024-06-03

### Added

- [#293](https://github.com/green-code-initiative/creedengo-rules-specifications/issues/293) [EC513] Swift port
- [#310](https://github.com/green-code-initiative/creedengo-rules-specifications/issues/310) EC515 Swift port
- [#306](https://github.com/green-code-initiative/creedengo-rules-specifications/issues/306) Swift port of rule EC514
- [#315](https://github.com/green-code-initiative/creedengo-rules-specifications/pull/315) Add rule EC530 for javascript
- [#321](https://github.com/green-code-initiative/creedengo-rules-specifications/pull/321) Add rule EC522 for javascript (avoid brightness override)
- [Python #26](https://github.com/green-code-initiative/creedengo-python/issues/26) [EC89] [Python] Avoid unlimited cache

### Changed

- [#318](https://github.com/green-code-initiative/creedengo-rules-specifications/issues/318) Implement the rule EC523 for React Native

### Deleted

- [#318](https://github.com/green-code-initiative/creedengo-rules-specifications/issues/318) Merge the rule EC8 with EC523

## [1.5.4] - 2024-05-24

### Added

- [#298](https://github.com/green-code-initiative/creedengo-rules-specifications/pull/298) Add HTML rule EC36 (Avoid autoplay for videos and audio content)
- [C# #36](https://github.com/green-code-initiative/creedengo-csharp/issues/36) [EC86] [C#] GC.Collect should not be called
- [C# #42](https://github.com/green-code-initiative/creedengo-csharp/issues/42) [EC87] [C#] Use collection indexer
- [C# #44](https://github.com/green-code-initiative/creedengo-csharp/issues/44) [EC88] [C#] Dispose resource asynchronously

### Changed

- [mobile #302](https://github.com/green-code-initiative/creedengo-rules-specifications/pull/302) Big cleanup / sorting of mobile related rules
- [C# #304](https://github.com/green-code-initiative/creedengo-rules-specifications/pull/304) [EC81] [C#] Fix type in json spec to allow import into Sonarqube

## [1.5.3] - 2024-05-03

### Changed

- [PHP #23](https://github.com/green-code-initiative/creedengo-php/issues/23) Deprecation of EC22 rule for PHP (waiting for measurement) - correction

## [1.5.2] - 2024-05-02

### Changed

- [PHP #23](https://github.com/green-code-initiative/creedengo-php/issues/23) Deprecation of EC22 rule for PHP (waiting for measurement)

## [1.5.1] - 2024-04-29

### Added

- Swift rules cleanup and updates (removed duplicated rules, added [EC602])
- [C# #18](https://github.com/green-code-initiative/creedengo-csharp/issues/18) [EC81] [C#] Specify struct layout
- [C# #285](https://github.com/green-code-initiative/creedengo-rules-specifications/pull/285) [EC82] [C#] Variable can be made constant
- [C# #286](https://github.com/green-code-initiative/creedengo-rules-specifications/issues/286) [EC83] [C#] Replace Enum ToString() with nameof
- [C# #27](https://github.com/green-code-initiative/creedengo-csharp/issues/27) [EC84] [C#] Avoid async void methods
- [C# #34](https://github.com/green-code-initiative/creedengo-csharp/issues/34) [EC85] [C#] Make type sealed

## [1.5.0] - 2024-02-02

### Added

- [#269](https://github.com/green-code-initiative/creedengo-rules-specifications/pull/269) Add new Javascript rule EC31 (Prefer lighter formats for image files)

### Changed

- Refuse rule proposition [Avoid returning a JPA Entity in a RestController](https://github.com/green-code-initiative/creedengo-rules-specifications/pull/138) for Java because of lack of arguments and measures
- Refuse rule proposition [Avoid use of static in interface](https://github.com/green-code-initiative/creedengo-rules-specifications/pull/7) for Java because of lack of arguments and measures
- update RULES.md : close 2 old PRs and add to rework rules array

### Deleted

- [#272](https://github.com/green-code-initiative/creedengo-rules-specifications/pull/272) Remove deprecated java rules EC4, EC53, EC63 and EC75

## [1.4.7] - 2024-01-11

### Changed

- [#123](https://github.com/green-code-initiative/creedengo-rules-specifications/issues/123) Rule EC7 not implementable neither Java nor PHP
- Depreciation of rule EC34 for Python and PHP to replace it by EC35

## [1.4.6] - 2023-12-30

### Changed

- [#266](https://github.com/green-code-initiative/creedengo-rules-specifications/issues/266) enable automatic publish of ecocode-rules-specifications to Maven Central

## [1.4.5] - 2023-12-30

### Deleted

- [#182](https://github.com/green-code-initiative/creedengo-rules-specifications/issues/182) delete java plugin sources because it was moved to new repository `ecoCode-java`

## [1.4.4] - 2023-12-29

### Changed

- RULES.md upgrades
- technical upgrade for maven central publication

### Deleted

- [#182](https://github.com/green-code-initiative/creedengo-rules-specifications/issues/182) disable java plugin beacasue moved to new repository `ecoCode-java`

## [1.4.3] - 2023-12-19

### Added

- [#248](https://github.com/green-code-initiative/creedengo-rules-specifications/issues/248) EC2 : Add tests to prove there is no problem with 'instanceof' operator

### Changed

- [#123](https://github.com/green-code-initiative/creedengo-rules-specifications/issues/123) Complete resources for EC7 rule for Python language
- Update ecocode-rules-specifications to 0.0.10

### Deleted

- [#4](https://github.com/green-code-initiative/creedengo-python/issues/4) Deprecate rule EC66 for Python because not applicable (see details inside issue)
- [#240](https://github.com/green-code-initiative/creedengo-rules-specifications/issues/240) Deprecate rule EC53 for Java because of no good arguments and not enough green measures
- [#258](https://github.com/green-code-initiative/creedengo-rules-specifications/pull/258) Deprecate rule EC63 for Java because there are already 3 native Sonarqube rules that cover the same use cases
- [#259](https://github.com/green-code-initiative/creedengo-rules-specifications/pull/259) Deprecate rule EC75 for Java because not applicable since JDK8

## [1.4.2] - 2023-12-05

### Added

- [#224](https://github.com/green-code-initiative/creedengo-rules-specifications/issues/224) Add Swift rules from ecocode-ios to ecocode-rules-specifications

### Changed

- Update ecocode-rules-specifications to 0.0.9

### Deleted

- [#243](https://github.com/green-code-initiative/creedengo-rules-specifications/pull/243) Deprecate rule EC4 for Java because not applicable

## [1.4.1] - 2023-12-04

### Changed

- [ios#3](https://github.com/green-code-initiative/creedengo-ios/issues/3) Move iOS rules into centralized rules repository
- [android#67](https://github.com/green-code-initiative/creedengo-android/issues/67) Move Android rules into centralized rules repository
- [ios#3](https://github.com/green-code-initiative/creedengo-ios/issues/3) Move iOS rules into centralized rules repository
- [#103](https://github.com/green-code-initiative/creedengo-rules-specifications/issues/103) Upgrade RULES.md: set proposed HTML rule "HTML page must contain a doctype tag" as refused with link to the justification
- [#106](https://github.com/green-code-initiative/creedengo-rules-specifications/issues/106) Upgrade RULES.md : rule EC67 not relevant neither for Python nor Rust
- [#112](https://github.com/green-code-initiative/creedengo-rules-specifications/issues/112) Updating EC1 rule to add controls on streams
- [#128](https://github.com/green-code-initiative/creedengo-rules-specifications/pull/128) Adding EC35 rule for Python and PHP : EC35 rule replaces EC34 with a specific use case ("file not found" sepcific)
- [#132](https://github.com/green-code-initiative/creedengo-rules-specifications/issues/132) Upgrade RULES.md: set proposed Python rule "Use numpy array instead of standard list" as refused with link to the justification
- [#136](https://github.com/green-code-initiative/creedengo-rules-specifications/issues/136) Upgrade rule EC53 for Python : no implementation possible for python
- [#140](https://github.com/green-code-initiative/creedengo-rules-specifications/issues/140) Upgrade rule EC3 for Python : no implementation possible for python
- [#185](https://github.com/green-code-initiative/creedengo-rules-specifications/issues/185) Add build number to manifest
- [#216](https://github.com/green-code-initiative/creedengo-rules-specifications/issues/216) Upgrade rule EC2 for Java : Multiple if-else statement improvment
- [#225](https://github.com/green-code-initiative/creedengo-rules-specifications/pull/225) Upgrade licence system and licence headers of Java files
- [#247](https://github.com/green-code-initiative/creedengo-rules-specifications/issues/247) Upgrade rule EC2 for Java : float and double types deleted because of non compatibility with rule

## [1.4.0] - 2023-08-08

### Added

- [#205](https://github.com/green-code-initiative/creedengo-rules-specifications/issues/205) compatibility with SonarQube 10.1
- [#210](https://github.com/green-code-initiative/creedengo-rules-specifications/pull/210) Publish to Maven Central (module ecocode-rules-specifications)

### Deleted

- [#182](https://github.com/green-code-initiative/creedengo-rules-specifications/issues/182) Split repository : move Python module to new `ecoCode-python` repository
- [#182](https://github.com/green-code-initiative/creedengo-rules-specifications/issues/182) Split repository : move Php module to new `ecoCode-php` repository

## [1.3.1] - 2023-07-19

### Added

- [#207](https://github.com/green-code-initiative/creedengo-rules-specifications/issues/207) Add release tag analyzis on SonarCloud

### Deleted

- [#211](https://github.com/green-code-initiative/creedengo-rules-specifications/pull/211) Move JavaScript plugin to its dedicated repository

## [1.3.0] - 2023-07-04

### Added

- [#108](https://github.com/green-code-initiative/creedengo-rules-specifications/issues/108) new Python rule EC66 : Use single quote (') instead of quotation mark (")
- [#109](https://github.com/green-code-initiative/creedengo-rules-specifications/issues/109) new PHP rule EC3 : Getting the size of the collection in the loop. For further [RULES.md](./RULES.md) file
- [#113](https://github.com/green-code-initiative/creedengo-rules-specifications/issues/113) new Python rule EC10 : Use unoptimized vector images
- [#127](https://github.com/green-code-initiative/creedengo-rules-specifications/issues/127) new Python rule EC404 : Usage of generator comprehension instead of list comprehension in for loop declaration
- [#190](https://github.com/green-code-initiative/creedengo-rules-specifications/pull/190) Add Python rule: Use unoptimized vector images
- [#191](https://github.com/green-code-initiative/creedengo-rules-specifications/issues/191) Update rule tags for Java, Python, and PHP plugins
- [#192](https://github.com/green-code-initiative/creedengo-rules-specifications/pull/192) new Python rule EC203 : Detect unoptimized file formats
- Add JavaScript rules from [ecoCode ESLint plugin v0.2.0](https://github.com/green-code-initiative/creedengo-linter/releases/tag/eslint-plugin%2F0.2.0)

### Changed

- [#19](https://github.com/green-code-initiative/creedengo-common/issues/19) process changed for development environment installation : easier to initialize locally environment (check [`INSTALL.md`](https://github.com/green-code-initiative/creedengo-common/blob/main/doc/INSTALL.md#howto-install-sonarqube-dev-environment) file)
- [#187](https://github.com/green-code-initiative/creedengo-rules-specifications/issues/187) upgrade librairies to SonarQube 10.0.0
- [#196](https://github.com/green-code-initiative/creedengo-rules-specifications/issues/196) updating PHP files to make them following the coding standards (PSR-12)
- [#201](https://github.com/green-code-initiative/creedengo-rules-specifications/pull/201) Clean-up plugins and dependencies
- technical : upgrade of maven plugins versions

## [1.2.1] - 2023-04-18

### Changed

- [#180](https://github.com/green-code-initiative/creedengo-rules-specifications/pull/180) correction of SonarQube review for MarketPlace (sonar plugin)

## [1.2.0] - 2023-04-14

### Added

- [#171](https://github.com/green-code-initiative/creedengo-rules-specifications/issues/171) Add migration mechanism to support "issue re-keying"

### Changed

- [#161](https://github.com/green-code-initiative/creedengo-rules-specifications/pull/161) Remove unnecessary junit dependencies in pom.xml
- [#166](https://github.com/green-code-initiative/creedengo-rules-specifications/issues/166) Correction of wrong message of rule EC63
- [#167](https://github.com/green-code-initiative/creedengo-rules-specifications/issues/167) Use same kind for rules across different languages
- [#173](https://github.com/green-code-initiative/creedengo-rules-specifications/issues/173) Update issue description of rule EC34 (try-catch)

## [1.1.0] - 2023-04-03

### Changed

- [#63](https://github.com/green-code-initiative/creedengo-rules-specifications/issues/63) Update plugins to be compliant for SonarQube MarketPlace integration ( PR [#79](https://github.com/green-code-initiative/creedengo-rules-specifications/pull/79) )
- [#88](https://github.com/green-code-initiative/creedengo-rules-specifications/pull/88) upgrade rules matrix with new ids + refactoring rules documentation (`RULES.md`)

### Deleted

- [#85](https://github.com/green-code-initiative/creedengo-rules-specifications/issues/85) Cleaning some useless classes on PHP plugin

## [1.0.0] - 2023-03-24

### Added

- [#44](https://github.com/green-code-initiative/creedengo-rules-specifications/pull/44) Update the PHP description rules
- [#67](https://github.com/green-code-initiative/creedengo-rules-specifications/pull/67) Add JavaScript plugin
- add `ecocode` tag on all rules on Java, PHP, Python and javascript plugins

### Changed

- [#40](https://github.com/green-code-initiative/creedengo-rules-specifications/issues/40) Refactoring of package names (`cnumr` to `greencodeinitiative`)
- [#55](https://github.com/green-code-initiative/creedengo-rules-specifications/issues/55) rename `eco-conception` tag of rules to `eco-design`
- [#58](https://github.com/green-code-initiative/creedengo-rules-specifications/issues/58) check and upgrade compatibility to SonarQube 9.9
- move common init scripts to `ecoCode-common` repository
- modifying documentation and move `CONTRIBUTING.md`, `CODE_STYLE.md` and `INSTALL.md` to common doc in `ecoCode-common` repository
- security / performance optimizations : correction of `sonarcloud.io` security hotspots (java / php, python) and move Pattern compilation to static attribute
- [#64](https://github.com/green-code-initiative/creedengo-rules-specifications/issues/64) Python: ecoCode plugin with SonarQube, no code-smell detection
- [#65](https://github.com/green-code-initiative/creedengo-rules-specifications/issues/65) Create a test project to check new plugin rule in real environment
- [#71](https://github.com/green-code-initiative/creedengo-rules-specifications/issues/71) After an PHP analysis, no ecocode code smells appears in my Sonar project
- [#76](https://github.com/green-code-initiative/creedengo-rules-specifications/issues/76) correction of SonarQube plugins homepage link broken
- documentation upgrade

### Deleted

- cleaning old files (move them to `ecoCode-archive` repository)

## [0.2.2] - 2023-01-19

### Added

- [#23](https://github.com/green-code-initiative/creedengo-rules-specifications/pull/23) Add images to the description files.
- [#46](https://github.com/green-code-initiative/creedengo-rules-specifications/pull/46) Add CONTRIBUTING.MD, CODE_OF_CONDUCT.md and CODE_STYLE.md

### Changed

- [#27](https://github.com/green-code-initiative/creedengo-rules-specifications/pull/27) Fix [WARNING] Maven-shade-plugin overlapping classes and upgrade SonarRuntime.
- [#33](https://github.com/green-code-initiative/creedengo-rules-specifications/issues/33) Update plugin description in code
- [#42](https://github.com/green-code-initiative/creedengo-rules-specifications/issues/42) Fix Crash SonarQube analysis because of some ClassCast Exceptions
- [#48](https://github.com/green-code-initiative/creedengo-rules-specifications/pull/48) correction SONAR issue info - delete public keyword on tests
- Improve "build" GitHub actions to execute checks on branches from fork repositories

## [0.2.1] - 2022-12-30

### Changed

- [#22](https://github.com/green-code-initiative/creedengo-rules-specifications/issues/22) Error when running sonar scan with ecocode

## [0.2.0] - 2022-12-28

### Added

- [#15](https://github.com/green-code-initiative/creedengo-rules-specifications/pull/15) Upgrade some versions + sonar version from 9.3 to 9.8
- [#17](https://github.com/green-code-initiative/creedengo-rules-specifications/issues/17) improve releasing system
- [#25](https://github.com/green-code-initiative/creedengo-rules-specifications/issues/25) Release management vs maven packaging (not the same
  version)

### Changed

- documentation upgrade (internal)

## [0.1.1] - 2022-12-20

### Added

- [#161](https://github.com/cnumr/creedengo-rules-specifications/issues/161) adding tool to update tags of native rules with our custom tags

### Changed

- documentation upgrade (internal)
- optimization/refactoring on pom.xml dependencies (internal)

## [0.1.0] - 2022-12-14

### Added

- First official release of ecocode plugins : java plugin, php plugin and python plugin

## Comparison List

[unreleased](https://github.com/green-code-initiative/creedengo-rules-specifications/compare/2.2.1...HEAD)
[2.2.1](https://github.com/green-code-initiative/creedengo-rules-specifications/compare/2.2.0...2.2.1)
[2.2.0](https://github.com/green-code-initiative/creedengo-rules-specifications/compare/2.1.0...2.2.0)
[2.1.0](https://github.com/green-code-initiative/creedengo-rules-specifications/compare/2.0.0...2.1.0)
[2.0.0](https://github.com/green-code-initiative/creedengo-rules-specifications/compare/1.6.5...2.0.0)
[1.6.5](https://github.com/green-code-initiative/creedengo-rules-specifications/compare/1.6.4...1.6.5)
[1.6.4](https://github.com/green-code-initiative/creedengo-rules-specifications/compare/1.6.3...1.6.4)
[1.6.3](https://github.com/green-code-initiative/creedengo-rules-specifications/compare/1.6.2...1.6.3)
[1.6.2](https://github.com/green-code-initiative/creedengo-rules-specifications/compare/1.6.1...1.6.2)
[1.6.1](https://github.com/green-code-initiative/creedengo-rules-specifications/compare/1.6.0...1.6.1)
[1.6.0](https://github.com/green-code-initiative/creedengo-rules-specifications/compare/1.5.4...1.6.0)
[1.5.4](https://github.com/green-code-initiative/creedengo-rules-specifications/compare/1.5.3...1.5.4)
[1.5.3](https://github.com/green-code-initiative/creedengo-rules-specifications/compare/1.5.2...1.5.3)
[1.5.2](https://github.com/green-code-initiative/creedengo-rules-specifications/compare/1.5.1...1.5.2)
[1.5.1](https://github.com/green-code-initiative/creedengo-rules-specifications/compare/1.5.0...1.5.1)
[1.5.0](https://github.com/green-code-initiative/creedengo-rules-specifications/compare/1.4.7...1.5.0)
[1.4.7](https://github.com/green-code-initiative/creedengo-rules-specifications/compare/1.4.6...1.4.7)
[1.4.6](https://github.com/green-code-initiative/creedengo-rules-specifications/compare/1.4.5...1.4.6)
[1.4.5](https://github.com/green-code-initiative/creedengo-rules-specifications/compare/1.4.4...1.4.5)
[1.4.4](https://github.com/green-code-initiative/creedengo-rules-specifications/compare/1.4.3...1.4.4)
[1.4.3](https://github.com/green-code-initiative/creedengo-rules-specifications/compare/1.4.2...1.4.3)
[1.4.2](https://github.com/green-code-initiative/creedengo-rules-specifications/compare/1.4.1...1.4.2)
[1.4.1](https://github.com/green-code-initiative/creedengo-rules-specifications/compare/1.4.0...1.4.1)
[1.4.0](https://github.com/green-code-initiative/creedengo-rules-specifications/compare/1.3.1...1.4.0)
[1.3.1](https://github.com/green-code-initiative/creedengo-rules-specifications/compare/1.3.0...1.3.1)
[1.3.0](https://github.com/green-code-initiative/creedengo-rules-specifications/compare/1.2.1...1.3.0)
[1.2.1](https://github.com/green-code-initiative/creedengo-rules-specifications/compare/1.2.0...1.2.1)
[1.2.0](https://github.com/green-code-initiative/creedengo-rules-specifications/compare/1.1.0...1.2.0)
[1.1.0](https://github.com/green-code-initiative/creedengo-rules-specifications/compare/1.0.0...1.1.0)
[1.0.0](https://github.com/green-code-initiative/creedengo-rules-specifications/compare/0.2.2...1.0.0)
[0.2.2](https://github.com/green-code-initiative/creedengo-rules-specifications/compare/0.2.1...0.2.2)
[0.2.1](https://github.com/green-code-initiative/creedengo-rules-specifications/compare/0.2.0...0.2.1)
[0.2.0](https://github.com/green-code-initiative/creedengo-rules-specifications/compare/0.1.1...0.2.0)
[0.1.1](https://github.com/green-code-initiative/creedengo-rules-specifications/compare/0.1.0...0.1.1)
[0.1.0](https://github.com/green-code-initiative/creedengo-rules-specifications/releases/tag/0.1.0)
