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

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.greencodeinitiative.tools.exporter.infra.RuleReader;
import org.greencodeinitiative.tools.exporter.infra.RuleWriter;

import java.io.IOException;

@Mojo(name = "export")
public class ExportMojo extends AbstractMojo {

    @Parameter(property = "export.resourceArtifactFile", defaultValue = "${project.build.directory}/${project.build.finalName}.jar")
    private String resourceArtifactFile;

    @Parameter(property = "export.outputJsonFile", defaultValue = "${project.build.directory}/rules-json/rules.json")
    private String outputJsonFile;

    @Override
    public void execute() throws MojoExecutionException {
        getLog().info("Reading jar: " + resourceArtifactFile);

        RuleReader reader = new RuleReader(resourceArtifactFile);
        RuleWriter writer = new RuleWriter(outputJsonFile);
        try {
            writer.writeRules(reader.readRules());
            getLog().info("Rules exported successfully to " + outputJsonFile);
        } catch (IOException e) {
            throw new MojoExecutionException("Cannot read or write rules", e);
        }
    }

}
