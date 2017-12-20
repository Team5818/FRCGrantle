package org.rivierarobotics.frcgrantle

import nl.javadude.gradle.plugins.license.License
import org.gradle.api.NamedDomainObjectCollection
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.plugins.ExtraPropertiesExtension
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.compile.JavaCompile
import org.rivierarobotics.frcgrantle.tasks.FirstAntConfig
import org.rivierarobotics.frcgrantle.tasks.FirstCopy
import util.PluginExtension

import static org.rivierarobotics.frcgrantle.Const.*

class StandardScript implements Plugin<Project> {

    @Override
    void apply(Project project) {
        def ext = project.extensions.create('grantle', SSExtension)

        project.apply plugin: "net.ltgt.apt"
        project.apply plugin: "com.github.hierynomus.license"
        project.apply plugin: 'java'
        project.apply plugin: 'eclipse'
        project.apply plugin: 'idea'
        project.apply plugin: 'aversion-util'

        project.afterEvaluate {
            PluginExtension util = (PluginExtension) project.extensions.getByName('util')
            util.javaVersion = ext.javaVersion
            project.idea.project.languageLevel = ext.javaVersion
        }

        project.repositories { RepositoryHandler repo ->
            repo.mavenCentral()
            repo.maven { MavenArtifactRepository mar ->
                mar.name = "FRC Releases"
                mar.url = "http://first.wpi.edu/FRC/roborio/maven/release"
            }
            repo.maven { MavenArtifactRepository mar ->
                mar.name = "5818 Releases"
                mar.url = "https://team5818.github.io/maven/"
            }
            repo.maven { MavenArtifactRepository mar ->
                mar.name = "Kauai Labs Releases"
                mar.url = "https://www.kauailabs.com/maven2"
            }
            repo.maven { MavenArtifactRepository mar ->
                mar.name = "central snapshots"
                mar.url = "https://oss.sonatype.org/content/repositories/snapshots/"
            }
        }

        Configuration frcCompileConf = project.configurations.getByName(FRC_COMPILE)
        Configuration frcNativeConf = null

        // setup lib catchers
        project.configurations { ConfigurationContainer conf ->
            frcNativeConf = conf.create(FRC_NATIVE)
        }

        Set<Dependency> excludedDeps = new HashSet<>()

        FirstAntConfig configTask = project.tasks.create("configureFrcAnt", FirstAntConfig)

        def copyNativeTask = project.tasks.create("copyFrcNativeFiles", FirstCopy) { FirstCopy task ->
            task.unpackJar(true)
            task.configuration(frcNativeConf)
            task.outputDir(configTask.userLibsDir)
            task.excludedDependencies = excludedDeps
            task.expectedExtensions("so")
        }

        def copyCompileTask = project.tasks.create("copyFrcCompileFiles", FirstCopy) { FirstCopy task ->
            task.unpackJar(false)
            task.configuration(frcCompileConf)
            task.outputDir(configTask.userLibsDir)
            task.excludedDependencies = excludedDeps
            task.expectedExtensions("jar")
        }

        def copyFrcTask = project.tasks.create("copyFrcFiles") { Task task -> task.dependsOn(copyCompileTask, copyNativeTask) }

        // setup task dependencies
        copyCompileTask.dependsOn(configTask)
        copyNativeTask.dependsOn(configTask)
        project.tasks.getByName('eclipse').dependsOn(copyFrcTask)

        project.afterEvaluate {
            project.dependencies { DependencyHandler deps ->
                def vs = ext.versionSet

                // Libraries from the FRC maven repo. //
                configTask.cscoreJar = deps.add(FRC_COMPILE, vs.cscore.toMapDependency(classifier: 'arm'))
                excludedDeps.add(configTask.cscoreJar)
                deps.add(FRC_NATIVE, vs.cscoreNative.toMapDependency(classifier: 'athena-uberzip', ext: 'zip'))

                configTask.networkTablesJar = deps.add(FRC_COMPILE, vs.networkTables.toMapDependency(classifier: 'arm'))
                excludedDeps.add(configTask.networkTablesJar)

                configTask.opencvJar = deps.add(FRC_COMPILE, vs.opencv.toMapDependency())
                excludedDeps.add(configTask.opencvJar)
                deps.add(FRC_NATIVE, vs.opencvNative.toMapDependency(classifier: 'linuxathena'))

                configTask.wpilibJar = deps.add(FRC_COMPILE, vs.wpilib.toMapDependency())
                excludedDeps.add(configTask.wpilibJar)
                deps.add(FRC_NATIVE, vs.wpilibNative.toMapDependency())
                deps.add(FRC_NATIVE, vs.wpilibRuntime.toMapDependency(ext: 'zip'))

                // Libraries from the 5818 maven repo. //
                deps.add(FRC_COMPILE, vs.ctrLib.toMapDependency())
                deps.add(FRC_NATIVE, vs.ctrLibNative.toMapDependency(ext: 'zip'))

                // Other //
                deps.add(FRC_COMPILE, vs.navx.toMapDependency())
            }
        }

        // Add eclipse FRC nature
        project.eclipse.project.file.beforeMerged {
            proj ->
                proj.natures.add('edu.wpi.first.wpilib.plugins.core.nature.FRCProjectNature')
        }

        // Move source into src
        project.sourceSets.getByName('main') {
            it.java.srcDirs = it.resources.srcDirs = ['src']
        }

        // Setup compiling to be better.
        project.configure([project.compileJava, project.compileTestJava]) { JavaCompile compile ->
            compile.options.compilerArgs += ['-Xlint:all', '-Xlint:-path']
            compile.options.deprecation = true
            compile.options.encoding = 'UTF-8'
            compile.options.incremental = true
            compile.options.fork = true
        }

        // License configuration

        project.tasks.withType(License.class).each { License l ->
            ExtraPropertiesExtension licenseExt = l.extensions.getByType(ExtraPropertiesExtension.class)
            licenseExt.set('name', project.name)
            licenseExt.set('organization', project.organization)
            licenseExt.set('url', project.url)

            l.header = project.rootProject.file('HEADER.txt')
            l.ignoreFailures = false
            l.strictCheck = true
            l.include('**/*.java')
            l.mapping(java: 'SLASHSTAR_STYLE')
        }
    }


}
