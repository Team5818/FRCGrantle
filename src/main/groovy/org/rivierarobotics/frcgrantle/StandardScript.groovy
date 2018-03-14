package org.rivierarobotics.frcgrantle

import nl.javadude.gradle.plugins.license.License
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
import org.gradle.api.tasks.compile.JavaCompile
import org.rivierarobotics.frcgrantle.tasks.FirstAntConfig
import org.rivierarobotics.frcgrantle.tasks.FirstCopy
import util.PluginExtension

import static org.rivierarobotics.frcgrantle.Const.*
import static org.rivierarobotics.frcgrantle.Util.ifNonNull

class StandardScript implements Plugin<Project> {

    @Override
    void apply(Project project) {
        def ext = project.extensions.create(EXTENSION, SSExtension)

        project.apply plugin: "net.ltgt.apt"
        project.apply plugin: "com.github.hierynomus.license"
        project.apply plugin: 'java'
        project.apply plugin: 'eclipse'
        project.apply plugin: 'idea'
        project.apply plugin: 'aversion-util'

        // to avoid conflicts with ant, which also uses build
        project.buildDir = project.file('gradleBuildDir')

        project.afterEvaluate {
            PluginExtension util = (PluginExtension) project.extensions.getByName('util')
            ext.validate()
            util.javaVersion = ext.javaVersion
            project.idea.project.languageLevel = ext.javaVersion
        }

        project.repositories { RepositoryHandler repo ->
            repo.mavenCentral()
            repo.maven { MavenArtifactRepository mar ->
                mar.name = "FRC Releases"
                mar.url = "http://first.wpi.edu/FRC/roborio/maven/release"
                mar.metadataSources({ ms ->
                    ms.mavenPom()
                })
            }
            // For pathfinder
            repo.maven { MavenArtifactRepository mar ->
                mar.name = "Jaci Releases"
                mar.url = "http://dev.imjac.in/maven"
                mar.metadataSources({ ms ->
                    ms.mavenPom()
                })
            }
            repo.maven { MavenArtifactRepository mar ->
                mar.name = "5818 Releases"
                mar.url = "https://team5818.github.io/maven/"
                mar.metadataSources({ ms ->
                    ms.artifact()
                })
            }
            repo.maven { MavenArtifactRepository mar ->
                mar.name = "central snapshots"
                mar.url = "https://oss.sonatype.org/content/repositories/snapshots/"
                mar.metadataSources({ ms ->
                    ms.mavenPom()
                })
            }
        }

        Configuration frcCompileConf = project.configurations.getByName(FRC_COMPILE)
        Configuration frcNativeConf = null
        Configuration frcBuiltInNativeConf = null

        // setup lib catchers
        project.configurations { ConfigurationContainer conf ->
            frcNativeConf = conf.create(FRC_NATIVE)
            frcBuiltInNativeConf = conf.create(FRC_BUILT_IN_NATIVE)
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

        def copyBuiltInNativeTask = project.tasks.create("copyFrcBuiltInNativeFiles", FirstCopy) { FirstCopy task ->
            task.unpackJar(true)
            task.configuration(frcBuiltInNativeConf)
            task.outputDir(configTask.wpilibNativeDir)
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

        def copyFrcTask = project.tasks.create("copyFrcFiles") { Task task -> task.dependsOn(copyCompileTask, copyNativeTask, copyBuiltInNativeTask) }

        // setup task dependencies
        copyCompileTask.dependsOn(configTask)
        copyNativeTask.dependsOn(configTask)
        copyBuiltInNativeTask.dependsOn(configTask)
        project.tasks.getByName('eclipse').dependsOn(copyFrcTask)

        project.afterEvaluate {
            project.dependencies { DependencyHandler deps ->
                def vs = ext.versionSet

                vs.builtIn.forEach { propKey, simpleDep ->
                    def dep = deps.add(FRC_COMPILE, simpleDep.toMapDependency())
                    configTask.builtInJars.put(propKey, dep)
                    excludedDeps.add(dep)
                }

                vs.userJava.forEach { simpleDep ->
                    deps.add(FRC_COMPILE, simpleDep.toMapDependency())
                }

                vs.userNative.forEach { simpleDep ->
                    deps.add(FRC_NATIVE, simpleDep.toMapDependency())
                }

                vs.builtInNative.forEach { simpleDep ->
                    deps.add(FRC_BUILT_IN_NATIVE, simpleDep.toMapDependency())
                }
            }
        }

        // Add eclipse FRC nature
        project.eclipse.project.file.beforeMerged { proj ->
            proj.natures.add('edu.wpi.first.wpilib.plugins.core.nature.FRCProjectNature')
        }

        // Move source into src
        project.sourceSets.getByName('main') {
            it.java.srcDirs = it.resources.srcDirs = ['src']
        }
        // Move tests into src-tests
        project.sourceSets.getByName('test') {
            it.java.srcDirs = it.resources.srcDirs = ['src-tests']
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
