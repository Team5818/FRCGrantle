package org.rivierarobotics.frcgrantle.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.Dependency
import org.gradle.api.file.CopySpec
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

/**
 * Copy task dedicated to copying files from dependency sets to FIRST-specified locations.
 */
class FirstCopy extends DefaultTask {

    private Configuration configuration
    private File outputDir
    private boolean unpackJar
    @Internal
    Set<Dependency> excludedDependencies

    {
        group = 'FRC'
    }

    @InputFiles
    Closure<Set<File>> getInputFiles() {
        return {
            Set<Dependency> deps = new HashSet<>(configuration.dependencies)
            deps.removeAll(excludedDependencies)
            return configuration.files(deps.toArray(new Dependency[0]))
        }
    }

    @Input
    Configuration getConfiguration() {
        return configuration
    }

    void setConfiguration(Configuration configuration) {
        this.configuration = configuration
    }

    void configuration(Configuration configuration) {
        setConfiguration(configuration)
    }

    @Input
    boolean getUnpackJar() {
        return unpackJar
    }

    void setUnpackJar(boolean unpackJar) {
        this.unpackJar = unpackJar
    }

    void unpackJar(boolean unpackJar) {
        setUnpackJar(unpackJar)
    }

    @OutputDirectory
    File getOutputDir() {
        return outputDir
    }

    void setOutputDir(Object outputDir) {
        this.outputDir = getProject().file(outputDir)
    }

    void outputDir(Object outputDir) {
        setOutputDir(outputDir)
    }

    @TaskAction
    void copy() {
        def didWork = inputs.files.any { file ->
            // assumes well-formed extensions... lol
            if (!file.name.contains(".")) {
                throw new IllegalStateException("Files should have an extension")
            }
            logger.info("Copying", file)
            def files = project.files(file)
            def ext = file.name.split("\\.").last()
            switch (ext) {
                case "jar":
                    if (!unpackJar) {
                        break
                    }
                    files = project.zipTree(file)
                    break
                case "zip":
                    files = project.zipTree(file)
            }
            def workResult = project.copy { CopySpec copy ->
                copy.from(files)
                copy.into(getOutputDir())
            }
            return workResult.didWork
        }
        setDidWork(didWork)
    }

}
