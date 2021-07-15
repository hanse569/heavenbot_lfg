package be.isservers.hmb.web;

public class SessionData {
    private final String id;
    private final String name;
    private final String image;

    public SessionData(String id, String name, String image) {
        this.id = id;
        this.name = name;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public String getImageLink() {
        return "https://cdn.discordapp.com/avatars/" + this.id + "/" + this.image + ".png";
    }
}
