package org.eurekastreams.server.domain.stream;

import java.io.Serializable;

public class BookmarkFilter implements StreamFilter, Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = 154645451231L;

    private String name = "";
    private String request = "";
    private long id = 0L;

    private BookmarkFilter()
    {

    }

    public BookmarkFilter(final long inId, final String inName, final String inRequest)
    {
        id = inId;
        name = inName;
        request = inRequest;
    }

    public long getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public String getRequest()
    {
        return request;
    }

    public void setName(String inName)
    {
        name = inName;
    }

    public void setRequest(final String inRequest)
    {
        request = inRequest;
    }

    public void setId(final long inId)
    {
        id = inId;
    }
}