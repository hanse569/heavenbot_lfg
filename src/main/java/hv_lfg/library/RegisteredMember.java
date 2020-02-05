package hv_lfg.library;

public class RegisteredMember {

    private String idDiscord;
    private String name;

    public RegisteredMember(String idDiscord, String name) {
        this.idDiscord = idDiscord;
        this.name = name;
    }

    public String getIdDiscord() {
        return idDiscord;
    }
    public void setIdDiscord(String idDiscord) {
        this.idDiscord = idDiscord;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
