<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
    version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
        
        
	<xsl:output method="text" indent="no" encoding="UTF-8"/>
           
	<xsl:template match="/Theme">
	div.banner-container
	{
		background: 
			<xsl:value-of select="HeaderBackground/HeaderBackgroundColor"/> 
			<xsl:apply-templates select="HeaderBackground/HeaderBackgroundImage"/>;
			
      	        border-bottom: 1px solid <xsl:value-of select="ActiveTabBorderColor"/>; 		
	}

        .themeable .site-labeling
        {
            color: <xsl:value-of select="HeaderBackground/HeaderForegroundColor"/>;
        }
        
        .themeable .site-labeling a:link, .themeable .site-labeling a:visited, .themeable .site-labeling a:hover, .themeable .site-labeling a:active
        {
            color: <xsl:value-of select="HeaderBackground/HeaderForegroundColor"/>;
            text-decoration: underline;
        }

	.themeable div.tab, .themeable div.new-tab-button
	{ 
	      background-color: <xsl:value-of select="InactiveTabBackgroundColor"/>;
	      border-color:  <xsl:value-of select="InactiveTabBorderColor"/>;	      
	}

        .themeable .configure-tab
	{ 
	      background-color: <xsl:value-of select="InactiveTabBackgroundColor"/>;
	      border-color: <xsl:value-of select="InactiveTabForegroundColor"/>;
        }

        .themeable .configure-tab a
        {
	      color: <xsl:value-of select="InactiveTabForegroundColor"/>;
        }

	.themeable div.tab-container div.active
	{
		background-color: <xsl:value-of select="PageBackground/PageBackgroundColor"/>;
	}

	.themeable div.tab-container div.active
	{
      	border-top: 1px solid <xsl:value-of select="ActiveTabBorderColor"/>; 
      	border-left: 1px solid <xsl:value-of select="ActiveTabBorderColor"/>; 
      	border-right: 1px solid <xsl:value-of select="ActiveTabBorderColor"/>; 
      	border-bottom:1px solid <xsl:value-of select="PageBackground/PageBackgroundColor"/>;	      
	}
	
	.themeable div.tab-container div.active div div.gwt-Label
	{
      	color: <xsl:value-of select="ActiveTabForegroundColor"/>; 
	}

	.themeable div.layout-container
	{
      	border-color: <xsl:value-of select="ActiveTabBorderColor"/>;
	}
	.themeable div.tab div div.gwt-Label, .themeable div.new-tab-button a
	{
	      color: <xsl:value-of select="InactiveTabForegroundColor"/>;
	}
	
	.themeable .gadget-zone
	{ 
	      border: 1px solid <xsl:value-of select="GadgetBorderColor"/> ! important;
	}

	.themeable .gadget-zone-chrome-title-bar
	{ 
	      background-color: <xsl:value-of select="GadgetTitleBackgroundColor"/>;
	}
	
	.themeable .gadget-zone-chrome-title-bar-title-button
	{ 
	      color: <xsl:value-of select="GadgetTitleForegroundColor"/>;
	}

	.themeable .main-contents
	{
		background: 
			<xsl:value-of select="PageBackground/PageBackgroundColor"/> 
			<xsl:apply-templates select="PageBackground/PageBackgroundImage"/>
			 0 125px;
	}

	</xsl:template>

	<xsl:template match="PageBackground/PageBackgroundImage"> 
			url(<xsl:value-of select="."/>)
			<xsl:choose>
				<xsl:when test="./@RepeatX and not(./@RepeatY)">repeat-x</xsl:when>
				<xsl:when test="./@RepeatY and not(./@RepeatX)">repeat-y</xsl:when>
				<xsl:when test="not(./@RepeatY) and not(./@RepeatX)">no-repeat</xsl:when>
				<xsl:otherwise>repeat</xsl:otherwise>
			</xsl:choose>
	</xsl:template>
	
	<xsl:template match="HeaderBackground/HeaderBackgroundImage"> 
			url(<xsl:value-of select="."/>)
			<xsl:choose>
				<xsl:when test="./@Center">no-repeat center</xsl:when>
				<xsl:when test="not(./@Center)">repeat</xsl:when>
			</xsl:choose>
	</xsl:template>	

</xsl:stylesheet> 
