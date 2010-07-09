var eurekastreams = eurekastreams || {};
eurekastreams.samplefeature = eurekastreams.samplefeature || {};

eurekastreams.samplefeature.container = function()
{    
    return{
        init: function()
        {
            gadgets.rpc.register('testFeature', eurekastreams.samplefeature.container.testFeature);    
        },
        
        testFeature: function()
        {
            alert('You called the Sample Feature Container javascript - good for you.');
        }
    };
}();    
eurekastreams.samplefeature.container.init();
