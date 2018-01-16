package org.rivierarobotics.frcgrantle
/**
 * The great ship S.S. Extension. It sails the seas of Gradloria, in search of proper configuration values.
 */
class SSExtension {

    def versionSet_2017_3_1() {
        versionSet.addBuiltInLibrary('wpilib', SimpleDep.WPILIB_2017.withVersion('2017.3.1'))
        versionSet.addUserNativeLibrary(SimpleDep.WPILIB_2017_NATIVE.withVersion('2017.3.1'))
        versionSet.addUserNativeLibrary(SimpleDep.WPILIB_2017_RUNTIME.withVersion('2017.3.1'))

        versionSet.addNewStyleBuiltInLibrary('opencv', SimpleDep.OPENCV.withVersion('3.2.0'))

        versionSet.addBuiltInLibrary('cscore', SimpleDep.CSCORE.withVersion('1.0.2').withInputOverrides(classifier: 'arm'))
        versionSet.addUserNativeLibrary(SimpleDep.CSCORE_NATIVE.withVersion('1.0.2').withInputOverrides(classifier: 'athena-uberzip', ext: 'zip'))

        versionSet.addBuiltInLibrary('networktables', SimpleDep.NETWORK_TABLES.withVersion('3.1.7').withInputOverrides(classifier: 'arm'))

        versionSet.addUserJavaLibrary(SimpleDep.CTR_LIB.withVersion('4.4.1.14'))
        versionSet.addUserNativeLibrary(SimpleDep.CTR_LIB_NATIVE.withVersion('4.4.1.14').withInputOverrides(ext: 'zip'))

        versionSet.addUserJavaLibrary(SimpleDep.NAVX.withVersion('NONE'))
        appliedVersionConfiguration = true
    }

    def versionSet_2018_1_1() {
        versionSet.addNewStyleBuiltInLibrary('wpilib', SimpleDep.WPILIB_2018.withVersion('2018.1.1'))
        versionSet.addUserNativeLibrary(SimpleDep.WPILIB_2018_RUNTIME.withVersion('2018.1.1').withInputOverrides(classifier: 'linuxathena'))

        versionSet.addBuiltInLibrary('wpiutil', SimpleDep.WPI_UTIL.withVersion('3.0.0'))

        versionSet.addNewStyleBuiltInLibrary('opencv', SimpleDep.OPENCV.withVersion('3.2.0'))

        versionSet.addNewStyleBuiltInLibrary('cscore', SimpleDep.CSCORE_2018.withVersion('1.1.0'))

        versionSet.addNewStyleBuiltInLibrary('ntcore', SimpleDep.NTCORE.withVersion('4.0.0'))

        versionSet.addUserJavaLibrary(SimpleDep.CTR_LIB.withVersion('5.1.3.1'))
        versionSet.addUserNativeLibrary(SimpleDep.CTR_LIB_NATIVE.withVersion('5.1.3.1').withInputOverrides(ext: 'zip'))

        versionSet.addUserJavaLibrary(SimpleDep.NAVX.withVersion('3.0.342'))
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
