package org.rivierarobotics.frcgrantle

class SimpleDep {

    static SimpleDep fromMap(Map<String, String> map) {
        return create(map['group'], map['name'], map['version'])
    }

    static Creator WPILIB_2017 = new Creator("edu.wpi.first.wpilibj", "athena")
    static Creator WPILIB_2017_NATIVE = new Creator("edu.wpi.first.wpilibj", "athena-jni")
    static Creator WPILIB_2017_RUNTIME = new Creator("edu.wpi.first.wpilib", "athena-runtime")
    static Creator WPILIB_2018 = new Creator("edu.wpi.first.wpilibj", "wpilibj-java")
    static Creator WPILIB_2018_NATIVE = new Creator("edu.wpi.first.wpilibj", "wpilibj-jni")
    static Creator WPILIB_2018_RUNTIME = new Creator("edu.wpi.first.wpilibj", "wpilibj-jniShared")
    static Creator OPENCV = new Creator("org.opencv", "opencv-java")
    static Creator OPENCV_NATIVE = new Creator("org.opencv", "opencv-jni")
    static Creator CSCORE = new Creator("edu.wpi.cscore.java", "cscore")
    static Creator CSCORE_NATIVE = CSCORE
    static Creator CSCORE_2018 = new Creator("edu.wpi.first.cscore", "cscore-java")
    static Creator CSCORE_2018_NATIVE = new Creator("edu.wpi.first.cscore", "cscore-jni")
    static Creator NETWORK_TABLES = new Creator("edu.wpi.first.wpilib.networktables.java", "NetworkTables")
    static Creator CTR_LIB = new Creator("com.ctre", "ctrlib")
    static Creator CTR_LIB_NATIVE = new Creator("com.ctre", "ctrlib")
    static Creator NAVX = new Creator("com.kauailabs.navx.frc", "navx_frc")

    static SimpleDep create(String group, String name, String version) {
        // toolazy.png
        if (group == null || name == null || version == null) {
            throw new NullPointerException("one is null: ${group}/${name}/${version}")
        }
        return new SimpleDep(group, name, version)
    }

    static final class Creator {
        private final String group, name

        Creator(String group, String name) {
            this.group = group
            this.name = name
        }

        SimpleDep withVersion(String version) {
            return create(group, name, version)
        }
    }

    final String group, name, version
    final Map<String, String> inputOverrides

    private SimpleDep(String group, String name, String version, Map<String, String> inputOverrides = [:]) {
        this.group = group
        this.name = name
        this.version = version
        this.inputOverrides = inputOverrides
    }

    SimpleDep withName(String name) {
        return create(group, name, version)
    }

    SimpleDep withGroupName(String group, String name) {
        return create(group, name, version)
    }

    SimpleDep withInputOverrides(Map<String, String> inputOverrides) {
        return new SimpleDep(group, name, version, inputOverrides)
    }

    Map<String, String> toMapDependency(Map<String, String> inputs = [:]) {
        def copy = new HashMap(inputs)
        copy.putAll(inputOverrides)
        copy['group'] = group
        copy['name'] = name
        copy['version'] = version
        return copy
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        SimpleDep simpleDep = (SimpleDep) o

        if (group != simpleDep.group) return false
        if (name != simpleDep.name) return false
        if (version != simpleDep.version) return false

        return true
    }

    int hashCode() {
        int result
        result = (group != null ? group.hashCode() : 0)
        result = 31 * result + (name != null ? name.hashCode() : 0)
        result = 31 * result + (version != null ? version.hashCode() : 0)
        return result
    }

    @Override
    String toString() {
        return "SimpleDep{" +
                "group='" + group + '\'' +
                ", name='" + name + '\'' +
                ", version='" + version + '\'' +
                '}';
    }
}
