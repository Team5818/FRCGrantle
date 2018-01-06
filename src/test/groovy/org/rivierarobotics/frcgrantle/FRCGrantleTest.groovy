package org.rivierarobotics.frcgrantle

import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class FRCGrantleTest extends Specification {
    private static final def FVS_VERSIONS = [
            "V_2017_3_1",
            "V_2018_1_1"
    ]
    @Rule
    final TemporaryFolder testProjectDir = new TemporaryFolder()
    File buildFile

    def newBuildFile(String versionString) {
        buildFile = testProjectDir.newFile('build.gradle')
        buildFile << """
            plugins {
                id 'org.rivierarobotics.frcgrantle'
            }
            grantle.packageBase = "org.rivierarobotics.robot"
            grantle.versionSet = grantle.${versionString}
        """
        def props = testProjectDir.newFile('gradle.properties')
        props << """
            organization=test
            url=http://example.com
        """
    }

    def "configures FRC build.properties"() {
        when:
        newBuildFile(versionString)
        def result = GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withArguments('configureFrcAnt', '-S')
                .withPluginClasspath()
                .build()

        then:
        assert result.task(":configureFrcAnt").outcome == SUCCESS
        with(loadPropertiesFile()) { Properties props ->
            assert props.containsKey('userLibs.dir')
            assert props.containsKey('wpilib.native.lib')
            assert props.containsKey('cscore.jar')
            assert props.containsKey('networktables.jar')
            assert props.containsKey('opencv.jar')
            assert props.containsKey('wpilib.jar')
        }

        where:
        versionString << FVS_VERSIONS
    }

    private Properties loadPropertiesFile() {
        def buildPropertiesFile = new File(testProjectDir.root, 'build.properties')
        def props = new Properties()
        buildPropertiesFile.withInputStream { props.load(it) }
        props
    }

    def "installs FRC libraries"() {
        when:
        newBuildFile(versionString)
        def result = GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withArguments('copyFrcFiles', '-S')
                .withPluginClasspath()
                .build()

        then:
        assert result.task(":copyFrcFiles").outcome == SUCCESS
        with(loadPropertiesFile()) { Properties props ->
            def userLibs = new File((String) props['userLibs.dir'])
            assert userLibs.exists()
            assert userLibs.list().length > 0
        }

        where:
        versionString << FVS_VERSIONS
    }
}