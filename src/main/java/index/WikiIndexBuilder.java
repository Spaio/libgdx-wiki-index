package index;

import com.esotericsoftware.yamlbeans.YamlReader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class WikiIndexBuilder {

    private FileNode filesTree;
    private final Map<String, FileNode> pages = new HashMap<>();

    private final String projectPath;

    public WikiIndexBuilder(String projectPath) {
        this.projectPath = projectPath;
    }

    FileNode build() {

        File rootDirectory = new File(projectPath + "wiki/");
        filesTree = new FileNode(projectPath, rootDirectory);

        processDirectory(filesTree);
        filesTree.sortChildren();

        return filesTree;
    }

    private void processDirectory(FileNode parentNode) {
        File directory = parentNode.getFile();

        if (! directory.isDirectory()) {
            throw new IllegalArgumentException(directory + " is not a directory");
        }

        File[] files = directory.listFiles();

        for (File file : files) {
            if (isHasToBeIgnored(file)) {
                continue;
            }

            if (file.getName().equals("_category.yml")) {
                try {
                    String content = new String(Files.readAllBytes(Paths.get(file.getPath())));
                    parentNode.parseDataFromYaml(new YamlReader(content));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                FileNode node = new FileNode(projectPath, file);
                parentNode.addChild(node);
                if (file.isDirectory()) {
                    processDirectory(node);
                } else if (file.getName().endsWith(".md")) {
                    pages.put(node.getRelativePathWithoutExtension(), node);
                }
                node.sortChildren();
            }
        }
    }

    private boolean isHasToBeIgnored(File file) {
        if (file.getName().equals("_category.yml")) {
            return false;
        }
        if (file.getName().startsWith("_")) {
            return true;
        }
        if (file.getName().startsWith(".")) {
            return true;
        }
        if (file.isDirectory()) {
            return false;
        }
        return ! file.getName().matches(".*\\.md");
    }

    public Map<String, FileNode> getPages() {
        return pages;
    }
}
