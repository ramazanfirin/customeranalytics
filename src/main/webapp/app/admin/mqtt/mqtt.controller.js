(function () {
    'use strict';

    angular
        .module('customeranalyticsApp')
        .controller('MqttController', MqttController);

    MqttController.$inject = ['$cookies', '$http', 'JhiTrackerService'];

    function MqttController ($cookies, $http, JhiTrackerService) {
        // This controller uses a Websocket connection to receive user activities in real-time.
        var vm = this;
        
        vm.records = [];
    }
    
    function onConnect() {
    	  // Once a connection has been made, make a subscription and send a message.
    	  console.log("onConnect");
    	  client.subscribe("face");

    }
    
    function onConnectionLost(responseObject) {
    	  if (responseObject.errorCode !== 0) {
    	    console.log("onConnectionLost:"+responseObject.errorMessage);
    	  }
    	}

    	// called when a message arrives
    	function onMessageArrived(message) {
    	  console.log("onMessageArrived:"+message.payloadString);
    	}
    
})();
