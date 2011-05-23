package org.eurekastreams.web.client.events.data;

import org.eurekastreams.server.domain.stream.StreamScope;

public class PostableStreamScopeChangeEvent extends BaseDataResponseEvent<StreamScope>
{
    public PostableStreamScopeChangeEvent(StreamScope inResponse)
    {
        super(inResponse);
    }

}
