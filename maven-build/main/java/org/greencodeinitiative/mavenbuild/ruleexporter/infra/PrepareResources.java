package org.greencodeinitiative.mavenbuild.ruleexporter.infra;

import org.greencodeinitiative.mavenbuild.ruleexporter.domain.RuleDescriptionFile;
import org.greencodeinitiative.mavenbuild.ruleexporter.domain.RuleMetadata;
import org.greencodeinitiative.mavenbuild.ruleexporter.util.FileHelper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PrepareResources implements Runnable {

    private static final System.Logger LOGGER = System.getLogger("PrepareResources");

    private final Path sourceDir;
    private final Path targetDir;
    private final Path indexJsonFile;
    private final RuleFactory ruleFactory;

    public PrepareResources(Path sourceDir, Path targetDir, Path indexJsonFile) {
        this.sourceDir = sourceDir;
        this.targetDir = targetDir;
        this.indexJsonFile = indexJsonFile;
        this.ruleFactory = new RuleFactory();
    }

    @Override
    public void run() {
        List<RuleDescriptionFile> ruleFiles = getResourcesToCopy();

        // Prepare resources (copy and merge files)
        ruleFiles.forEach(ruleFile -> {
            FileHelper.mergeOrCopyJsonMetadata(
                    ruleFile.getMetadataPath(),
                    ruleFile.getSpecificMetadataPath(),
                    ruleFile.getMetadataTargetPath(targetDir)
            );
            FileHelper.copyFile(ruleFile.getPath(), ruleFile.getHtmlDescriptionTargetPath(targetDir));
        });

        // Optionally export index.json
        if (indexJsonFile != null) {
            exportIndexJson(ruleFiles, indexJsonFile);
        }
    }

    private void exportIndexJson(List<RuleDescriptionFile> ruleFiles, Path outputFile) {
        try {
            Collection<RuleMetadata> ruleMetadatas = ruleFiles
                    .stream()
                    .map(ruleFactory::toRuleMetadata)
                    .collect(Collectors.toList());

            RuleMetadataWriter writer = new RuleMetadataWriter(outputFile.toString());
            writer.writeRules(ruleMetadatas);

            LOGGER.log(System.Logger.Level.INFO, "{0} rules exported successfully to {1}", ruleFiles.size(), outputFile);
        } catch (IOException e) {
            throw new ProcessException("Cannot export index.json", e);
        }
    }

    private List<RuleDescriptionFile> getResourcesToCopy() {
        try (Stream<Path> stream = Files.walk(sourceDir)) {
            return stream
                    .filter(Files::isRegularFile)
                    .map(ruleFactory::createFromHtmlDescription)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read file", e);
        }
    }
}
