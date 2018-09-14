(function() {
    'use strict';

    angular
        .module('customeranalyticsApp')
        .controller('StuffDialogController', StuffDialogController);

    StuffDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'DataUtils', 'entity', 'Stuff', 'Branch'];

    function StuffDialogController ($timeout, $scope, $stateParams, $uibModalInstance, DataUtils, entity, Stuff, Branch) {
        var vm = this;

        vm.stuff = entity;
        vm.clear = clear;
        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;
        vm.save = save;
        vm.branches = Branch.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.stuff.id !== null) {
                Stuff.update(vm.stuff, onSaveSuccess, onSaveError);
            } else {
                Stuff.save(vm.stuff, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('customeranalyticsApp:stuffUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


        vm.setImage = function ($file, stuff) {
            if ($file && $file.$error === 'pattern') {
                return;
            }
            if ($file) {
                DataUtils.toBase64($file, function(base64Data) {
                    $scope.$apply(function() {
                        stuff.image = base64Data;
                        stuff.imageContentType = $file.type;
                    });
                });
            }
        };

    }
})();
