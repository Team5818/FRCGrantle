package org.rivierarobotics.frcgrantle

class Util {
    static void ifNonNull(Object nullable, Closure<?> code) {
        if (nullable != null) {
            code.call([nullable])
        }
    }
}
