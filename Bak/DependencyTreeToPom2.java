package aa;

import java.io.*;
import java.util.*;
import java.util.regex.*;

public class DependencyTreeToPom2 {
	
	// mvn dependency:copy-dependencies -DoutputDirectory=lib
	// mvn dependency:tree
	// mvn dependency:tree > a.txt

    // 支援可選的 classifier（groupId:artifactId:type[:classifier]:version:scope）
    private static final Pattern DEP_PATTERN = Pattern.compile(
        ".*?([a-zA-Z0-9_.-]+):" +          // groupId
        "([a-zA-Z0-9_.-]+):" +             // artifactId
        "([a-zA-Z0-9_.-]+):" +             // type
        "(?:([a-zA-Z0-9_.-]+):)?" +        // 可選 classifier
        "([a-zA-Z0-9_.-]+):" +             // version
        "([a-z]+).*"                       // scope
    );

    public static void main(String[] args) throws IOException {
        File inputFile = new File("C:\\p\\pomWork\\a.txt");
        Set<String> uniqueDeps = new HashSet<>();
        List<String> pomDependencies = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
            String line;

            while ((line = reader.readLine()) != null) {
                Matcher matcher = DEP_PATTERN.matcher(line.trim());

                if (matcher.matches()) {
                    String groupId = matcher.group(1);
                    String artifactId = matcher.group(2);
                    String type = matcher.group(3);
                    String classifier = matcher.group(4); // 可為 null
                    String version = matcher.group(5);
                    String scope = matcher.group(6);

                    if ("provided".equals(scope) || "system".equals(scope)) continue;

                    String coordinateKey = groupId + ":" + artifactId + ":" + version + 
                                           (classifier != null ? ":" + classifier : "");

                    if (uniqueDeps.contains(coordinateKey)) continue;
                    uniqueDeps.add(coordinateKey);

                    StringBuilder dep = new StringBuilder();
                    dep.append("    <dependency>\n");
                    dep.append("        <groupId>").append(groupId).append("</groupId>\n");
                    dep.append("        <artifactId>").append(artifactId).append("</artifactId>\n");
                    dep.append("        <version>").append(version).append("</version>\n");
                    if (classifier != null) {
                        dep.append("        <classifier>").append(classifier).append("</classifier>\n");
                    }
                    dep.append("        <scope>system</scope>\n");

                    // 組出 jar 檔案名
                    String jarFile = artifactId + "-" + version;
                    if (classifier != null) {
                        jarFile += "-" + classifier;
                    }
                    jarFile += "." + type;

                    dep.append("        <systemPath>${project.basedir}/lib/").append(jarFile).append("</systemPath>\n");
                    dep.append("    </dependency>");

                    pomDependencies.add(dep.toString());
                }
            }
        }

        System.out.println("<!-- 產生的 dependencies： -->");
        for (String dep : pomDependencies) {
            System.out.println(dep);
            System.out.println();
        }
    }
}
