package org.eurekastreams.server.action.execution.stream;

import java.io.Serializable;
import java.util.List;

import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.stream.StreamScope;
import org.eurekastreams.server.persistence.mappers.FindByIdMapper;
import org.eurekastreams.server.persistence.mappers.requests.FindByIdRequest;

public class InsertStreamBookmarkExecution implements ExecutionStrategy<PrincipalActionContext>
{
    /**
     * Mapper used to retrieve and save the page that holds the streams.
     */
    private final FindByIdMapper<Person> personMapper;

    /**
     * Constructor.
     *
     * @param inPersonMapper
     *            the person mapper.
     */
    public InsertStreamBookmarkExecution(final FindByIdMapper<Person> inPersonMapper)
    {
        personMapper = inPersonMapper;
    }

    
    public Serializable execute(PrincipalActionContext inActionContext) throws ExecutionException
    {
        
        Person person = (Person) inActionContext.getState().get("person");

        if (person == null)
        {
            person = personMapper.execute(new FindByIdRequest("Person", inActionContext.getPrincipal().getId()));
        }
        
        List<StreamScope> bookmarks = person.getBookmarks();
        
        bookmarks.add((StreamScope) inActionContext.getParams());
        
        return (StreamScope) inActionContext.getParams();
    }

}
