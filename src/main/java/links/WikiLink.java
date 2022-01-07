package links;

public class WikiLink {

    private final String
            title,
            rawUrl;

    private String
            path,
            url,
            linkedPageName,
            anchor = "";

    WikiLink(String title, String url) {
        this.title = title;
        this.rawUrl = url;

        this.url = url.trim();

        path = url;
        if (path.contains("#")) {
            anchor = path.substring(path.indexOf("#"));
            path = path.substring(0, path.indexOf("#"));
        }

        linkedPageName = path.substring(path.lastIndexOf("/") + 1);
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getLinkedPageName() {
        return linkedPageName;
    }

    public String getRawUrl() {
        return rawUrl;
    }

    public String getPath() {
        return path;
    }

    public String getAnchor() {
        return anchor;
    }
}
