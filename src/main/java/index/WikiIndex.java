package index;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

public class WikiIndex {

    private final String projectPath;

    WikiIndexBuilder indexBuilder;

    public WikiIndex(String projectPath) {
        this.projectPath = projectPath;

        indexBuilder = new WikiIndexBuilder(projectPath);
        FileNode filesTree = indexBuilder.build();

        //printTree(0, filesTree);

        String tree = makeTree(0, filesTree);
        String sidebar = makeSidebar(0, filesTree);

        try {
            Files.write(Paths.get(projectPath + "_includes/wiki_index.md"), tree.getBytes());
            Files.write(Paths.get(projectPath + "_includes/wiki_sidebar.md"), sidebar.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void printTree(int level, FileNode filesTree) {
        for (FileNode node : filesTree.getChildren()) {
            for (int i = 0; i < level; i++) {
                System.out.print("-");
            }

            if (node.getFile().isDirectory()) {
                System.out.print(" " + node.getTitle() + "/");
            } else {
                System.out.print(" " + node.getTitle());
            }
            System.out.println();

            printTree(level + 1, node);
        }
    }
    private String makeTree(int level, FileNode filesTree) {
        String result = "";

        for (FileNode node : filesTree.getChildren()) {
            if (node.isExcludedFromIndex()) {
                continue;
            }
            if (node.getFile().getName().equals(filesTree.getIndexPage())) {
                continue;
            }
            for (int i = 0; i < level; i++) {
                result += "  ";
            }

            if (node.getFile().isDirectory()) {
                if (node.getIndexPage() != null) {
                    String path = node.getFile().getPath();
                    path = path.replace(projectPath, "");
                    path = "/" + path;
                    path += "/" + node.getIndexPage();

                    result += "* [" + node.getTitle() + "](" + path + ")";
                } else {
                    result += "* " + node.getTitle();
                }
            } else {
                String path = node.getFile().getPath();
                path = path.replace(projectPath, "");
                path = "/" + path;
                if (path.endsWith(".md")) {
                    path = path.substring(0, path.length() - 3);
                }

                result += "* [" + node.getTitle() + "](" + path + ")";
            }
            result += "\n";

            result += makeTree(level + 1, node);
        }
        return result;
    }

    private String makeSidebar(int level, FileNode filesTree) {
        String result = "";

        for (FileNode node : filesTree.getChildren()) {
            if (node.isExcludedFromIndex()) {
                continue;
            }
            if (node.getFile().getName().equals(filesTree.getIndexPage())) {
                continue;
            }
            for (int i = 0; i < level; i++) {
                result += "  ";
            }

            if (node.getFile().isDirectory()) {
                if (node.getIndexPage() != null) {
                    String path = node.getFile().getPath();
                    path = path.replace(projectPath, "");
                    path = "/" + path;
                    path += "/" + node.getIndexPage();

                    result += "* [" + node.getTitle() + "](" + path + ")";
                } else {
                    result += "* " + node.getTitle();
                }
            } else {
                String path = node.getFile().getPath();
                path = path.replace(projectPath, "");
                path = "/" + path;
                if (path.endsWith(".md")) {
                    path = path.substring(0, path.length() - 3);
                }

                result += "* [" + node.getTitle() + "](" + path + ")";
            }
            result += "\n";

            result += makeTree(level + 1, node);
        }
        return result;
    }


    public Map<String, FileNode> getWikiPagesList() {
        return indexBuilder.getPages();
    }

}
