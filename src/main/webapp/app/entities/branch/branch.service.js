(function() {
    'use strict';
    angular
        .module('customeranalyticsApp')
        .factory('Branch', Branch);

    Branch.$inject = ['$resource'];

    function Branch ($resource) {
        var resourceUrl =  'api/branches/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
