<div align="center">
  <picture>
    <source media="(prefers-color-scheme: dark)" width="500" height="100" srcset="docs/resources/creedengo_light.svg">
    <source media="(prefers-color-scheme: light)" width="500" height="100" srcset="docs/resources/creedengo_dark.svg">
    <img alt="Creedengo logo" width="500" height="100" src="docs/resources/creedengo_light.svg">
  </picture>
  <p>
    <strong>A Green Code Initiative project</strong>
  </p>
</div>

---

_creedengo_ is a collective project aiming to reduce environmental footprint of software at the code level. The goal of
the project is to provide a list of static code analyzers to highlight code structures that may have a negative
ecological impact: energy and resources over-consumption, "fatware", shortening terminals' lifespan, etc.

_creedengo_ is based on evolving catalogs of [good practices](docs/rules), for various technologies. A SonarQube plugin
then implements these catalogs as rules for scanning your projects.

**Warning**: this is still a very early stage project. Any feedback or contribution will be highly appreciated. Please
refer to the contribution section.

[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)
[![Contributor Covenant](https://img.shields.io/badge/Contributor%20Covenant-2.1-4baaaa.svg)](https://github.com/green-code-initiative/creedengo-common/blob/main/doc/CODE_OF_CONDUCT.md)

## 🌿 SonarQube Plugins

7 technologies are supported by creedengo right now:

- "standard" plugins :
    - [Java plugin](https://github.com/green-code-initiative/creedengo-java)
    - [JavaScript plugin](https://github.com/green-code-initiative/creedengo-javascript)
    - [PHP plugin](https://github.com/green-code-initiative/creedengo-php)
    - [Python plugin](https://github.com/green-code-initiative/creedengo-python)
    - [C# plugin](https://github.com/green-code-initiative/creedengo-csharp)
- mobile plugins :
    - [Android plugin](https://github.com/green-code-initiative/ecoCode-android)
    - [iOS plugin](https://github.com/green-code-initiative/creedengo-ios)

![Screenshot](docs/resources/screenshot.jpg)

### eco-design SonarQube plugin

![Ekko logo](docs/resources/5ekko.png)

There are two kinds of plugins :

- One for web / backoffice (PHP, Python, Java, JavaScript), using smells described in the 2nd edition of the repository
  published in september 2015.
  You can find all the
  rules [here (in french)](https://docs.google.com/spreadsheets/d/1nujR4EnajnR0NSXjvBW3GytOopDyTfvl3eTk2XGLh5Y/edit#gid=1386834576).
  The current repository is for web / backOffice
- One for mobile (Android/iOS), using [a set of code smells](https://github.com/cnumr/best-practices-mobile) theorised
  by Dr. Olivier Le Goaër.
  You can find this plugin in the repository [here](https://github.com/green-code-initiative/ecoCode-android)

### How a SonarQube plugin works

Code is parsed to be transformed as AST. AST will allow you to access one or more nodes of your code.
For example, you’ll be able to access of all your `for` loop, to explore content etc.

To better understand AST structure, you can use the [AST Explorer](https://astexplorer.net/).

### creedengo rules specification repository

This project contains the specifications of all creedengo rules, for all languages.

#### Structure

Rules are organized by folder based on their ID in the [root rules folder](src/main/rules).
Each of these folders contains a file with the metadata of the rule, and description by language.

The metadata file uses the format supported by
the [SonarSource Analyzers Commons](https://github.com/SonarSource/sonar-analyzer-commons/tree/master/commons) library.
To find out what values can be put there, we advise you to use the
official [SonarQube documentation](https://docs.sonarsource.com/sonarqube/latest/user-guide/rules/overview/), and to
rely on already existing files.

Here is an example:

```text
src/main/rules
├── GCI104
│   ├── java
│   │   ├── GCI104.asciidoc
│   │   ├── GCI104.json
│   ├── php
│   │   ├── GCI104.asciidoc
│   ├── python
│   │   ├── GCI104.asciidoc
│   └── GCI104.json
├── ...
```

To specify metadata for a given language (for example deprecate a rule only for a single language), it is possible to
create a json file in the language folder, and this will be merged with the common file during build. The keys in the
specific file have priority and it is possible to add new ones but not to delete them from the global one.

#### Description language

The description of the rules uses the ASCIIDOC format (
with [Markdown compatibility](https://docs.asciidoctor.org/asciidoc/latest/syntax-quick-reference/#markdown-compatibility))
in order to allow the inclusion of other pages (this feature is not available in standard with Markdown).

See:

- [AsciiDoc Syntax Quick Reference](https://docs.asciidoctor.org/asciidoc/latest/syntax-quick-reference/)
- [Compare AsciiDoc to Markdown](https://docs.asciidoctor.org/asciidoc/latest/asciidoc-vs-markdown/)

## 🚀 Getting Started

You can quickly explore Creedengo plugins using Docker. Refer to the "Getting Started" section of each plugin for
detailed instructions:

- [Java plugin](https://github.com/green-code-initiative/creedengo-java?tab=readme-ov-file#-getting-started)
- [PHP plugin](https://github.com/green-code-initiative/creedengo-php?tab=readme-ov-file#-getting-started)
- [Python plugin](https://github.com/green-code-initiative/creedengo-python?tab=readme-ov-file#-getting-started)
- [C# plugin](https://github.com/green-code-initiative/creedengo-csharp?tab=readme-ov-file#-getting-started)
- [Android Java plugin](https://github.com/green-code-initiative/ecoCode-android?tab=readme-ov-file#-quickstart)

## 🛒 Distribution

The primary way to obtain Creedengo plugins is through the SonarQube Marketplace, accessible in the Administration
section. Alternatively, you can download them directly from the GitHub releases.

We had split our plugins repository `creedengo` to one repository for each plugin on december 2023.
Thus, plugin versions are available on 2 repositories depending on version you want :

- Java plugin :
    - from 0.x to
      1.4.3 : [creedengo repository](https://github.com/green-code-initiative/creedengo-rules-specifications/releases)
    - since 1.5.0 : [creedengo-java repository](https://github.com/green-code-initiative/creedengo-java/releases)
- PHP plugin :
    - from 0.x to
      1.3.1 : [creedengo repository](https://github.com/green-code-initiative/creedengo-rules-specifications/releases)
    - since 1.4.0 : [creedengo-php repository](https://github.com/green-code-initiative/creedengo-php/releases)
- Python plugin :
    - from 0.x to
      1.3.1 : [creedengo repository](https://github.com/green-code-initiative/creedengo-rules-specifications/releases)
    - since 1.4.0 : [creedengo-python repository](https://github.com/green-code-initiative/creedengo-python/releases)
- Javascript plugin :
    - from 0.x to
      1.3.0 : [creedengo repository](https://github.com/green-code-initiative/creedengo-rules-specifications/releases)
    - since
      1.4.0 : [creedengo-javascript repository](https://github.com/green-code-initiative/creedengo-javascript/releases)
- C# plugin :
    - since 0.x : [creedengo repository](https://github.com/green-code-initiative/creedengo-csharp/releases)
- Android plugin : [creedengo-android repository](https://github.com/green-code-initiative/ecoCode-android/releases)
- iOS plugin : [creedengo-ios repository](https://github.com/green-code-initiative/creedengo-ios/releases)

## 🧩 Plugins version compatibility (OLD `ecocode` plugin)

| Plugins Version     | SonarQube version           | Java version |
|---------------------|-----------------------------|--------------|
| 1.4+                | SonarQube 9.4.+ LTS to 10.1 | 11 / 17      |
| 1.2.x, 1.3.x        | SonarQube 9.4.+ LTS to 10.0 | 11 / 17      |
| 0.2.x, 1.0.x, 1.1.x | SonarQube 9.4.+ LTS to 9.9  | 11 / 17      |
| 0.1.x               | SonarQube 8.9.+ LTS to 9.3  | 11 / 17      |

## 🤝 Contribution

You are a technical expert, a designer, a project manager, a CSR expert, an ecodesign expert...

You want to offer the help of your company, help us to organize, communicate on the project ?

You have ideas to submit to us ?

We are listening to you to make the project progress collectively, and maybe with you !

WE NEED YOU !

Here is the [Starter pack](https://github.com/green-code-initiative/creedengo-common/blob/main/doc/starter-pack.md)

## 🤓 Main contributors

Any question ? We are here for you !
first, create an issue, please.
Then, if no answer, contact ...

- [Jules Delecour](https://www.linkedin.com/in/jules-delecour-498680118/)
- [Geoffrey Lalloué](https://github.com/glalloue)
- [Julien Hertout](https://www.linkedin.com/in/julien-hertout-b1175449/)
- [Justin Berque](https://www.linkedin.com/in/justin-berque-444412140)
- [Olivier Le Goaër](https://olegoaer.perso.univ-pau.fr)
- [David De Carvalho](https://www.linkedin.com/in/david%E2%80%8E-de-carvalho-8b395284/)
- [Maxime Malgorn](https://www.linkedin.com/in/maximemalgorn/)
- [Gilles Grousset](https://www.linkedin.com/in/gillesgrousset/)
- [Vianney De Bellabre](https://www.linkedin.com/in/vianney-de-bellabre/)
- [Jérôme Cardon](https://www.linkedin.com/in/jcardon79/)
- [Johanna Duigou](https://www.linkedin.com/in/johannaduigou/)

## 🧐 Core Team Emeriti

Here we honor some no-longer-active core team members who have made valuable contributions in the past.

- [Maxime Dubois](https://www.linkedin.com/in/maxime-dubois-%F0%9F%8C%B1-649a3a3/)
- Gaël Pellevoizin
- [Nicolas Daviet](https://github.com/NicolasDaviet)
- [Mathilde Grapin](https://github.com/fkotd)

They have contributed to the success of creedengo :

- [Davidson Consulting](https://www.davidson.fr/)
- [Orange Business](https://www.orange-business.com/)
- [Snapp'](https://www.snapp.fr/)
- [Université de Pau et des Pays de l'Adour (UPPA)](https://www.univ-pau.fr/)
- [Solocal](https://www.solocal.com/) / [PagesJaunes.fr](https://www.pagesjaunes.fr/)
- [Capgemini](https://www.capgemini.fr/)
- [Accenture](https://www.accenture.com/)

They supported the project :

- [Région Nouvelle-Aquitaine](https://www.nouvelle-aquitaine.fr/)
