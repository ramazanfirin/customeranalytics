(function() {
    'use strict';
    angular
        .module('customeranalyticsApp')
        .factory('Record', Record);

    Record.$inject = ['$resource', 'DateUtils'];

    function Record ($resource, DateUtils) {
        var resourceUrl =  'api/records/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
			'deleteAll': { method: 'GET', isArray: false,url:'/api/records/deleteAll'},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.insert = DateUtils.convertDateTimeFromServer(data.insert);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
