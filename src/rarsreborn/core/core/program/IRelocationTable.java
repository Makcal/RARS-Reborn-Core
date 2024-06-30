package rarsreborn.core.core.program;

import java.util.List;

public interface IRelocationTable {
    void addRequest(long offset, LinkRequest request);

    List<RelocationRecord> getRecords();
}
