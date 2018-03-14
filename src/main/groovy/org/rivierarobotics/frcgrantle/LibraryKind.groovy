package org.rivierarobotics.frcgrantle

class LibraryKind {

    private static final LibraryKind BUILT_IN_JAVA = javaKind('java')
    private static final LibraryKind USER_JAVA = javaKind('')
    private static final LibraryKind JNI = nativeKind('jni', 'jar')
    private static final LibraryKind CPP = nativeKind('cpp', 'zip')

    static LibraryKind builtInJava() {
        return BUILT_IN_JAVA
    }

    static LibraryKind userJava() {
        return USER_JAVA
    }

    static LibraryKind jni() {
        return JNI
    }

    static LibraryKind cpp() {
        return CPP
    }

    static LibraryKind nativeKind(String suffix, String ext) {
        return of(suffix, 'linuxathena', ext)
    }

    static LibraryKind javaKind(String suffix) {
        return of(suffix, '', 'jar', true)
    }

    static LibraryKind of(String suffix, String classifier, String ext, boolean java = false) {
        return new LibraryKind(suffix, classifier, ext, java)
    }

    private final String suffix
    private final String classifier
    private final String ext
    private final boolean java

    private LibraryKind(String suffix, String classifier, String ext, boolean java) {
        this.suffix = suffix
        this.classifier = classifier
        this.ext = ext
        this.java = java
    }

    String getSuffix() {
        return suffix
    }

    String getClassifier() {
        return classifier
    }

    String getExt() {
        return ext
    }

    boolean isJava() {
        return java
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        LibraryKind that = (LibraryKind) o

        if (classifier != that.classifier) return false
        if (ext != that.ext) return false
        if (suffix != that.suffix) return false

        return true
    }

    int hashCode() {
        int result
        result = (suffix != null ? suffix.hashCode() : 0)
        result = 31 * result + (classifier != null ? classifier.hashCode() : 0)
        result = 31 * result + (ext != null ? ext.hashCode() : 0)
        return result
    }
}
