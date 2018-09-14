(function() {
    'use strict';

    angular
        .module('customeranalyticsApp')
        .controller('RecordDetailController', RecordDetailController);

    RecordDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Record', 'Device', 'Stuff'];

    function RecordDetailController($scope, $rootScope, $stateParams, previousState, entity, Record, Device, Stuff) {
        var vm = this;

        vm.record = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('customeranalyticsApp:recordUpdate', function(event, result) {
            vm.record = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
