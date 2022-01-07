package links;

import index.FileNode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WikiLinksChecker {

    private final Map<String, FileNode> pagesByPathMap;

    public WikiLinksChecker(Map<String, FileNode> pagesByPathMap) {
        this.pagesByPathMap = pagesByPathMap;
    }

    public void findBrokenLinks() {
        for (FileNode page : pagesByPathMap.values()) {
            try {
                checkPage(page);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void checkPage(FileNode page) throws IOException {
        String pageContent = page.getContent();

        List<WikiLink> links = new ArrayList<>();

        Pattern wikiLinkPattern = Pattern.compile("(?<!!)\\[(.+?)\\]\\((.+?)\\)");

        Matcher matcher = wikiLinkPattern.matcher(pageContent);

        while (matcher.find()) {
            String url = matcher.group(2);

            // Avoiding external links
            if (url.startsWith("http://") || url.startsWith("https://")) {
                continue;
            }
            // Avoiding inner anchor links
            if (url.startsWith("#")) {
                continue;
            }

            WikiLink wikiLink = new WikiLink(matcher.group(1), url);
            links.add(wikiLink);
        }

        checkPageLinks(page, links);
    }

    private void checkPageLinks(FileNode page, List<WikiLink> links) {

        boolean pageWritten = false;
        for (WikiLink link : links) {
            if (pagesByPathMap.containsKey(link.getPath())) {
                //System.out.println("        Link is correct");
                continue;
            }
            if (!pageWritten) {
                System.out.println(page.getRelativePathWithoutExtension());
                pageWritten = true;
            }
            System.out.println("    -   " + link.getTitle()
                    + " - " + link.getUrl());

            System.out.println("        Link is broken");

            for (FileNode anotherPage : pagesByPathMap.values()) {
                if (anotherPage.getName().equalsIgnoreCase(link.getLinkedPageName())) {
                    String oldLink = "[" + link.getTitle() + "](" + link.getRawUrl() + ")";
                    String newLink = "[" + link.getTitle() + "]("
                            + anotherPage.getRelativePathWithoutExtension()
                            + link.getAnchor() + ")";

                    System.out.println("        Possible page: " + anotherPage.getRelativePathWithoutExtension());
                    System.out.println("        Old link: " + oldLink);
                    System.out.println("        New link: " + newLink);
                    System.out.println("        Press enter to resolve");
                    Scanner input = new Scanner(System.in);

                    //if (input.nextLine().isEmpty()) {
                        resolve(page, oldLink, newLink);
                    //}
                    System.out.println("        Resolved");
                }
            }

        }
    }

    private void resolve(FileNode page, String oldLink, String newLink) {
        page.setContent(page.getContent().replace(oldLink, newLink));
        page.save();
    }

}
