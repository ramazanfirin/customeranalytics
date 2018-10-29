(function() {
    'use strict';

    angular
        .module('customeranalyticsApp')
        .controller('RecordController', RecordController);

    RecordController.$inject = ['$state', 'Record', 'ParseLinks', 'AlertService', 'paginationConstants', 'pagingParams','$interval'];

    function RecordController($state, Record, ParseLinks, AlertService, paginationConstants, pagingParams,$interval) {

        var vm = this;

        vm.deleteAll = deleteAll;
        vm.loadPage = loadPage;
        vm.predicate = pagingParams.predicate;
        //vm.reverse  = pagingParams.ascending;
        vm.reverse = false;
        vm.transition = transition;
        vm.itemsPerPage = paginationConstants.itemsPerPage;

        loadAll();
        
        $interval(loadAll, 1000);

        function deleteAll(){
        	Record.deleteAll();
        }
        
        function loadAll () {
            Record.query({
                page: pagingParams.page - 1,
                size: vm.itemsPerPage,
                sort: sort()
            }, onSuccess, onError);
            function sort() {
                var result = [vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc')];
                if (vm.predicate !== 'id') {
                    result.push('id');
                }
                return result;
            }
            function onSuccess(data, headers) {
                vm.links = ParseLinks.parse(headers('link'));
                vm.totalItems = headers('X-Total-Count');
                vm.queryCount = vm.totalItems;
                vm.records = data;
                vm.page = pagingParams.page;
            }
            function onError(error) {
                AlertService.error(error.data.message);
            }
        }

        
        
        function loadPage(page) {
            vm.page = page;
            vm.transition();
        }

        function transition() {
            $state.transitionTo($state.$current, {
                page: vm.page,
                sort: vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc'),
                search: vm.currentSearch
            });
        }
    }
})();
