package org.eurekastreams.server.service.actions.strategies.activity;

import java.util.List;

public interface ListCollider
{
    public List<Long> collide(final List<Long> sorted, final List<Long> unsorted);
}
