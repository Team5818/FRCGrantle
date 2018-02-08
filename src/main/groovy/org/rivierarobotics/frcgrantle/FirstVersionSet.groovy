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
    private final Set<SimpleDep> builtInNative = new HashSet<>()

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

    Set<SimpleDep> getBuiltInNative() {
        return builtInNative
    }

    def addFirstLibrary(String propertyKey, SimpleDep dependency, List<LibraryKind> nativeKinds) {
        applyNativeKinds(dependency, nativeKinds) { SimpleDep dep, isJava ->
            if (isJava) {
                addBuiltInLibrary(propertyKey, dep)
            } else {
                addBuiltInNativeLibrary(dep)
            }
        }
    }

    def addUserLibrary(SimpleDep dependency, List<LibraryKind> nativeKinds) {
        applyNativeKinds(dependency, nativeKinds) { SimpleDep dep, isJava ->
            if (isJava) {
                addUserJavaLibrary(dep)
            } else {
                addUserNativeLibrary(dep)
            }
        }
    }

    private static def applyNativeKinds(SimpleDep simpleDep, List<LibraryKind> nativeKinds, Closure<?> callback) {
        for (LibraryKind kind : nativeKinds) {
            def dep = simpleDep.withInputOverrides(classifier: kind.classifier, ext: kind.ext)
            if (!kind.suffix.isEmpty()) {
                dep = dep.withNameSuffix('-' + kind.suffix)
            }
            callback.call(dep, kind == LibraryKind.userJava() || kind == LibraryKind.builtInJava())
        }
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

    def addBuiltInNativeLibrary(SimpleDep dependency) {
        builtInNative.add(dependency)
    }

}
