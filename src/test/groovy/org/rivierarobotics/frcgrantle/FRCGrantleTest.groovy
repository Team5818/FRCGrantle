package org.rivierarobotics.frcgrantle

import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification
import spock.lang.Unroll

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class FRCGrantleTest extends Specification {
    private static final def FVS_VERSIONS = [
            "2018.2.2",
            "2018.3.1",
            "2018.3.3",
            "2018.4.1"
    ]
    @Rule
    final TemporaryFolder testProjectDir = new TemporaryFolder()
    File buildFile

    def newBuildFile(String versionString, String pathfinder = null) {
        buildFile = testProjectDir.newFile('build.gradle')
        buildFile << """
            plugins {
                id 'org.rivierarobotics.frcgrantle'
            }
            grantle.packageBase = "org.rivierarobotics.robot"
            grantle.teamNumber = 5818
            grantle.versionSet_${versionString.replace('.', '_')}()
        """
        if (pathfinder != null) {
            buildFile << """
            grantle.usePathfinder("${pathfinder}")
        """
        }
        def props = testProjectDir.newFile('gradle.properties')
        props << """
            organization=test
            url=http://example.com
        """
    }

    @Unroll("Grantle configures build.properties for #versionString")
    def "configures FRC build.properties"() {
        when:
        newBuildFile(versionString)
        def result = GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withArguments('configureFrcAnt', '-Si')
                .withPluginClasspath()
                .build()

        then:
        assert result.task(":configureFrcAnt").outcome == SUCCESS
        with(loadPropertiesFile()) { Properties props ->
            assert props.containsKey('userLibs.dir')
            assert props.containsKey('wpilib.native.lib')
            assert props.containsKey('cscore.jar')
            assert props.containsKey('ntcore.jar')
            assert props.containsKey('opencv.jar')
            assert props.containsKey('wpilib.jar')
            assert props.containsKey('wpiutil.jar')
            assert props.getOrDefault('team-number', '') == '5818'
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

    @Unroll("Grantle installs FRC libraries for #versionString")
    def "installs FRC libraries"() {
        when:
        newBuildFile(versionString)
        def result = GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withArguments('copyFrcFiles', '-Si')
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

    /*
     * This test verifies that all library files have their respective libraries also resolved.
     */
    @Unroll("Grantle installs native dependencies completely for #versionString")
    def "installs native libraries correctly"() {
        when:
        newBuildFile(versionString)
        def result = GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withArguments('copyFrcFiles', '-Si')
                .withPluginClasspath()
                .build()

        then:
        assert result.task(":copyFrcFiles").outcome == SUCCESS
        with(loadPropertiesFile()) { Properties props ->
            def userLibs = new File((String) props['userLibs.dir'])
            def wpiLibs = new File((String) props['wpilib.native.lib'])
            assert userLibs.exists()
            assert userLibs.list().length > 0
            def unsat = NativeDepChecker.getUnsatisfiedDependencies([userLibs.toPath(), wpiLibs.toPath()])
            if (!unsat.isEmpty()) {
                userLibs.list().each { println it }
                assert unsat.isEmpty()
            }
        }

        where:
        versionString << FVS_VERSIONS
    }

    def "Grantle includes pathfinder if asked"() {
        when:
        newBuildFile("2018.4.1", "1.8")
        def result = GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withArguments('copyFrcFiles', '-Si')
                .withPluginClasspath()
                .build()

        then:
        assert result.task(":copyFrcFiles").outcome == SUCCESS
        with(loadPropertiesFile()) { Properties props ->
            def userLibs = new File((String) props['userLibs.dir'])
            assert userLibs.exists()
            assert userLibs.list().length > 0
            assert userLibs.list().any { f -> f.contains("libpathfinderjava.so") }
            assert userLibs.list().any { f -> f.contains("Pathfinder") && f.endsWith(".jar") }
        }
    }
}
