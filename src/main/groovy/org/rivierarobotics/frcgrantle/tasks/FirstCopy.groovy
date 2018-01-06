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

    private Set<String> expectedExtensions = new HashSet<>()
    private Configuration configuration
    private File outputDir
    private boolean unpackJar
    @Internal
    Set<Dependency> excludedDependencies

    {
        group = 'FRC'
    }

    @Input
    Set<String> getExpectedExtensions() {
        return expectedExtensions
    }

    void setExpectedExtensions(Set<String> expectedExtensions) {
        this.expectedExtensions = expectedExtensions
    }

    void expectedExtensions(String... extensions) {
        expectedExtensions.addAll(extensions)
    }

    @InputFiles
    Closure<Set<File>> getInputFiles() {
        return {
            Set<Dependency> deps = configuration.dependencies.findAll { dep ->
                return !excludedDependencies.any { excl -> dep.group == excl.group && dep.name == excl.name && dep.version == excl.version }
            }
            def copyInputFiles = new HashSet<>(configuration.files(deps.toArray(new Dependency[0])))
            def dontUseInputFiles = configuration.files(excludedDependencies.toArray(new Dependency[0]))
            copyInputFiles.removeAll(dontUseInputFiles)
            return copyInputFiles
        }
    }

    @Internal
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
        inputs.files.each { file ->
            // assumes well-formed extensions... lol
            if (!file.name.contains(".")) {
                throw new IllegalStateException("Files should have an extension")
            }
            logger.debug("Copying ${file}")
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
            files.filter { f ->
                def extension = f.name.split("\\.").last()
                return expectedExtensions.contains(extension)
            }.each { f ->
                f.withInputStream { input ->
                    def outputFile = new File(getOutputDir(), f.name)
                    outputFile.withOutputStream { output ->
                        output << input
                    }
                }
            }
        }
    }

}
