/*
 * Creedengo Rule Exporter - Export all rules to JSON files usable by the website
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
package org.greencodeinitiative.tools.exporter;

import org.greencodeinitiative.tools.exporter.infra.RuleReader;
import org.greencodeinitiative.tools.exporter.infra.RuleWriter;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Exporter {

    private static final Logger LOGGER = Logger.getLogger(Exporter.class.getName());

    public static void main(String[] args) {
        if (args.length != 2) {
            throw new IllegalArgumentException("Usage: java -jar rule-exporter.jar <resourceArtifactFile> <outputJsonFile>");
        }

        String resourceArtifactFile = args[0];
        String outputJsonFile = args[1];

        RuleReader reader = new RuleReader(resourceArtifactFile);
        RuleWriter writer = new RuleWriter(outputJsonFile);
        try {
            writer.writeRules(reader.readRules());
            LOGGER.log(Level.INFO, "Rules exported successfully to {0}", outputJsonFile);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read or write rules", e);
        }
    }

}
