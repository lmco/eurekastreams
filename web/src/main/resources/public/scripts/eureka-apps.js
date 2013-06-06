if (typeof(Eureka) === "undefined") {
    var Eureka = {};
}


Eureka.EventBus =
{
    observers : [],

    addObserver : function(eventKey, handler)
    {
        if (this.observers[eventKey] == null)
        {
            this.observers[eventKey] = new Array();
        }
        this.observers[eventKey].push(handler); 
    },

    notifyObservers : function(eventKey, data)
    {
        if (this.observers[eventKey] != null)
        {
            for (var i = 0; i < this.observers[eventKey].length; i++)
            {
                this.observers[eventKey][i](data);
            }
        }
    }
}


Eureka.BannerBar = function(img, color)
{
    this.img = img;
    this.color = color;

    this.getContainer = function()
    {
        return jQuery("<div style='background: url("+img+") no-repeat scroll left center "+color+"; height: 40px;'></div>");
    }
}



Eureka.Container =
{
    viewCount : 0,
    views : [],
    viewContainer : null,
    contentContainer : null,
    currentContent : null,
    width : 0,
    splitScreen: false,
        contents : [],
    unAuthedVal : false,
    init : function(containerId, splitScreen, appInitCallback)
    {
        if (jQuery("body").width() != 0)
        {
        this.splitScreen = splitScreen;
        this.container = jQuery("#" + containerId);
        if (this.unAuthedVal == true)
        {
            this.container.hide();
        }
        this.viewContainer = jQuery("<div class='view-container'></div>");
        this.container.append(this.viewContainer);
                this.container.addClass("app-container");
        this.viewContainer.css("position", "absolute");
        
        if (splitScreen)
        {
            jQuery("body").addClass("canvas");
            this.width = 330;
            this.contentContainer = jQuery("<div></div>");
            this.contentContainer.addClass("content-container");
            this.contentContainer.css("width", jQuery("body").width() - 330 + "px");
            this.contentContainer.css("min-height", "300px");
            this.container.append(this.contentContainer);
        }
        else
        {
            this.width = jQuery("body").width();
        }
        
        this.viewContainer.css("width", this.width + "px");
        if (appInitCallback != null)
        {
            appInitCallback();
        }
            }
        else
            {
        setTimeout(function(){Eureka.Container.init(containerId, splitScreen, appInitCallback);},20);
            }
    },
    unAuthed : function(msg)
    {
        this.unAuthedVal = true;
        jQuery("body").html("<div style='padding:10px'>"+msg+"</div>");
    },
    isSplitScreen : function()
    {
        return this.splitScreen;
    },
    switchView : function(obj)
    {
        if (jQuery("body").width() != 0 && this.viewContainer != null)
        {
            obj.css("width", this.width + "px");
            obj.css("float", "left");
        
            this.viewContainer.css("width", this.width*(this.viewCount+1)+"px");
        
            this.views.push(obj);
            this.viewContainer.append(obj);
            Eureka.resize();

            if (this.viewCount > 0) // We have something before us, we need to slide it out
            {
                obj.css("left", this.width*(this.viewCount) + "px");

                this.viewContainer.animate({left:'-='+this.width},'fast','linear',function(){
                    Eureka.resize();
                });
            }
        
            this.viewCount++;

            Eureka.EventBus.notifyObservers("switchedView", this.viewCount);
        }
        else
        {
            setTimeout(function(){Eureka.Container.switchView(obj);},20);
        }
    },
    setContents : function(obj, noPush)
    {
        this.contents.push(obj);

        if (this.currentContent != null)
        {
            this.currentContent.detach();
        }
        this.contentContainer.append(obj);
        this.currentContent = obj;
        this.contentContainer.css("height", "auto");
        setTimeout(function() {
            Eureka.resize();
            Eureka.Container.adjustContentHeight();
        }, 5);
    },
    adjustContentHeight : function()
    {
        if (this.contentContainer != null)
        {
            this.contentContainer.css("height", jQuery("body").height());
        }
    },
    goBack : function(inCanvasRight)
    {
        if (!inCanvasRight)
        {
            var views = this.views;
            this.viewContainer.animate({left:'+='+this.width},'fast','linear',function(){
                Eureka.Container.views[Eureka.Container.viewCount-1].detach();
                Eureka.Container.viewCount--;
                Eureka.Container.views.pop();
                Eureka.Container.viewContainer.css("width", Eureka.Container.width*(Eureka.Container.viewCount)+"px");
                Eureka.resize();
		Eureka.EventBus.notifyObservers("goingBack", Eureka.Container.viewCount);
            });
        }
        else
        {
            this.contents.pop();
            Eureka.Container.setContents(this.contents[this.contents.length-1]);
            this.contents.pop();
	    Eureka.EventBus.notifyObservers("goingBack", this.contents.length);
        }
    }
}

Eureka.Form = function(titleText, callback)
{
    this.validators = [];
    this.keys = [];
    this.container = jQuery("<div class='gadget-form'></div>");
    var title = jQuery("<div class='title-bar'></div>");
    this.container.append(title);
    
    var cancelButton = jQuery("<div class='gadget-cancel-button'>Cancel</div>");
    cancelButton.click(function() { Eureka.Container.goBack(); });
    title.append(cancelButton );
    
    if (titleText != "")
    {
        title.append("<strong>"+titleText+"</strong>");
    }

    var saveButton = jQuery("<a class='save-button'>Save</a>");
    var thisBuffered = this;
    saveButton.click(function() {
        var errors = thisBuffered.validate();
        if (errors.length == 0)
        {
            callback(form.serialize());
        }
        else
        {
            var errStr = "";
            for (var i = 0; i < errors.length; i++)
            {
                errStr += errors[i] + "\n";
            }
            alert(errStr);
        }
    });

    title.append(saveButton);

    var form = jQuery("<form></form>");
    this.container.append(form);

    this.validate = function()
    {
        var errors = [];
        for (var key in this.validators)
        {
        	var id = key.split('.', 1);  // allow for multiple rules per field
            var input = jQuery("#"+id);
            var val = input.val();
            if (val == input.attr('title'))
            {
                val = "";
            }
            if (!this.validators[key].validator(val))
            {
                errors.push(this.validators[key].message);
            }
        }
        return errors;
    }

    this.addValidator = function(key, check, message)
    {
        this.validators[key] = {};
        this.validators[key].message = message;
		if (typeof check == 'function')
			this.validators[key].validator = check;
		else if (typeof check == 'string' || check instanceof RegExp)
			this.validators[key].validator = function(val) { return val.match(check); };
    }

    this.addTextBox = function(key, title, defaultValue)
    {
        this.keys.push(key);
        var input = jQuery("<input id='"+key+"' type='text' name='"+key+"' value='"+defaultValue+"' title='"+title+"' />");
        input.val(input.attr('title'));
        input.addClass('text-label');

        input.focus(function(){
            if(input.val() == input.attr('title')) 
        {
                input.val('');
                input.removeClass('text-label');
            }
        });
     
        input.blur(function(){
            if(input.val() == '') {
            input.val(input.attr('title'));
                input.addClass('text-label');
            }
        });

        form.append(input);
    }
    
    this.clear = function()
    {
        for (var x in this.keys)
        {
            var key = this.keys[x];
            var input = jQuery("#"+key);
            input.val(input.attr('title'));
            input.addClass('text-label');
        }
    }
    this.getContainer = function()
    {
        return this.container;
    }
}

Eureka.SearchBar = function(callback, supressType)
{
    this.container = jQuery("<div></div>");
    this.callback = callback;
    this.supressType = supressType;

    var input = jQuery("<input class='text-label' id='gadget-search' value='Search...' />");
    input.keyup(function(e) {
        if(e.which == 13 || (input.val().length > 2 && supressType != true))
        {
            callback(input.val());
        }
    });
    input.focus(function(){
            if(input.val() == 'Search...') 
        {
                input.val('');
                input.removeClass('text-label');
            }
    });
     
    input.blur(function(){
            if(input.val() == '') {
            input.val('Search...');
                input.addClass('text-label');
            }
    });

    input.css("width", jQuery("body").width() - 66);

    var title = jQuery("<div class='title-bar collapsed search-bar'></div>");
    this.container.append(title);
	
    title.append(jQuery("<div class='gadget-pre-search'></div>"));
    title.append(input);
    title.append(jQuery("<div class='gadget-post-search'></div>"));

    var buttonContainer = jQuery("<div class='button-container'></div>");
    title.prepend(buttonContainer);

    Eureka.EventBus.addObserver("switchedView", function(data) {
        if (buttonContainer.width() > 0)
        {
            input.css("width", jQuery("body").width() - 66 - buttonContainer.width());
        }
	if (data > 0)
	{
		jQuery(".dim").hide();
		jQuery(".search-results-for").hide();
		jQuery(".search-results").hide();
		Eureka.resize();
	}
    });

    Eureka.EventBus.addObserver("goingBack", function(data) {
	if (data == 1)
	{
		jQuery(".dim").show();
		jQuery(".search-results-for").show();
		jQuery(".search-results").show();
		Eureka.resize();
	}
    });

    this.setResultsPanel = function(obj)
    {
        if (obj == null)
        {
        obj = jQuery("<div style='padding-top:20px;' class='no-items'>No Search Results</div>");
            }

        jQuery(".dim").detach();
        jQuery(".search-results-for").detach();
        jQuery(".search-results").detach();
        Eureka.resize();

        var dim = jQuery("<div class='dim'></div>");
        var resultsFor = jQuery("<div class='search-results-for'>Searching for: " + input.val() + "</div>")
        obj.addClass("search-results");


        dim.css("width", jQuery("body").width());
        dim.css("height", jQuery("body").height());
        dim.css("background", "white");
    

                var clearLink = jQuery("<a class='nav-x'>X</a>");
        clearLink.click(function() {
            dim.detach();
            resultsFor.detach();
            obj.detach();
            Eureka.resize();
            jQuery('.app-container').removeClass('search-results-shown');
            input.val('Search...');
                    input.addClass('text-label');
        });
                resultsFor.append(clearLink);

        jQuery(".app-container").append(dim);
                jQuery(".app-container").append(resultsFor);
        jQuery(".app-container").append(obj).addClass('search-results-shown');
        var top = obj.css("top").replace(/px/,"");
        obj.css("height", obj.height()+parseInt(top));
        Eureka.resize();
            
    }

    this.addObj = function(obj)
    {
        buttonContainer.append(obj);
    }

    this.getContainer = function()
    {
        return this.container;
    }
}

Eureka.ExpandableSectionContainer = function()
{
    // Set up the initial container. 
    this.container = jQuery("<div></div>");
    this.container.addClass('expandable-section-container');
    this.sectionCount = 0;


    this.addObj = function(obj)
    {
        this.container.append(obj);
    }
    this.addSection = function(titleText, obj, expanded, isEditable, addSection, additionalButton)
    {   
        var index = this.sectionCount;

        var section = jQuery("<div></div>");
        section.addClass('section');
        section.addClass('section-num-' + this.sectionCount);

        this.container.append(section);
            Eureka.resize();
        
        var title = jQuery("<div class='title-bar clickable'></div>");
        section.append(title);
        var expandButton = jQuery("<span></span>");
        
        if (isEditable)
        {
            section.addClass("editable");
            section.addClass("not-editing");
                        
            
            var editLink = jQuery("<div class='expandable-edit'>Edit</div>");
            editLink.click(function(event) { section.addClass("editing"); section.removeClass("not-editing"); event.stopPropagation(); });
            
            var addLink = jQuery("<div class='expandable-add'>Add</div>");
            addLink.click(function(event) {  Eureka.Container.switchView(addSection.getContainer()); addSection.clear(); event.stopPropagation(); });

            var doneLink = jQuery("<div class='expandable-done'>Done</div>");
            doneLink.click(function(event) { section.removeClass("editing"); section.addClass("not-editing"); event.stopPropagation(); });

            title.append(editLink);
            title.append(doneLink);
            if (addSection != null)
            {
                title.append(addLink);
            }
        }

        if (additionalButton != null)
        {
            title.append(additionalButton);
        }

        // these two are not float right. List them last, because the float-right buttons act silly otherwise
        title.append(expandButton);
        title.append("<span><strong> " + titleText + "</strong></span>");
        
        this.sectionCount++;

        var canvasKey = "default";
        if (Eureka.Container.isSplitScreen()) canvasKey = "canvas";

        var key = canvasKey + "sec" + index + "-";

        obj.showSection = function(slide, dontPersist)
        {
            if (!dontPersist) Eureka.setAppDataForInstance(key, "true");

            expandButton.html("<span class='down-arrow'></span>");
            title.unbind("click");
            title.removeClass("collapsed");
            title.click(function() { obj.hideSection(true); });

            if(slide && /MSIE (\d+\.\d+);/.test(navigator.userAgent) == false)
            {
                        obj.animate(
                            { 
                                height: 'toggle'
                            },
                            {
                                duration: 200,
                                step: function(fx)
                                {
                                        Eureka.resize();
                                },
                                complete: function()
                                {
                                        Eureka.resize();
                                        Eureka.Container.adjustContentHeight();
                                }
                            }
                        );
            }
            else
            {
                obj.show();
                Eureka.resize();
            }
        }

        obj.hideSection = function(slide, dontPersist)
            {
            if (!dontPersist) Eureka.setAppDataForInstance(key, "false");
            
            expandButton.html("<span class='right-arrow'></span>");
            title.unbind("click");
            title.addClass("collapsed");
            title.click(function() { obj.showSection(true); });
            
            if(slide && /MSIE (\d+\.\d+);/.test(navigator.userAgent) == false)
            {
                obj.animate(
                    { 
                        height: 'toggle'
                    },
                    {
                        duration: 200,
                        step: function(fx)
                        {
                            Eureka.resize();
                        },
                        complete: function()
                        {
                            Eureka.resize();
                        }
                    }
                );
            }
            else
            {
                obj.hide();
                Eureka.resize();
            }
        }
        
        obj.hideSection(false, true);
        var viewerJSON = opensocial.data.DataContext.getDataSet("databindviewer");

        var dataSet = opensocial.data.DataContext.getDataSet("appDataSet")[viewerJSON.id];
        var data = null;

        if (dataSet != undefined)
        {
            data = dataSet[Eureka.getAppDataForInstanceKey(key)];
        }

            if (data == null)
            {
                if (expanded)
                {
                    obj.showSection(false, true);
                }
                else
                {
                    obj.hideSection(false, true);           
                }
            }
            else if (data == "true")
            {
                obj.showSection(false, true);
            }
            else
            {
                obj.hideSection(false, true);
            }

                Eureka.resize();

    
        obj.addClass("eureka-section-contents");
        section.append(obj);

    return section;
    }   
    
    this.getContainer = function()
    {
        return this.container;
    }
};

Eureka.ListItem = function(itemCount, primaryName, byLine, metaData, obj, onClick, slide, removeHandle)
{
    this.itemCount = itemCount;
    var item = jQuery("<div><div class='item-contents'>" + primaryName + " " + byLine + "</div><span class='fade-out'></span><div class='metadata'>"+metaData+"</div></div>");
    item.addClass("list-view-item");
    if (metaData != null && metaData != "")
    {
        item.addClass('with-metadata');
    }

    var removeLink = jQuery("<div class='gadget-remove-button'>Remove</div>");
    
    if (this.itemCount == 0)
    {
        item.addClass("first");
    }
    if (obj == null && onClick != null)
    {
        item.click( function() { if(!removeLink.is(":visible")) { onClick(itemCount); } });
    }
    else if (obj != null)
    {
        if (slide)
        {
            item.append("<span class='slide-arrow'>&gt;</span>");
            item.click(function() { if(!removeLink.is(":visible")) { Eureka.Container.switchView(obj); } if (onClick != null) { onClick(itemCount); }});
            setInterval(function() {
                item.find('.slide-arrow').css('margin-top', -1 * (item.height() / 2) - 5).show();
            }, 250);
        }
        else
        {
            item.click(function() { if(!removeLink.is(":visible")) {  jQuery(".list-view-item").removeClass("selected"); item.addClass("selected"); Eureka.Container.setContents(obj); } if (onClick != null) { onClick(itemCount); }});
        }
    }

    if (removeHandle != null)
    {
        removeLink.click(function(event) { removeHandle(itemCount); item.hide(); Eureka.resize(); event.stopPropagation(); });
        item.append(removeLink);
    }

    this.container = item;

    this.getContainer = function()
    {
        return this.container;
    }
}

Eureka.ListView = function()
{
    this.container = jQuery("<div></div>");
    this.itemCount = 0;
    var noItems = jQuery("<div class='no-items'>No Items to Display</div>");
    this.container.append(noItems);
    this.addItem = function(primaryName, byLine, metaData, obj, onClick, slide, removeHandle)
    {
        noItems.hide();
        var item = new Eureka.ListItem(this.itemCount, primaryName, byLine, metaData, obj, onClick, slide, removeHandle);
        this.container.append(item.getContainer());
        this.itemCount++;
        return item.getContainer();
    }
    
    this.getContainer = function()
    {
        return this.container;
    }
};

Eureka.FeedListView = function(url, canvas, inSectionName, indexToOpen, getFeedLink)
{
    var sectionName = inSectionName;
    var listView = new Eureka.ListView();
    //fetch feed
    var params={};
    params[gadgets.io.RequestParameters.METHOD]=gadgets.io.MethodType.GET;
    params[gadgets.io.RequestParameters.CONTENT_TYPE] = gadgets.io.ContentType.FEED;
    params[gadgets.io.RequestParameters.AUTHORIZATION] = gadgets.io.AuthorizationType.NONE;
    params[gadgets.io.RequestParameters.GET_SUMMARIES] = true;
    params[gadgets.io.RequestParameters.NUM_ENTRIES] = 7;
    if(canvas) params[gadgets.io.RequestParameters.NUM_ENTRIES] = 10;
    
    params[gadgets.io.RequestParameters.REFRESH_INTERVAL] = 0;
    gadgets.io.makeRequest(url,
            function(results)
            {
                if(!results.error)
                {

                    var feed = results.data;
            
                    if(feed.Entry)
                    {
                        for(var i = 0; i<feed.Entry.length; i++)
                        {
                            if(canvas)
                            {
                            	var date = new Date(feed.Entry[i].Date);
                            	var itemContent = jQuery('<div class="feed-content"><div class="feed-title"><a target="_blank" href="'+feed.Entry[i].Link+'">'+ feed.Entry[i].Title +'</a></div><div class="feed-date">'+ date.toString("MM dd, yyyy") +'</div>' + feed.Entry[i].Summary + '</div><a style="text-align:center;display:block;margin:10px" target="_blank" href="'+feed.Entry[i].Link+'">Click to Rate Article or Add a Comment</a>');
                            	itemContent.find('a').attr("target", "_blank");	
								itemContent = itemContent.add(jQuery('<div class="feed-item-controls"><a target="_self" href="javascript:Eureka.shareLink(\''+feed.Entry[i].Title+'\',\''+feed.Entry[i].Link+'\');">Share</a></div>'));
                                var currentItem = listView.addItem(feed.Entry[i].Title, '', '', itemContent, null, null, null);
                                if(indexToOpen == i)
                                {
                                    currentItem.click();
                                }
                            }
                            else
                            {
                                listView.addItem(feed.Entry[i].Title, '', '', null, function(itemCount){gadgets.views.requestNavigateTo('canvas', {selectedSection : sectionName, selectedIndex : itemCount});}, null, null);
                            }
                        }
            if (!canvas)
            {
                var lv = listView.addItem("See More News", '', '', null, function(itemCount){gadgets.views.requestNavigateTo('canvas', {selectedSection : sectionName, selectedIndex : 0});}, null, null);
                lv.addClass('see-more');
            }
            else
            {
                var lv = listView.addItem("View All " + sectionName, '', '', null, function(itemCount){ window.open(getFeedLink(feed)); }, null, null);
                lv.addClass('see-more');
            }
                    }
            Eureka.resize();
                }
                else
                {
                    alert("error occured");
                }
            }, params);
    //add items
    
    this.getContainer = function()
    {
        return listView.getContainer();
    }
};

Eureka.Tab = function(name, tabContainer)
{
    this.container = jQuery("<div class='tab'><div class='inner'>" + name + "</div></div>");
    
    this.container.click(function() { jQuery('.slide-arrow').hide(); tabContainer.switchToTab(name); Eureka.resize(); });
     
    this.getContainer = function()
    {
        return this.container;
    }
}

Eureka.TabPanel = function()
{   
    this.container = jQuery("<div></div>");

    this.tabBar = jQuery("<div class='tab-bar'></div>");
    this.tabContentContainer = jQuery("<div></div>");
    this.container.append(this.tabBar);
    this.container.append(this.tabContentContainer);
    
    this.tabs = [];
    this.tabContents = [];
    this.tabOnRender = [];
    this.currentTab = "";
    this.tabCount = 0;

    this.addTab = function(name, obj, last, onRender)
    {
        var tab = new Eureka.Tab(name, this);
        
        if (this.tabCount == 0)
        {
            tab.getContainer().addClass('first');
        }
        if (last)
        {
            tab.getContainer().addClass('last');
        }   
        this.tabCount++;

        this.tabs[name] = tab;
        this.tabContents[name] = obj;
        this.tabOnRender[name] = onRender;
        
        this.tabContents[name].hide();
        
        this.tabBar.append(tab.getContainer());
        this.tabContentContainer.append(this.tabContents[name]);
        
        if (this.currentTab == "")
        {
            this.switchToTab(name);
        }
    }

    this.switchToTab = function(name)
    {
        if (this.currentTab != "")
        {
            this.tabs[this.currentTab].getContainer().removeClass("selected");
            this.tabContents[this.currentTab].hide();
        }
        
        this.currentTab = name;

        if (this.tabOnRender[name] != null)
        {
            this.tabOnRender[name](name, this.tabContents[this.currentTab]);
            this.tabOnRender[name] = null;
        }

        this.tabs[this.currentTab].getContainer().addClass("selected");
        this.tabContents[this.currentTab].show().trigger('show');

        setTimeout(function() { Eureka.resize(); }, 500);
    }
    
    this.getContainer = function()
    {
        return this.container;
    }
};

Eureka.Help = function(content, titleText)
{
        this.container = jQuery("<div></div>");
    var title = jQuery("<div class='title-bar'></div>");
        title.append(jQuery("<a class='back-arrow' href=\"javascript:gadgets.views.requestNavigateTo('home');\"></a>"));
    if (titleText == null)
    {
        title.append("<strong>Help</strong>");
    }
    else
    {
        title.append("<strong>" + titleText + "</strong>");
    }
    this.container.append(title);
    
    var helpContainer = jQuery("<div class='gadget-help'></div>");
        helpContainer.append(content);

    this.container.append(helpContainer);
        this.getContainer = function()
        {
                return this.container;
        }
        this.renderInto = function(containerId)
        {
                jQuery("#" + containerId).append(this.getContainer());
                setTimeout(function() { Eureka.resize(); }, 100);
        }
}


Eureka.Table = function(tableData, grided)
{
    this.container = jQuery("<div></div>");

    var myTable = '' ;
    var gridedClass = '';
    if (grided)
    {
        gridedClass = 'grid';
    }
    myTable += '<div class="table-container"><table class="eureka-table ' + gridedClass  + '">' ;

    if (tableData.headers != null)
    {
        myTable +=  "<thead>" ;
        
        if (tableData.topHeaders != null)
        {
            myTable += "<tr class='header-row-top-headers'>";

            for (var i = 0; i < tableData.topHeaders.length; i++)
            {
                if(tableData.topHeaders[i].colspan != null)
                {
                    myTable += "<th colspan='" + tableData.topHeaders[i].colspan + "'>" + tableData.topHeaders[i].value + "</th>";
                }
                else
                {
                    myTable += "<th>" + tableData.topHeaders[i].value + "</th>";
                }
            }

            myTable += "</tr>";
        }
        myTable +=   "<tr class='header-row r0'>";

        for (var i = 0; i < tableData.headers.length; i++)
        {
            myTable += "<th class='data-column "+tableData.headers[i]+" c"+i+"'>" + tableData.headers[i] + "</th>";
        }

        myTable +=   "</tr>" ;
        myTable +=  "</thead>" ;
    }   
    myTable +=  "<tbody>" ;


    for (var i = 0; i < tableData.data.length; i++)
    {   
        var last = '';
        if (i == tableData.data.length-1)
        {
            last = 'last';
        }
        var rowNum = i+1;
        var oddOrEven = "even";
        if (i % 2 != 0)
        {
            oddOrEven = "odd";
        }

        myTable += "<tr class='data-row " + oddOrEven + " r" + rowNum + " " + last + "'>";

        var dataRow = tableData.data[i];
        
        for (var j = 0; j < dataRow.length; j++)
        {
            var count = j;
            if (tableData.headers != null)
            {
                if (j == tableData.headers.length)
                {
                    myTable += "</tr><tr class='subrow " + oddOrEven + " sr" + rowNum +" " + last + "'>";               
                }
                count = j%tableData.headers.length;
                myTable += "<td class='data-column "+tableData.headers[count]+" c"+count+"'>" + dataRow[j] + "</td>";
            }
            else
            {
                myTable += "<td class='data-column c"+count+"'>" + dataRow[j] + "</td>";
            }
        }
        myTable +=   "</tr>";
    }

    myTable +=  "</tbody>" ;
    myTable +=  "</table></div>" ;


    this.container.append(myTable);

    this.getContainer = function()
    {
        return this.container;
    }
}

Eureka.PostBox = function(text, postcb, maxlength, contentWarning)
{
		var input = jQuery("<input class='text-label' id='gadget-search' value='"+text+"' />");
		input.css("width", jQuery("body").width() - 66);
		var title = jQuery("<div class='title-bar collapsed search-bar'></div>");
		title.css("border-top", "1px solid #BBB");
    		title.append(jQuery("<div class='gadget-pre-textbox'></div>"));
		title.append(input);
		title.append(jQuery("<div class='gadget-post-search'></div>"));
 		
		var postComment = jQuery("<div class='post-contents'></div>");
		var commentInput = jQuery("<textarea rows='3' cols='30' />");
		var countDown = jQuery("<div class='post-count-down'>" + maxlength + "</div>");

        if (contentWarning != null)
        {
            postComment.append("<div class='content-warning'>" + contentWarning + "</div>");
        }

		commentInput.css("width", jQuery("body").width() - 30);
		postComment.append(commentInput);
		
		Eureka.EventBus.addObserver("goingBack", function(data) {
			if (data == 1)
			{
				commentInput.val("");
				postComment.hide();
				jQuery('.post-count-down').html(maxlength);		
				jQuery('.post-button').addClass('post-button-disabled');
			}
		    });

		var post = jQuery("<div class='post-button post-button-disabled'>Post</div>");
		post.click(function()
		{	
            if (!post.hasClass('post-button-disabled'))
            {
			    var comment = commentInput.val();
    			postcb(comment);
	    		commentInput.val("");
		    	postComment.hide();
            }
		});


		var cancel = jQuery("<span class='cancel-button'>Cancel</span>");
		cancel.click(function()
		{	
			commentInput.val("");
			postComment.hide();
			jQuery('.post-count-down').html(maxlength);		
			jQuery('.post-button').addClass('post-button-disabled');			
		});

		postComment.append(cancel);
		postComment.append(post);

        if (maxlength != null)
        {
		    postComment.append(countDown);
        }

		postComment.hide();
		jQuery("body").append(postComment);

		input.focus(function()
		{
            postComment.show();
			commentInput.focus();
   		});

            commentInput.keyup(function()
            {
                var textLength = commentInput.val().length;

                if (textLength > 0)
                {
                    jQuery('.post-button').removeClass('post-button-disabled');
                }
                else
                {
                    jQuery('.post-button').addClass('post-button-disabled');
                }

                if (maxlength != null)
                {
                    jQuery('.post-count-down').html(maxlength - textLength);

                    if (textLength <= maxlength && textLength > 0)
                    {
                        jQuery('.post-button').removeClass('post-button-disabled');
                    }
                    else
                    {
                        jQuery('.post-button').addClass('post-button-disabled');
                    }
                }

            });

    this.getContainer = function()
    {
        return title;
    }
}

Eureka.ScrolledList = function(obj, moreCallback, size)
{
	obj.css("position", "relative");
	obj.css("top", "0px");
    	this.container = jQuery("<div></div>");
        var objContainer = jQuery("<div class='scroll-list'></div>");
	objContainer.css("height", size + "px");
	objContainer.css("overflow", "hidden");
	objContainer.css("position", "relative");
	objContainer.append(obj);

	var less = jQuery("<div class='scroll-less'><span>less</span></div>");

	var more = jQuery("<div class='scroll-more'><span>more</span></div>");
	var scrollingDown = false;

	var thisBuffered = this;

	less.mousedown(function()
	{
		thisBuffered.scrollToTop();
	});

	less.mouseup(function()
	{
		obj.stop();
	});

	more.mousedown(function()
	{
		scrollingDown = true;
		thisBuffered.scrollToBottom();
	});

	more.mouseup(function()
	{
		obj.stop();
		scrollingDown = false;
	});

	this.container.append(less);
	this.container.append(objContainer);
        this.container.append(more);


	
	this.checkIfMore = function()
	{
		var height = parseInt(obj.css("height").replace("px", ""));
		if (height > 0)
		{
			if ((height - size) > 0)
			{	
				this.enableMore();
			}
		}
		else
		{
			setTimeout(function() { thisBuffered.checkIfMore(); }, 20);
		}
	}

	this.checkIfMore();
	
    	this.getContainer = function()
    	{
        	return this.container;
    	}

	this.appendToList = function(newObj)
	{
		obj.append(newObj);
		if (scrollingDown)
		{
			this.scrollToBottom();
		}
	}

	this.scrollToTop = function(speed)
	{
		if (speed == null)
		{
			speed = 2;
		}
		var top = -1*parseInt(obj.css("top").replace("px", ""));
		var height = parseInt(obj.css("height").replace("px", ""));
		var newTopAni = "+=" + top;
		obj.animate({
			top: newTopAni
		}, top*speed, function()
		{
			thisBuffered.disableLess();
		});

		thisBuffered.enableMore();
	}

	this.scrollToBottom = function(speed)
	{
		if (speed == null)
		{
			speed = 2;
		}
		var top = parseInt(obj.css("top").replace("px", ""));
		var height = parseInt(obj.css("height").replace("px", ""));
		if ((height - size) > 0)
		{	
			var newTop = height-size+top;
			var newTopAni = "-="+newTop;
			obj.animate({
				top: newTopAni
			}, newTop*speed, function()
			{
				moreCallback();
			});

			thisBuffered.enableLess();
		}
	}

	this.endOfList = function()
	{
		this.disableMore();
	}

	this.disableLess = function()
	{
		less.css("visibility", "hidden");
	}

	this.disableMore = function()
	{
		more.css("visibility", "hidden");
	}

	this.enableLess = function()
	{
		less.css("visibility", "visible");
	}

	this.enableMore = function()
	{
		more.css("visibility", "visible");
	}

	this.disableLess();
	this.disableMore();
}
Eureka.BasicContainer = function(obj, titleText, goBack, inCanvasRight)
{
    this.container = jQuery("<div></div>");
    var title = jQuery("<div class='title-bar'></div>");
    this.container.append(title);
    
    if(goBack)
    {
        title.addClass("add-back");
        title.append("<a class='back-arrow' href='javascript:Eureka.Container.goBack("+inCanvasRight+");'></a>");
    }
    
    if (titleText != "")
    {
        title.append("<strong>"+titleText+"</strong><span class='fade-out'></span>");
    }

    if (obj != null)
    {
        this.container.append(obj);
    }
    this.addElement = function(el)
    {
        this.container.append(el);
    }
    
    this.getContainer = function()
    {
        return this.container;
    }
}

Eureka.Cache =
{
    people : []
}

Eureka.RequestBatch = function()
{
    this.personCallbacks = {};
    this.personAccounts = [];

    this.addPersonRequest = function(name, cb)
    {
        if (this.personCallbacks[name] == undefined)
        {
            this.personCallbacks[name] = [];
        }

        this.personCallbacks[name].push(cb);

        if (jQuery.inArray(name, this.personAccounts) == -1)
        {
            this.personAccounts.push(name);
        }
    }    

    this.executePersonRequests = function()
    {
        var thisObj = this;
        var reqPeople = [];

        for (var i in this.personAccounts)
        {
            if (Eureka.Cache.people[this.personAccounts[i]] != undefined)
            {
                for (j in this.personCallbacks[this.personAccounts[i]])
                {
                    this.personCallbacks[this.personAccounts[i]][j](Eureka.Cache.people[this.personAccounts[i]]);
                }
            }
            else
            {
                reqPeople.push(this.personAccounts[i]);
            }
        }

        if (reqPeople.length > 0)
        {
            var req = opensocial.newDataRequest();
            var friendspec = opensocial.newIdSpec({userId : reqPeople, groupId : 'ALL'});
            req.add(req.newFetchPeopleRequest(friendspec), 'viewerfriends');
            req.send(function(result){
               if (!result.hadError())
               {
                   var friendsData = result.get('viewerfriends').getData();
                   friendsData.each(
                       function(person)
                       {
                           var escapedName = person.getDisplayName().replace(/&#39;/g,'\'');
                           person.getDisplayName = function() { return escapedName };

                           var accountId = person.getField('accounts')[0].username;
                           for (i in thisObj.personCallbacks[accountId])
                           {
                               thisObj.personCallbacks[accountId][i](person);
                           }
                           Eureka.Cache.people[accountId] = person;
                       }
                   );
               }
               else
               {
								 // do nothing
               }
           });
        }
    }
}

Eureka.setAppDataForInstance = function(inKey, value)
{
    var params = gadgets.util.getUrlParameters();
        var mid = params["mid"];
    var key = inKey + mid;
    var req = opensocial.newDataRequest();
    req.add(req.newUpdatePersonAppDataRequest(key, value));
    req.send(function(response) {});
}

Eureka.getAppDataForInstanceKey = function(inKey)
{
    var params = gadgets.util.getUrlParameters();
        var mid = params["mid"];
    return inKey + mid;
}

Eureka.getAppDataForInstance = function(inKey, callback)
{
    var params = gadgets.util.getUrlParameters();
        var mid = params["mid"];
    var key = inKey + mid;
    var req = opensocial.newDataRequest();
    req.add(req.newFetchPersonRequest("VIEWER"), 'viewer'); 
    var viewer = opensocial.newIdSpec({ "userId" : "VIEWER" });
    req.add(req.newFetchPersonAppDataRequest(viewer, key), "get_data"); // * means we load ALL data
    req.send(function(response) { 
        var viewerId = response.get('viewer').getData().getField('id');
        var data = response.get('get_data').getData()[viewerId][key];
        callback(data);
    });
}
Eureka.resize = function()
{

    var tallest = 0;
    jQuery('.app-container').children().each(function()
    {
        var height = jQuery(this).height();
        if (height  > tallest)
        {
            tallest = height;
        }
    });

    jQuery('.app-container').height(tallest);
    gadgets.window.adjustHeight(jQuery('body').height());
}

Eureka.authorizeNoLookup = function(allowedOrgs, org)
{
    var authed = false;
    for (var k in allowedOrgs)
    {
        if (allowedOrgs[k] == org)
        {
            authed = true;
        }
    }
    if (!authed)
    {
        if (org == null)
        {
            Eureka.Container.unAuthed("We are unable to fetch the data required for this application at the moment. Please try back later.");
        }
        else
        {
            Eureka.Container.unAuthed("This app is not available for your business area.  Please visit the <a target='_blank' href='https://passport.global.lmco.com'>Passport site</a> to view more information on your business area’s passport replacement resources.");
        }
    }
}

Eureka.authorize = function(allowedOrgs)
{
    Eureka.getCurrentUserOrg(function(org) {
        var authed = false;
        for (var k in allowedOrgs)
        {
            if (allowedOrgs[k] == org)
            {
                authed = true;
            }
        }
        if (!authed)
        {
            if (org == null)
            {
                Eureka.Container.unAuthed("We are unable to fetch the data required for this application at the moment. Please try back later.");
            }
            else
            {
                Eureka.Container.unAuthed("This app is not available for your business area.  Please visit the <a target='_blank' href='https://passport.global.lmco.com'>Passport site</a> to view more information on your business area’s passport replacement resources.");
            }
        }
    });
}

Eureka.makeOauthRequest = function(url, cb, refreshInterval)
{
                		var params={};
                		params[gadgets.io.RequestParameters.METHOD]=gadgets.io.MethodType.GET;
                		params[gadgets.io.RequestParameters.CONTENT_TYPE] = gadgets.io.ContentType.JSON;
                		params[gadgets.io.RequestParameters.AUTHORIZATION] = gadgets.io.AuthorizationType.OAUTH;
                		params[gadgets.io.RequestParameters.REFRESH_INTERVAL] = refreshInterval;
                		params[gadgets.io.RequestParameters.OAUTH_SERVICE_NAME] = "eurekastreams";
                		params[gadgets.io.RequestParameters.OAUTH_USE_TOKEN] = "never";

      
                		gadgets.io.makeRequest(url,
                    		function(results)
                    		{
                        		if(!results.error)
                        		{
						cb(results);
                        		}
		                        else
                		        {
		                            alert("error occured");
                		        }
                    		}, params);
}

Eureka.makeSignedRequest = function(url, cb, errorcb, refreshInterval)
{
                        var params={};
                        params[gadgets.io.RequestParameters.METHOD]=gadgets.io.MethodType.GET;
                        params[gadgets.io.RequestParameters.CONTENT_TYPE] = gadgets.io.ContentType.JSON;
                        params[gadgets.io.RequestParameters.AUTHORIZATION] = gadgets.io.AuthorizationType.SIGNED;
                        params[gadgets.io.RequestParameters.REFRESH_INTERVAL] = refreshInterval;

      
                        gadgets.io.makeRequest(url,
                            function(results)
                            {
                                if(!results.error)
                                {
                                    cb(results);
                                }
                                else
                                {
                                    errorcb(results.error);
                                }
                            }, params);
}

Eureka.shareLink = function(title, url)
{
               var params = {};
               params[opensocial.Activity.Field.TITLE] = " ";
               params[opensocial.Activity.Field.TEMPLATE_PARAMS] = {};
               params[opensocial.Activity.Field.TEMPLATE_PARAMS]["baseObjectType"] = "BOOKMARK";
               params[opensocial.Activity.Field.TEMPLATE_PARAMS]["targetUrl"] = url;
               params[opensocial.Activity.Field.TEMPLATE_PARAMS]["targetTitle"] = title;
               params[opensocial.Activity.Field.TEMPLATE_PARAMS]["description"] = "";
               params[opensocial.Activity.Field.TEMPLATE_PARAMS]["thumbnail"] = "";
               var activity = opensocial.newActivity(params);
               opensocial.requestCreateActivity(activity, opensocial.CreateActivityPriority.HIGH, 
                   function(result) 
                   {
                       if(!result.hadError())
                       {
                           eurekastreams.core.triggerShowNotificationEvent("Item Shared");
                       }
                       else
                       {
                           eurekastreams.core.triggerShowNotificationEvent("Error Sharing Item");
                       }
                   });
}

Eureka.getCurrentUserOrg = function(callback)
{
                var url= '${build.app.baseurl}/resources/dataservice/datastore/functionaltree/key/eureka:currentuser';

                var params={};
                params[gadgets.io.RequestParameters.METHOD]=gadgets.io.MethodType.GET;
                params[gadgets.io.RequestParameters.CONTENT_TYPE] = gadgets.io.ContentType.JSON;
                params[gadgets.io.RequestParameters.AUTHORIZATION] = gadgets.io.AuthorizationType.OAUTH;
                params[gadgets.io.RequestParameters.REFRESH_INTERVAL] = 1;
                params[gadgets.io.RequestParameters.OAUTH_SERVICE_NAME] = "eurekaapps";
                params[gadgets.io.RequestParameters.OAUTH_USE_TOKEN] = "never";

                gadgets.io.makeRequest(url,
                    function(results)
                    {
                        if(!results.error)
                        {
                if (results.data == null)
                {
                callback("unknown");
                } else {
                var data = results.data.orgCode;
                    var returnData = "";    

                if (data == null)
                {
                    returnData = "";
                }
                else if (data.indexOf("Info Sys & Global") != -1 || data.indexOf("IS&GS") != -1)
                {
                    returnData = "isgs";
                }
                else if (data.indexOf("Enterprise Business Services") != -1)
                {
                    returnData = "ebs";
                }
                else if (data.indexOf("Corporate") != -1)
                {
                    returnData = "corp";
                }

                callback(returnData);
                }
                        }
                        else
                        {
                            alert("error occured");
                        }
                    }, params);


}

var Url = {
 
	// public method for url encoding
	encode : function (string) {
		return escape(this._utf8_encode(string));
	},
 
	// public method for url decoding
	decode : function (string) {
		return this._utf8_decode(unescape(string));
	},
 
	// private method for UTF-8 encoding
	_utf8_encode : function (string) {
		string = string.replace(/\r\n/g,"\n");
		var utftext = "";
 
		for (var n = 0; n < string.length; n++) {
 
			var c = string.charCodeAt(n);
 
			if (c < 128) {
				utftext += String.fromCharCode(c);
			}
			else if((c > 127) && (c < 2048)) {
				utftext += String.fromCharCode((c >> 6) | 192);
				utftext += String.fromCharCode((c & 63) | 128);
			}
			else {
				utftext += String.fromCharCode((c >> 12) | 224);
				utftext += String.fromCharCode(((c >> 6) & 63) | 128);
				utftext += String.fromCharCode((c & 63) | 128);
			}
 
		}
 
		return utftext;
	},
 
	// private method for UTF-8 decoding
	_utf8_decode : function (utftext) {
		var string = "";
		var i = 0;
		var c = c1 = c2 = 0;
 
		while ( i < utftext.length ) {
 
			c = utftext.charCodeAt(i);
 
			if (c < 128) {
				string += String.fromCharCode(c);
				i++;
			}
			else if((c > 191) && (c < 224)) {
				c2 = utftext.charCodeAt(i+1);
				string += String.fromCharCode(((c & 31) << 6) | (c2 & 63));
				i += 2;
			}
			else {
				c2 = utftext.charCodeAt(i+1);
				c3 = utftext.charCodeAt(i+2);
				string += String.fromCharCode(((c & 15) << 12) | ((c2 & 63) << 6) | (c3 & 63));
				i += 3;
			}
 
		}
 
		return string;
	}
 
}

Eureka.Util = 
{
	isArray : function (v)
	{
		// technique from "JavaScript: The Good Parts" by Douglas Crockford
		return Object.prototype.toString.apply(v) === '[object Array]';
	}
}

Eureka.Api = function(version,access)
{
	var baseUrl = '${build.web.baseurl}/api/' + version + '/' + access;
	
	// Builds the URL for the Eureka Streams action API.
	// apiName = [string] Name of API to call (e.g. getSystemSettings)
	// request = [string/number/object, optional] request data for the API.  (Note:  This is the request itself, not
	//     the entire parameter set.  Does not include the keyword 'request' needed by the API because this method will
	//     add that.)
	// params = [string/object, optional] the entire parameter set for the API.
	// Use request or params, not both.
	this.buildUrl = function(apiName, request, params)
	{
		if (params)
		{
			if (typeof params != 'string')
				params = JSON.stringify(params);
		}
		else if (request)
		{
			if (typeof request == 'object')
				request = JSON.stringify(request);
			params = '{"request":' + request + '}';
		}
		else
			params = '{}';
		
		return baseUrl + '/' + apiName + '/' + encodeURIComponent(params);
	}

	// Sends a set of Eureka Streams API requests in parallel and invokes a callback when all have returned.
	// requests = [object/array] An object or array of objects representing the requests to make.  Each contains the
	//     following fields:
	//     - api:  [string] name of API to invoke
	//     - request:  [object/number/string, optional] API request object (the value of 'request' in the API parameter 
	//         set; see buildUrl's 'request' parameter)
	//     - params:  [object/string, optional] API parameter set (see buildUrl's 'params' parameter) 
	//     - refreshInterval:  [number, optional] value for gadgets.io.RequestParameters.REFRESH_INTERVAL.  Default is 1.
	//     - callback:  [function(results), optional] a callback invoked when this request's results are received
	// finalCallback = [function(responseMap), optional] the callback invoked when all API calls have completed.  Passed
	//     an object whose keys are the names of the APIs called and whose values are the responses.
	//
	// Notes:
	// - If the request list specifies the same API more than once, each will be called properly, but only one of the
	// results will end up in the response map.
	// - The final callback will not be invoked if the request list is empty.
	this.makeRequests = function (requests, finalCallback)
	{
		var makeApiRequest = function(apiName,url,refreshInterval,callback)
		{
			Eureka.makeOauthRequest(url,
				function(results)
				{
					responseMap[apiName] = results;
					outstanding--;
					if (callback)
					{
						try
						{
							callback(results);
						}
						catch(ex)
						{
							gadgets.log('Callback for ' + apiName + ' threw an exception (' + ex.name + ' - ' + ex.message + ')');
						}
					}
					if (!outstanding && finalCallback)
					{
						finalCallback(responseMap);
					}
				}, refreshInterval || 1);
		};
	
		var responseMap = {};
		if (!Eureka.Util.isArray(requests))
			requests = [requests];
		var outstanding = requests.length;
		var i;
		var rqst;
		for (i=0; i < requests.length; i++)
		{
			rqst = requests[i];
			makeApiRequest(rqst.api, this.buildUrl(rqst.api, rqst.request, rqst.params), rqst.refreshInterval, rqst.callback);
		}
	}
}

Eureka.Api.Full = new Eureka.Api('0', 'full');
Eureka.Api.ReadOnly = new Eureka.Api('0', 'read');


Eureka.Format = function() {

	var markdownLinkReplacer = function (t, r1, r2)
	{
		if (!r2 || isNumber(r2))
			r2 = r1;
		if (r2 && r2.charAt && r2.charAt(0) === '#')
			r2 = '${build.web.baseurl}' + r2;
		return ' <a href="' + r2 + '" target="_blank">' + r1 + '</a>';
	};
	
	function isNumber(n) 
	{
		return !isNaN(parseFloat(n)) && isFinite(n);
	}

	return {
		// Generates HTML for a markdown string.  Somewhat clumsy, and only supports links.
		markdownToHtml : function (str)
		{
			// HTML escape content
			str = jQuery('<div/>').text(str).html();
			// convert newlines
			str = str.replace(/(?:\r\n|\n|\r)/g, '<br />');
			
			// markdown link conversion
			
			// first, replace bare URLs that are not in parens (ones that aren't markdown)
			var linkRe = /(?:^|[^(])((?:https?|ftp|file):\/\/[-A-Z0-9+&@#\/%?=~_|!:,.;]*[-A-Z0-9+&@#\/%=~_|])/ig;
			str = str.replace(linkRe, markdownLinkReplacer);

			// next, replace markdown links
			var re = /\[([^\]\[]+)\]\(([\:\.-A-Za-z0-9+&@#\/%=~_|]*)\)/g;
			str = str.replace(re, markdownLinkReplacer);

			// finally, replace URLs in parens
			var linkRe2 = /\(((?:https?|ftp|file):\/\/[-A-Z0-9+&@#\/%?=~_|!:,.;]*[-A-Z0-9+&@#\/%=~_|])\)/ig;
			str = str.replace(linkRe2, markdownLinkReplacer);

			return str;
		}
	};
}();
