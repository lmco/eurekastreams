/**
 * This feature is a simple feature to demonstrate how to 
 * create features and include them with Shindig.
 */
var eurekastreams = eurekastreams || {};

eurekastreams.samplefeature = eurekastreams.samplefeature || {};

/**
 * Simple alert to verify that the feature has been loaded.
 */
eurekastreams.samplefeature.initsamplefeature = function()
{
	alert('You have successfully used the samplefeature');
};

eurekastreams.samplefeature.testSampleFeatureContainer = function()
{
    gadgets.rpc.call(null, "testFeature", null);
};