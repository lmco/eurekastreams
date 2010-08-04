package org.eurekastreams.server.service.actions.strategies.activity.datasources;

import java.io.Serializable;

import net.sf.json.JSONObject;

public interface PersistenceDataSourceRequestTransformer
{
    Serializable transform(JSONObject request);
}
