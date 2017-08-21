package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.StringJoiner;

public class CommandUtil {

    public static void exec(Path location, String... commandArgs) {
        try {
            ProcessBuilder builder = new ProcessBuilder(commandArgs);
            builder.directory(location.toFile());
            builder.start();
        } catch (Exception e) {
            throw new RuntimeException(e.getCause());
        }
    }

    public static String execOut(Path location, String... commandArgs) {
        ProcessBuilder builder = new ProcessBuilder(commandArgs);
        builder.directory(location.toFile());
        String result;
        try {
            Process process = builder.start();

            BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringJoiner sj = new StringJoiner(System.lineSeparator());
            br.lines().iterator().forEachRemaining(sj::add);
            result = sj.toString();

            process.waitFor();
            process.destroy();
        } catch (Exception e) {
            throw new RuntimeException(e.getCause());
        }
        return result;
    }
}
