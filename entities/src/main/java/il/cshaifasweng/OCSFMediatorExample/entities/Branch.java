package il.cshaifasweng.OCSFMediatorExample.entities;

public class Branch {
    private String id;
    private String name;

    public Branch(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
