package index;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FileNode {

    private File file;
    private String title = "_unknown_";
    private String name;
    private String indexPage = null;
    private boolean excludedFromIndex = false;
    private Integer order = 100;
    private final List<FileNode> children = new ArrayList<>();
    private final String relativePathWithoutExtension;
    private String content = "";

    FileNode(String projectUrl, File file) {
        this.file = file;


        name = file.getName();
        if (name.endsWith(".md")) {
            name = name.substring(0, name.length() - 3);
        }

        title = name;

        String path = file.getPath().replaceFirst(projectUrl, "");
        path = "/" + path;
        if (path.endsWith(".md")) {
            path = path.substring(0, path.length() - 3);
        }
        relativePathWithoutExtension = path;

        if (! file.isDirectory()) {
            try {
                content = new String(Files.readAllBytes(Paths.get(file.getPath())));


                /*
                String filename = file.getName();
                if (filename.endsWith(".md")) {
                    filename = filename.substring(0, filename.length() - 3);
                    String newContent = content.replace("\npermalink: /wiki/" + filename, "");

                    byte[] strToBytes = newContent.getBytes();
                    System.out.println(file.getPath());
                    Files.write(Paths.get(file.getPath()), strToBytes);
                }
                 */

                String yamlDelimiter = "---";
                if (content.startsWith(yamlDelimiter)) {

                    int endingBlockPos = content.indexOf(yamlDelimiter, yamlDelimiter.length());
                    String yamlBlock = content.substring(yamlDelimiter.length(), endingBlockPos).trim();
                    parseDataFromYaml(new YamlReader(yamlBlock));

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void addChild(FileNode childNode) {
        children.add(childNode);
    }

    List<FileNode> getChildren() {
        return children;
    }

    File getFile() {
        return file;
    }

    String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    void sortChildren() {
        children.sort((o1, o2) -> {
            File f1 = o1.getFile();
            File f2 = o2.getFile();
            if (f1.isDirectory() && !f2.isDirectory()) {
                return -1;
            }
            if (f2.isDirectory() && !f1.isDirectory()) {
                return 1;
            }
            if (! o1.order.equals(o2.order)) {
                return o1.order.compareTo(o2.order);
            }
            return f1.getName().compareTo(f2.getName());
        });
    }

    void parseDataFromYaml(YamlReader yamlReader) {

        try {
            Object root = yamlReader.read();

            if (root instanceof Map) {
                Map<?, ?> values = (Map<?, ?>) root;
                if (values.containsKey("title")) {
                    title = values.get("title") + "";
                }
                if (values.containsKey("indexPage")) {
                    indexPage = values.get("indexPage") + "";
                }
                if (values.containsKey("order")) {
                    try {
                        order = Integer.parseInt(values.get("order") + "");
                    } catch (NumberFormatException e) {
                        System.out.println("Wrong number format for order at " + file.getPath());
                    }
                }
                if (values.containsKey("excluded")) {
                    excludedFromIndex = Boolean.parseBoolean(values.get("excluded") + "");
                }
            }
        } catch (YamlException e) {
            e.printStackTrace();
        }
    }

    public boolean isExcludedFromIndex() {
        return excludedFromIndex;
    }

    public String getIndexPage() {
        return indexPage;
    }

    public String getRelativePathWithoutExtension() {
        return relativePathWithoutExtension;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public void save() {
        byte[] strToBytes = getContent().getBytes();

        try {
            Files.write(Paths.get(file.getPath()), strToBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
