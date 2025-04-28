package aa;
import java.io.*;
import java.util.*;
import java.util.regex.*;

public class DependencyTreeToPom {

	// mvn dependency:copy-dependencies -DoutputDirectory=lib
	// mvn dependency:tree
	// mvn dependency:tree > a.txt
	
    private static final Pattern DEP_PATTERN = Pattern.compile(
            ".*?([a-zA-Z0-9_.-]+):([a-zA-Z0-9_.-]+):([a-zA-Z0-9_.-]+):([a-zA-Z0-9_.-]+):([a-z]+).*");

    public static void main(String[] args) throws IOException {
        File inputFile = new File("C:\\p\\pomWork\\a.txt"); // 請輸入你的 dependency:tree 輸出檔案路徑 
        Set<String> uniqueDeps = new HashSet<>();
        List<String> pomDependencies = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
            String line;

            while ((line = reader.readLine()) != null) {
                Matcher matcher = DEP_PATTERN.matcher(line.trim());

                if (matcher.matches()) {
                    String groupId = matcher.group(1);
                    String artifactId = matcher.group(2);
                    String type = matcher.group(3); // jar, war, pom...
                    String version = matcher.group(4);
                    String scope = matcher.group(5);

                    if ("provided".equals(scope) || "system".equals(scope)) continue; // 跳過 provided

                    String jarFile = artifactId + "-" + version + "." + type;

                    String coordinate = groupId + ":" + artifactId + ":" + version;
                    if (uniqueDeps.contains(coordinate)) continue;
                    uniqueDeps.add(coordinate);

                    StringBuilder dep = new StringBuilder();
                    dep.append("    <dependency>\n");
                    dep.append("        <groupId>").append(groupId).append("</groupId>\n");
                    dep.append("        <artifactId>").append(artifactId).append("</artifactId>\n");
                    dep.append("        <version>").append(version).append("</version>\n");
                    dep.append("        <scope>system</scope>\n");
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
