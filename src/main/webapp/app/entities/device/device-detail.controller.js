(function() {
    'use strict';

    angular
        .module('customeranalyticsApp')
        .controller('DeviceDetailController', DeviceDetailController);

    DeviceDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Device', 'Branch'];

    function DeviceDetailController($scope, $rootScope, $stateParams, previousState, entity, Device, Branch) {
        var vm = this;

        vm.device = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('customeranalyticsApp:deviceUpdate', function(event, result) {
            vm.device = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
