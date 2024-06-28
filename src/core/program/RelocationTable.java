package core.program;

import java.util.LinkedList;
import java.util.List;

public class RelocationTable implements IRelocationTable {
    private final List<LinkRequest> relocationTable = new LinkedList<>();

    @Override
    public void requestLinking(LinkRequest request) {
        relocationTable.add(request);
    }

    @Override
    public List<LinkRequest> getLinkRequests() {
        return relocationTable;
    }
}
