import index.WikiIndex;
import links.WikiLinksChecker;

public class Main {

    String projectUrl = "/libgdx_website/";

    Main() {
        WikiIndex wikiIndex = new WikiIndex(projectUrl);
        wikiIndex.buildIndexPages();

        //WikiLinksChecker linksChecker = new WikiLinksChecker(wikiIndex.getWikiPagesList());
        //linksChecker.findBrokenLinks();
    }

    public static void main(String[] args) {
        new Main();
    }

}
