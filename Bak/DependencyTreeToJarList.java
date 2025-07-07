package kk;

import java.io.*;
import java.util.*;
import java.util.regex.*;

public class DependencyTreeToJarList {

    private static final Pattern DEP_PATTERN = Pattern.compile(
        ".*?([a-zA-Z0-9_.-]+):" +          // groupId
        "([a-zA-Z0-9_.-]+):" +             // artifactId
        "([a-zA-Z0-9_.-]+):" +             // type
        "(?:([a-zA-Z0-9_.-]+):)?" +        // optional classifier
        "([a-zA-Z0-9_.-]+):" +             // version
        "([a-z]+).*"                       // scope
    );

    public static void main(String[] args) throws IOException {
        File inputFile = new File("C:\\p\\pomWork\\deps.txt");
        Set<String> printed = new HashSet<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
            String line;
            int currentDepth = -1;

            while ((line = reader.readLine()) != null) {
                Matcher matcher = DEP_PATTERN.matcher(line.trim());

                if (!matcher.matches()) continue;

                String artifactId = matcher.group(2);
                String type       = matcher.group(3);
                String classifier = matcher.group(4); // may be null
                String version    = matcher.group(5);
                String scope      = matcher.group(6);

                if (!"jar".equals(type)) continue;
                if ("provided".equals(scope) || "system".equals(scope)) continue;

                String jarName = artifactId + "-" + version;
                if (classifier != null) jarName += "-" + classifier;
                jarName += ".jar";

                if (printed.contains(jarName)) continue;

                int depth = calculateDepth(line);
                if (depth <= currentDepth) {
                    System.out.println(); // new dependency block
                    currentDepth = depth;
                } else if (currentDepth == -1) {
                    currentDepth = depth;
                }

                System.out.println(jarName);
                printed.add(jarName);
            }
        }
    }

    private static int calculateDepth(String line) {
        int depth = 0;
        for (char c : line.toCharArray()) {
            if (c == '+' || c == '\\') break;
            if (c == '|') depth++;
        }
        return depth;
    }
}