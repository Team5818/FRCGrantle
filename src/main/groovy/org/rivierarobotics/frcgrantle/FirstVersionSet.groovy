package org.rivierarobotics.frcgrantle
/**
 * A set of versions corresponding to the libraries commonly used in FRC.
 */
class FirstVersionSet {

    static FirstVersionSet create() {
        return new FirstVersionSet()
    }

    private final Map<String, SimpleDep> builtIn = new HashMap<>()
    private final Set<SimpleDep> userJava = new HashSet<>()
    private final Set<SimpleDep> userNative = new HashSet<>()

    private FirstVersionSet() {
    }

    Map<String, SimpleDep> getBuiltIn() {
        return builtIn
    }

    Set<SimpleDep> getUserJava() {
        return userJava
    }

    Set<SimpleDep> getUserNative() {
        return userNative
    }

    def addBuiltInLibrary(String propertiesKey, SimpleDep dependency) {
        builtIn.put(propertiesKey, dependency)
    }

    def addUserJavaLibrary(SimpleDep dependency) {
        userJava.add(dependency)
    }

    def addUserNativeLibrary(SimpleDep dependency) {
        userNative.add(dependency)
    }

    /**
     * Adds a new-style built-in library, with the java library under name + '-java', and natives under name + '-jni'.
     * Also adds the appropriate classifier to the native dependency
     */
    def addNewStyleBuiltInLibrary(String propertyKey, SimpleDep dependency) {
        addBuiltInLibrary(propertyKey, dependency.withName(dependency.name + '-java'))
        addUserNativeLibrary(dependency.withName(dependency.name + '-jni').addInputOverrides(classifier: 'linuxathena'))
    }

}
