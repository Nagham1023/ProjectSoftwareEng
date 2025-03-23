package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;
import java.util.List;

public class tablesStatus implements Serializable {
    private List<TableNode> tables;
    private List<String> statuses;

    // Constructors
    public tablesStatus() {}

    public tablesStatus(List<TableNode> tables, List<String> statuses) {
        this.tables = tables;
        this.statuses = statuses;
    }

    // Getters and Setters
    public List<TableNode> getTables() {
        return tables;
    }

    public void setTables(List<TableNode> tables) {
        this.tables = tables;
    }

    public List<String> getStatuses() {
        return statuses;
    }

    public void setStatuses(List<String> statuses) {
        this.statuses = statuses;
    }
}