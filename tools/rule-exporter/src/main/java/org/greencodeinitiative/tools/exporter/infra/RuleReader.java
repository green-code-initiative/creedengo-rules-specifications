/*
 * ecoCode Rule Exporter - Export all rules to JSON files usable by the website
 * Copyright © 2024 Green Code Initiative (https://www.ecocode.io)
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
package org.greencodeinitiative.tools.exporter.infra;

import org.greencodeinitiative.tools.exporter.domain.RuleMetadata;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

import java.io.File;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class RuleReader {

    private final String zipFilename;

    public RuleReader(String zipFilename) {
        this.zipFilename = zipFilename;
    }

    private static FilterInputStream getContentsInFile(ZipInputStream inputStream) {
        return new FilterInputStream(inputStream) {
            @Override
            public void close() throws IOException {
                inputStream.closeEntry();
            }
        };
    }

    public Collection<RuleMetadata> readRules() throws IOException {
        return readAllMetadataFromZip().entrySet().stream()
                .map(entry -> JsonMapper.toRuleMetadata(entry.getKey(), entry.getValue()))
                .collect(Collectors.toSet());
    }

    private Map<String, JsonObject> readAllMetadataFromZip() throws IOException {
        Map<String, JsonObject> metadataMap = new HashMap<>();
        Path path = new File(zipFilename).toPath();
        try (
                InputStream inputStream = Files.newInputStream(path);
                ZipInputStream zipIn = new ZipInputStream(inputStream)) {
            ZipEntry entry;

            while ((entry = zipIn.getNextEntry()) != null) {
                if (!entry.getName().endsWith(".json") || entry.getName().startsWith("__MACOSX")) {
                    zipIn.closeEntry();
                    continue;
                }
                metadataMap.put(entry.getName(), readContents(getContentsInFile(zipIn)));
            }
        }

        return metadataMap;
    }

    private JsonObject readContents(InputStream contentsIn) {
        try (JsonReader reader = Json.createReader(contentsIn)) {
            return reader.readObject();
        }
    }

}
