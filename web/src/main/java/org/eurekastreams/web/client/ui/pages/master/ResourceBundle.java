package org.eurekastreams.web.client.ui.pages.master;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.CssResource.NotStrict;

public interface ResourceBundle extends ClientBundle
{
    ResourceBundle INSTANCE = GWT.create(ResourceBundle.class);

    @NotStrict
    @Source("style/core.css")
    public CssResource coreCss();
    
    @NotStrict
    @Source("style/ie.css")
    public CssResource ieCss();
}
