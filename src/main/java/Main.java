import index.WikiIndex;
import links.WikiLinksChecker;

public class Main {

    String projectUrl = "/Users/iowa/Documents/libgdx_website/";

    Main() {
        WikiIndex wikiIndex = new WikiIndex(projectUrl);

        WikiLinksChecker linksChecker = new WikiLinksChecker(wikiIndex.getWikiPagesList());
        linksChecker.findBrokenLinks();

    }

    public static void main(String[] args) {
        new Main();
    }

}
