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
                '5.2.1.1' // ctr
        )
    }

    def versionSet_2018_3_1() {
        versionSet_2018(
                '2018.3.1', // wpi
                '3.1.0', // wpiUtil
                '3.2.0', // openCv
                '1.2.0', // csCore
                '4.0.0', // ntCore
                '5.2.1.1' // ctr
        )
    }

    def versionSet_2018_3_3() {
        versionSet_2018(
                '2018.3.3', // wpi
                '3.1.0', // wpiUtil
                '3.2.0', // openCv
                '1.2.0', // csCore
                '4.0.0', // ntCore
                '5.3.1.0' // ctr
        )
    }

    def versionSet_2018_4_1() {
        versionSet_2018(
                '2018.4.1', // wpi
                '3.2.0', // wpiUtil
                '3.2.0', // openCv
                '1.3.0', // csCore
                '4.1.0', // ntCore
                '5.3.1.0' // ctr
        )
    }

    def versionSet_2018(String wpi, String wpiUtil, String openCv, String csCore, String ntCore, String ctr) {
        versionSet.addFirstLibrary('wpilib', SimpleDep.WPILIB.withVersion(wpi),
                [LibraryKind.builtInJava(), LibraryKind.nativeKind('jniShared', 'jar')])
        versionSet.addUserLibrary(SimpleDep.HAL_NATIVE.withVersion(wpi),
                [LibraryKind.nativeKind('', 'zip')])

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

    def usePathfinder(String version) {
        versionSet.addUserLibrary(SimpleDep.PATHFINDER.withVersion(version),
                [LibraryKind.javaKind('Java'),
                 LibraryKind.of('JNI', 'athena', 'zip')])
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
