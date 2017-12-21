package org.rivierarobotics.frcgrantle
/**
 * The great ship S.S. Extension. It sails the seas of Gradloria, in search of proper configuration values.
 */
class SSExtension {

    String javaVersion = '1.8'
    FirstVersionSet versionSet = FirstVersionSet.DEFAULT
    String packageBase

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
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new IllegalStateException(message)
        }
    }

}
