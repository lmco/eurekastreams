/*
 * Copyright (c) 2009 Lockheed Martin Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * 
 */
package org.eurekastreams.server.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.sf.gilead.pojo.gwt.LightEntity;

// TODO merge this with uinnovate's PagedSet and use from commons

/**
 * For returning result sets from a mapper.
 * 
 * @param <T> the class this paged set contains.
 */
public class PagedSet<T> extends LightEntity implements Serializable
{
    // TODO generate actual version ID
    /**
     * need to generate real id.
     */
    private static final long serialVersionUID = 1L;

    /**
     * start index of a constrained range of data.
     */
    private int fromIndex;
    
    /**
     * end index of a constrained range of data.
     */
    private int toIndex;
    
    /**
     * total data items. 
     */
    private int total;
    
    /**
     * list of ideas.
     */
    private List<T> pagedSet;
    
    /**
     * time it took to perform the search.
     */
    private String elapsedTime;
    
    /**
     * no argument constructor.
     * 
     * if from/to were 0/0, those indices would imply
     * it was returning the first element.
     * Using -1/-1 keeps it unambiguous that this is an empty set.
     */
    public PagedSet()
    {
        fromIndex = -1;
        toIndex = -1;
        total = 0;
        pagedSet = new ArrayList<T>();
    }

    /**
     * use this to tell if the range specified is valid. 
     * 
     * @param from  
     * 		the from value
     * @param to  
     * 		the to value
     * 
     * @return whether the range is valid or not.
     */
    public boolean isRangeValid(final int from, final int to)
    {
        if ((from > to) || (from < 0) || (to   < 0))
        {
            return false;
        }
        return true;
    }
    
    /**
     * public constructor.
     * 
     * @param from
     * 		the from value
     * @param to
     * 		the to value
     * @param inTotal
     * 		the total value
     * @param set
     * 		The array list
     */
    public PagedSet(final int from, final int to, final int inTotal, final List<T> set)
    {
        this.fromIndex = from;
        this.toIndex = to;
        this.total = inTotal;
        this.pagedSet = set;
        
    }

    /**
     * @return the fromIndex
     */
    public int getFromIndex()
    {
        return fromIndex;
    }

    /**
     * @param inFromIndex the fromIndex to set
     */
    public void setFromIndex(final int inFromIndex)
    {
        this.fromIndex = inFromIndex;
    }

    /**
     * @return the toIndex
     */
    public int getToIndex()
    {
        return toIndex;
    }

    /**
     * @param inToIndex the inToIndex to set
     */
    public void setToIndex(final int inToIndex)
    {
        this.toIndex = inToIndex;
    }

    /**
     * @return the total
     */
    public int getTotal()
    {
        return total;
    }

    /**
     * @param inTotal the total to set
     */
    public void setTotal(final int inTotal)
    {
        this.total = inTotal;
    }

    /**
     * @return the paged set
     */
    public List<T> getPagedSet()
    {
        return pagedSet;
    }

    /**
     * @param inPagedSet
     * 		the list of entities to set
     */
    public void setPagedSet(final List<T> inPagedSet)
    {
        this.pagedSet = inPagedSet;
    }
    
    /**
     * if this pagedset is equivalent to the other one.
     * must pass in a paged set.
     * 
     * warnings are suppressed because set type information is unavailable at runtime.
     * 
     * @param set
     * 		object to compare.
     * @return true if this object is equal.
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(final Object set)
    {
        if (set == null)
        {
            return false;
        }
        
        if (!(set instanceof PagedSet))
        {
            return false;
        }
        
        PagedSet other = (PagedSet) set;
        
        return this.fromIndex == other.fromIndex 
            && this.toIndex == other.toIndex
            && this.total == other.total
            && this.pagedSet.equals(other.pagedSet);
    }

    
    // TODO use org.apache.commons.lang.builder.HashCodeBuilder for a better hashcode
    // this should be done in uinnovate and commons wherever equals()/hashcode() is implemented
    /**
     * for compatibility with equals.
     * @return int the hashcode value for the list
     */
    @Override
    public int hashCode()
    {
        return pagedSet.hashCode();
    }
 
    /**
     * Get a string representation of how long the search took.
     * 
     * @return a string representation of how long the search took.
     */
    public String getElapsedTime()
    {
        return elapsedTime;
    }

    /**
     * sets the number of milliseconds it took to perform the search.
     * 
     * @param inElapsedTime
     *            String representing how long the search took
     */
    public void setElapsedTime(final String inElapsedTime)
    {
        elapsedTime = inElapsedTime;
    }
}
