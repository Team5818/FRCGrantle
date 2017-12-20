package org.rivierarobotics.frcgrantle.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.ResolvedConfiguration
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

import static org.rivierarobotics.frcgrantle.Const.FRC_COMPILE

class FirstAntConfig extends DefaultTask {

    private File userLibsDir = project.file('.frclibs/user')
    private File wpilibNativeDir = project.file('.frclibs/wpilib')
    private Dependency cscoreJar
    private Dependency networkTablesJar
    private Dependency opencvJar
    private Dependency wpilibJar

    {
        group = 'FRC'
    }

    @Input
    File getUserLibsDir() {
        return userLibsDir
    }

    void setUserLibsDir(File userLibsDir) {
        this.userLibsDir = userLibsDir
    }

    void userLibsDir(File userLibsDir) {
        setUserLibsDir(userLibsDir)
    }

    @Input
    File getWpilibNativeDir() {
        return wpilibNativeDir
    }

    void setWpilibNativeDir(File wpilibNativeDir) {
        this.wpilibNativeDir = wpilibNativeDir
    }

    void wpilibNativeDir(File wpilibNativeDir) {
        setWpilibNativeDir(wpilibNativeDir)
    }

    @Input
    Dependency getCscoreJar() {
        return cscoreJar
    }

    void setCscoreJar(Dependency cscoreJar) {
        this.cscoreJar = cscoreJar
    }

    void cscoreJar(Dependency cscoreJar) {
        setCscoreJar(cscoreJar)
    }

    @Input
    Dependency getNetworkTablesJar() {
        return networkTablesJar
    }

    void setNetworkTablesJar(Dependency networkTablesJar) {
        this.networkTablesJar = networkTablesJar
    }

    void networkTablesJar(Dependency networkTablesJar) {
        setNetworkTablesJar(networkTablesJar)
    }

    @Input
    Dependency getOpencvJar() {
        return opencvJar
    }

    void setOpencvJar(Dependency opencvJar) {
        this.opencvJar = opencvJar
    }

    void opencvJar(Dependency opencvJar) {
        setOpencvJar(opencvJar)
    }

    @Input
    Dependency getWpilibJar() {
        return wpilibJar
    }

    void setWpilibJar(Dependency wpilibJar) {
        this.wpilibJar = wpilibJar
    }

    void wpilibJar(Dependency wpilibJar) {
        setWpilibJar(wpilibJar)
    }

    private static void ensureDirectoryExists(File dir) {
        if (!dir.mkdirs()) {
            throw new IllegalStateException("Unable to create directory ${dir.absolutePath}")
        }
    }

    @TaskAction
    void configureFirstAnt() {
        ensureDirectoryExists(userLibsDir)
        ensureDirectoryExists(wpilibNativeDir)
        def antProperties = new Properties()
        antProperties['userLibs.dir'] = userLibsDir.absolutePath
        antProperties['wpilib.native.lib'] = wpilibNativeDir.absolutePath

        // grab libraries
        def compile = project.configurations.getByName(FRC_COMPILE)
        antProperties['cscore.jar'] = compile.files(cscoreJar).first()
        antProperties['networktables.jar'] = compile.files(networkTablesJar).first()
        antProperties['opencv.jar'] = compile.files(opencvJar).first()
        antProperties['wpilib.jar'] = compile.files(wpilibJar).first()
    }
}
