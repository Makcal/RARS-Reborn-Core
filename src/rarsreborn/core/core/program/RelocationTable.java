package rarsreborn.core.core.program;

import java.util.LinkedList;
import java.util.List;

public class RelocationTable implements IRelocationTable {
    private final List<RelocationRecord> relocationTable = new LinkedList<>();

    @Override
    public void addRequest(long offset, LinkRequest request) {
        relocationTable.add(new RelocationRecord(offset, request.label()));
        if (request.extra() != null) {
            relocationTable.add(new RelocationRecord(offset + request.extra(), request.label()));
        }
    }

    @Override
    public List<RelocationRecord> getRecords() {
        return relocationTable;
    }
}
