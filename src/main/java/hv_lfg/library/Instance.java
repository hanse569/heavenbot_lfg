package hv_lfg.library;

public class Instance {
    private int idInstance;
    private String name;
    private int type;
    private String thumbnail;

    public Instance() {}

    public int getIdInstance() { return idInstance; }
    public String getName() { return name; }
    public int getType() { return type; }
    public String getThumbmail() { return thumbnail; }

    public void setIdInstance(int idInstance) { this.idInstance = idInstance; }
    public void setName(String name) { this.name = name; }
    public void setType(int type) { this.type = type; }
    public void setThumbmail(String thumbmail) { this.thumbnail = thumbmail; }

    @Override
    public String toString() {
        return name;
    }
}
