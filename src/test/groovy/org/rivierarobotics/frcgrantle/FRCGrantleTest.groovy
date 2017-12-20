package org.rivierarobotics.frcgrantle

import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class FRCGrantleTest extends Specification {
    @Rule
    final TemporaryFolder testProjectDir = new TemporaryFolder()
    File buildFile

    def setup() {
        buildFile = testProjectDir.newFile('build.gradle')
        buildFile << """
            plugins {
                id 'org.rivierarobotics.frcgrantle'
            }
        """
        def props = testProjectDir.newFile('gradle.properties')
        props << """
            organization=test
            url=http://example.com
        """
    }

    def "configures FRC build.properties"() {
        when:
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
    }

    private Properties loadPropertiesFile() {
        def buildPropertiesFile = new File(testProjectDir.root, 'build.properties')
        def props = new Properties()
        buildPropertiesFile.withInputStream { props.load(it) }
        props
    }

    def "installs FRC libraries"() {
        when:
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
    }
}