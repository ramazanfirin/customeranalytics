(function() {
    'use strict';

    angular
        .module('customeranalyticsApp')
        .controller('StuffDetailController', StuffDetailController);

    StuffDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'DataUtils', 'entity', 'Stuff', 'Branch'];

    function StuffDetailController($scope, $rootScope, $stateParams, previousState, DataUtils, entity, Stuff, Branch) {
        var vm = this;

        vm.stuff = entity;
        vm.previousState = previousState.name;
        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;

        var unsubscribe = $rootScope.$on('customeranalyticsApp:stuffUpdate', function(event, result) {
            vm.stuff = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
