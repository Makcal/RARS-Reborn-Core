package core.program;

import java.util.List;

public interface IRelocationTable {
    void requestLinking(LinkRequest request);

    List<LinkRequest> getLinkRequests();
}
