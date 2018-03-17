package org.rivierarobotics.frcgrantle;

import com.google.common.collect.ImmutableList;
import com.google.common.io.ByteStreams;
import com.google.common.io.CharStreams;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Random;

/**
 * Makes a call to {@code ./get_native_dependencies.py} and interprets the results.
 */
public class NativeDependencies {

    private static final int PORT = new Random().nextInt(1000) + 4000;

    private static Process getNativeDeps;

    public static void terminate() {
        getNativeDeps.destroy();
        getNativeDeps = null;
    }

    private static void startGetNativeDeps() {
        if (getNativeDeps != null) {
            if (getNativeDeps.isAlive()) {
                return;
            }
            int err = getNativeDeps.exitValue();
            throw gndError("Get native dependencies script crashed with exit code " + err);
        }
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("./get_native_dependencies.py", String.valueOf(PORT));
            processBuilder.environment().put("PYTHONUNBUFFERED", "1");
            getNativeDeps = processBuilder
                    .redirectError(ProcessBuilder.Redirect.INHERIT)
                    .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                    .start();
            Thread.sleep(1000);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    private static IllegalStateException gndError(String msg) {
        return new IllegalStateException(msg + ", see error output.");
    }

    private static final byte MORE_DATA = (byte) 0xD;

    public static NativeDependencies parseFrom(Path path) {
        startGetNativeDeps();
        try (Socket s = new Socket("localhost", PORT)) {
            DataInputStream input = new DataInputStream(s.getInputStream());
            DataOutputStream output = new DataOutputStream(new BufferedOutputStream(s.getOutputStream()));

            String pathToCheck = path.toAbsolutePath().toString();
            byte[] rawUtf8 = pathToCheck.getBytes(StandardCharsets.UTF_8);
            output.writeInt(rawUtf8.length);
            output.write(rawUtf8);
            output.flush();

            ImmutableList.Builder<String> deps = ImmutableList.builder();
            while (input.readByte() == MORE_DATA) {
                int size = input.readInt();
                byte[] data = new byte[size];
                ByteStreams.readFully(input, data);
                deps.add(StandardCharsets.UTF_8.decode(ByteBuffer.wrap(data)).toString());
            }

            return from(deps.build());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static NativeDependencies from(List<String> dependencies) {
        return new NativeDependencies(ImmutableList.copyOf(dependencies));
    }

    private final List<String> dependencies;

    private NativeDependencies(List<String> dependencies) {
        this.dependencies = dependencies;
    }

    public List<String> getDependencies() {
        return dependencies;
    }

}
