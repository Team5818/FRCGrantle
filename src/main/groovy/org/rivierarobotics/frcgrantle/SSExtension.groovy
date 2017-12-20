package org.rivierarobotics.frcgrantle
/**
 * The great ship S.S. Extension. It sails the seas of Gradloria, in search of proper configuration values.
 */
class SSExtension {

    String javaVersion
    FirstVersionSet versionSet = FirstVersionSet.DEFAULT

    def javaVersion(String value) {
        javaVersion = value
    }

    def versionSet(FirstVersionSet value) {
        versionSet = value
    }

}
