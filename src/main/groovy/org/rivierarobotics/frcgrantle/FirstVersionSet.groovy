package org.rivierarobotics.frcgrantle

import groovy.transform.stc.ClosureParams
import groovy.transform.stc.SimpleType


/**
 * A set of versions corresponding to the libraries commonly used in FRC.
 */
class FirstVersionSet {

    static FirstVersionSet create(
            @ClosureParams(value = SimpleType.class, options = "Builder") @DelegatesTo(Builder) Closure<?> configurationClosure) {
        def builder = new Builder()
        configurationClosure.delegate = builder
        configurationClosure.call(builder)
        return builder.build()
    }

    static final class Builder {

        SimpleDep wpilib
        SimpleDep wpilibNative
        SimpleDep wpilibRuntime
        SimpleDep opencv
        SimpleDep opencvNative
        SimpleDep cscore
        SimpleDep cscoreNative
        SimpleDep networkTables
        SimpleDep ctrLib
        SimpleDep ctrLibNative
        SimpleDep navx

        FirstVersionSet build() {
            def options = [wpilib, wpilibNative, wpilibRuntime, opencv, opencvNative, cscore, cscoreNative, networkTables, ctrLib, ctrLibNative, navx]
            if (options.any { it == null }) {
                throw new NullPointerException("Something is null ${options}")
            }
            return new FirstVersionSet(wpilib, wpilibNative, wpilibRuntime, opencv, opencvNative, cscore, cscoreNative, networkTables, ctrLib, ctrLibNative, navx)
        }

    }

    static final def V_2017_3_1 = create { b ->
        b.wpilib = SimpleDep.WPILIB_2017.withVersion("2017.3.1")
        b.wpilibNative = SimpleDep.WPILIB_2017_NATIVE.withVersion("2017.3.1")
        b.wpilibRuntime = SimpleDep.WPILIB_2017_RUNTIME.withVersion("2017.3.1")
        b.opencv = SimpleDep.OPENCV.withVersion("3.2.0")
        b.opencvNative = SimpleDep.OPENCV_NATIVE.withVersion("3.2.0")
        b.cscore = SimpleDep.CSCORE.withVersion("1.0.2")
        b.cscoreNative = SimpleDep.CSCORE_NATIVE.withVersion("1.0.2")
        b.networkTables = SimpleDep.NETWORK_TABLES.withVersion('3.1.7')
        b.ctrLib = SimpleDep.CTR_LIB.withVersion("4.4.1.14")
        b.ctrLibNative = SimpleDep.CTR_LIB_NATIVE.withVersion("4.4.1.14")
        b.navx = SimpleDep.NAVX.withVersion("NONE")
    }

    static final def DEFAULT = V_2017_3_1

    final SimpleDep wpilib
    final SimpleDep wpilibNative
    final SimpleDep wpilibRuntime
    final SimpleDep opencv
    final SimpleDep opencvNative
    final SimpleDep cscore
    final SimpleDep cscoreNative
    final SimpleDep networkTables
    final SimpleDep ctrLib
    final SimpleDep ctrLibNative
    final SimpleDep navx

    FirstVersionSet(SimpleDep wpilib, SimpleDep wpilibNative, SimpleDep wpilibRuntime, SimpleDep opencv, SimpleDep opencvNative, SimpleDep cscore, SimpleDep cscoreNative, SimpleDep networkTables, SimpleDep ctrLib, SimpleDep ctrLibNative, SimpleDep navx) {
        this.wpilib = wpilib
        this.wpilibNative = wpilibNative
        this.wpilibRuntime = wpilibRuntime
        this.opencv = opencv
        this.opencvNative = opencvNative
        this.cscore = cscore
        this.cscoreNative = cscoreNative
        this.networkTables = networkTables
        this.ctrLib = ctrLib
        this.ctrLibNative = ctrLibNative
        this.navx = navx
    }
}
