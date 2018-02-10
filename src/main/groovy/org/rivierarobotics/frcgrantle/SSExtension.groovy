package org.rivierarobotics.frcgrantle
/**
 * The great ship S.S. Extension. It sails the seas of Gradloria, in search of proper configuration values.
 */
class SSExtension {

    def versionSet_2018_2_2() {
        versionSet_2018(
                '2018.2.2', // wpi
                '3.0.0', // wpiUtil
                '3.2.0', // openCv
                '1.1.0', // csCore
                '4.0.0', // ntCore
                '5.2.1.1', // ctr
                '3.0.346' // navX
        )
    }

    def versionSet_2018(String wpi, String wpiUtil, String openCv, String csCore, String ntCore, String ctr, String navX) {
        versionSet.addFirstLibrary('wpilib', SimpleDep.WPILIB.withVersion(wpi),
                [LibraryKind.builtInJava(), LibraryKind.nativeKind('jniShared', 'jar')])
        versionSet.addBuiltInNativeLibrary(SimpleDep.HAL_NATIVE.withVersion(wpi))

        versionSet.addFirstLibrary('wpiutil', SimpleDep.WPI_UTIL.withVersion(wpiUtil),
                [LibraryKind.builtInJava(), LibraryKind.cpp()])

        versionSet.addFirstLibrary('opencv', SimpleDep.OPENCV.withVersion(openCv),
                [LibraryKind.builtInJava(), LibraryKind.jni(), LibraryKind.cpp()])

        versionSet.addFirstLibrary('cscore', SimpleDep.CSCORE.withVersion(csCore),
                [LibraryKind.builtInJava(), LibraryKind.cpp()])

        versionSet.addFirstLibrary('ntcore', SimpleDep.NTCORE.withVersion(ntCore),
                [LibraryKind.builtInJava(), LibraryKind.cpp()])

        versionSet.addUserLibrary(SimpleDep.CTR_LIB.withVersion(ctr),
                [LibraryKind.builtInJava(), LibraryKind.cpp()])

        appliedVersionConfiguration = true
    }

    String javaVersion = '1.8'
    FirstVersionSet versionSet = FirstVersionSet.create()
    boolean appliedVersionConfiguration = false
    String packageBase
    int teamNumber = 0

    def javaVersion(String value) {
        javaVersion = value
    }

    def versionSet(FirstVersionSet value) {
        versionSet = value
        appliedVersionConfiguration = true
    }

    def packageBase(String value) {
        packageBase = value
    }

    def validate() {
        check(javaVersion != null, "javaVersion cannot be null")
        check(versionSet != null, "versionSet cannot be null")
        check(packageBase != null, "packageBase cannot be null")
        check(appliedVersionConfiguration, "version configuration not applied, please call a versionSet method or set `appliedVersionConfiguration` to true")
        check(teamNumber > 0, "teamNumber must be set")
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new IllegalStateException(message)
        }
    }

}
