# Maven Build Module

This directory contains processing code used during the Maven build.

## Overview

The `maven-build` module provides functionality to prepare rule resources by copying and merging JSON metadata files and HTML descriptions. It can also optionally export a consolidated index.json file in a single pass.

## Output Format

When index.json export is enabled, the generated file has the following structure:

```json
{
  "items": [
    {
      "id": "GCI1",
      "name": "Rule Title",
      "severity": "MAJOR",
      "technologies": ["java", "javascript"],
      "status": "READY"
    }
  ],
  "meta": {
    "technologies": ["java", "javascript", "python"],
    "severities": ["CRITICAL", "INFO", "MAJOR", "MINOR"],
    "statuses": ["DEPRECATED", "READY"]
  }
}
```

## Architecture

This module follows **Domain-Driven Design (DDD)** principles:

### Domain Layer
- **Pure domain models** with no external dependencies
- Business logic and rules validation
- Technology-agnostic

### Infrastructure Layer
- File system operations
- JSON parsing and writing
- External dependencies (Jakarta JSON, NIO)
- Domain object factories and mappers

## Dependencies

- Jakarta JSON API (jakarta.json:jakarta.json-api)
- Glassfish JSON Implementation (org.glassfish:jakarta.json)
- JUnit 5 (testing)
- AssertJ (testing)
