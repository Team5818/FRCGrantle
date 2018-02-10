package org.rivierarobotics.frcgrantle

class SimpleDep {

    static SimpleDep fromMap(Map<String, String> map) {
        return create(map['group'], map['name'], map['version'])
    }

    static Creator WPILIB = new Creator("edu.wpi.first.wpilibj", "wpilibj")
    static Creator WPI_UTIL = new Creator("edu.wpi.first.wpiutil", "wpiutil")
    static Creator OPENCV = new Creator("org.opencv", "opencv")
    static Creator CSCORE= new Creator("edu.wpi.first.cscore", "cscore")
    static Creator NTCORE = new Creator("edu.wpi.first.ntcore", "ntcore")
    static Creator CTR_LIB = new Creator("com.ctre.ctrlib", "ctrlib")
    static Creator HAL_NATIVE = new Creator("edu.wpi.first.hal", "hal")

    static SimpleDep create(String group, String name, String version, Map<String, String> inputOverrides = [:]) {
        // toolazy.png
        if (group == null || name == null || version == null) {
            throw new NullPointerException("one is null: ${group}/${name}/${version}")
        }
        return new SimpleDep(group, name, version, inputOverrides)
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

    private SimpleDep(String group, String name, String version, Map<String, String> inputOverrides) {
        this.group = group
        this.name = name
        this.version = version
        this.inputOverrides = inputOverrides
    }

    SimpleDep withName(String name) {
        return create(group, name, version, inputOverrides)
    }

    SimpleDep withNameSuffix(String suffix) {
        return withName(name + suffix)
    }

    SimpleDep withGroupName(String group, String name) {
        return create(group, name, version, inputOverrides)
    }

    SimpleDep addInputOverrides(Map<String, String> inputOverrides) {
        def map = new HashMap(this.inputOverrides)
        map.putAll(inputOverrides)
        return withInputOverrides(map)
    }

    SimpleDep withInputOverrides(Map<String, String> inputOverrides) {
        return create(group, name, version, inputOverrides)
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
