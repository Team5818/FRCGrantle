package org.rivierarobotics.frcgrantle;

import com.google.common.collect.ImmutableSet;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Pattern;

public class NativeDepChecker {

    private static final Pattern LIB_FILE = Pattern.compile("\\.(so|a)");

    public static Set<String> getUnsatisfiedDependencies(List<Path> roots) throws IOException {
        try {
            ImmutableSet.Builder<String> unsat = ImmutableSet.builder();
            for (Path root : roots) {
                Files.list(root)
                        .filter(p -> LIB_FILE.matcher(p.getFileName().toString()).find())
                        .filter(p -> !p.getFileName().toString().endsWith(".debug"))
                        .map(p -> new NativeDepChecker(roots, p).getUnsatisfiedDependencies())
                        .forEach(unsat::addAll);
            }
            return unsat.build();
        } finally {
            NativeDependencies.terminate();
        }
    }

    private final List<Path> roots;
    private final Deque<Path> unchecked = new LinkedList<>();
    private final Set<String> totallyKnown = new HashSet<>();
    private final Set<String> totallyUnknown = new HashSet<>();

    private NativeDepChecker(List<Path> roots, Path start) {
        this.roots = roots;
        unchecked.addLast(start);
    }

    public Set<String> getUnsatisfiedDependencies() {
        while (!unchecked.isEmpty()) {
            Path next = unchecked.pollFirst();
            NativeDependencies deps = NativeDependencies.parseFrom(next);
            deps.getDependencies().stream()
                    .filter(s -> !totallyKnown.contains(s))
                    .filter(s -> !totallyUnknown.contains(s))
                    .forEach(s -> {
                        Path knownPath = getKnownPath(s);
                        if (knownPath != null || isProvided(s)) {
                            totallyKnown.add(s);
                            if (knownPath != null) {
                                unchecked.add(knownPath);
                            }
                        } else {
                            totallyUnknown.add(s);
                        }
                    });
        }
        return ImmutableSet.copyOf(totallyUnknown);
    }

    private Path getKnownPath(String dep) {
        String libname = dep.substring(0, dep.indexOf('.'));
        try {
            for (Path root : roots) {
                Optional<Path> lib = Files.list(root)
                        .filter(p -> p.getFileName().toString().startsWith(libname))
                        .findAny();
                if (lib.isPresent()) {
                    return lib.get();
                }
            }
            return null;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static final Set<String> PROVIDED = ImmutableSet.of(
            "libstdc++.so.6",
            "libgcc_s.so.1",
            "libvisa.so",
            "libNiFpga.so.13",
            "libFRC_NetworkCommunication.so.18",
            "libniriodevenum.so.1",
            "libNiRioSrv.so.13",
            "libm.so.6",
            "libRoboRIO_FRC_ChipObject.so.18",
            "libpthread.so.0",
            "libniriosession.so.1",
            "ld-linux.so.3",
            "libNiFpgaLv.so.13",
            "librt.so.1",
            "libdl.so.2"
    );

    private boolean isProvided(String dep) {
        return PROVIDED.contains(dep);
    }

}
