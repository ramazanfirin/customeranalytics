(function() {
    'use strict';

    angular
        .module('customeranalyticsApp')
        .controller('BranchDeleteController',BranchDeleteController);

    BranchDeleteController.$inject = ['$uibModalInstance', 'entity', 'Branch'];

    function BranchDeleteController($uibModalInstance, entity, Branch) {
        var vm = this;

        vm.branch = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Branch.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
