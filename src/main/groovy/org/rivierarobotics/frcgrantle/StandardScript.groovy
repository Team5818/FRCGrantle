package org.rivierarobotics.frcgrantle

import nl.javadude.gradle.plugins.license.License
import org.gradle.api.NamedDomainObjectCollection
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.plugins.ExtraPropertiesExtension
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.compile.JavaCompile
import util.PluginExtension

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

        // setup lib catchers
        project.configurations { ConfigurationContainer conf ->
            conf.create('frcCompile')
            conf.create('frcNative')
        }

        project.afterEvaluate {
            project.dependencies {
                def vs = ext.versionSet

                // Libraries from the FRC maven repo. //
                frcCompile vs.cscore.toMapDependency(classifier: 'arm')
                frcNative vs.cscoreNative.toMapDependency(classifier: 'athena-uberzip', ext: 'zip')

                frcCompile vs.networkTables.toMapDependency(classifier: 'arm')
                frcCompile vs.networkTables.toMapDependency(classifier: 'desktop')

                frcCompile vs.opencv.toMapDependency()
                frcNative vs.opencvNative.toMapDependency(classifier: 'linuxathena')

                frcCompile vs.wpilib.toMapDependency()
                frcNative vs.wpilibNative.toMapDependency()
                frcNative vs.wpilibRuntime.toMapDependency(ext: 'zip')

                // Libraries from the 5818 maven repo. //
                frcCompile vs.ctrLib.toMapDependency()
                frcNative vs.ctrLibNative.toMapDependency(ext: 'so')

                // Other //
                frcCompile vs.navx.toMapDependency()
            }
        }

        // Add eclipse FRC nature
        project.eclipse.project.file.beforeMerged {
            proj ->
                proj.natures.add('edu.wpi.first.wpilib.plugins.core.nature.FRCProjectNature')
        }

        // Move source into src
        def sourceSets = (NamedDomainObjectCollection<SourceSet>) project.extensions.getByName("sourceSets")
        sourceSets.getByName('main') {
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
