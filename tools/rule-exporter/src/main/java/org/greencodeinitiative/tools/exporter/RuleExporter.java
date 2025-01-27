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
package org.greencodeinitiative.tools.exporter;

import org.greencodeinitiative.tools.exporter.infra.RuleReader;
import org.greencodeinitiative.tools.exporter.infra.RuleWriter;

import java.io.IOException;

public class RuleExporter {

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.out.println("Usage: java -jar rule-exporter.jar <input jar> <output json>");
            System.exit(1);
        }

        RuleReader reader = new RuleReader(args[0]);
        RuleWriter writer = new RuleWriter(args[1]);
        writer.writeRules(reader.readRules());
    }

}
