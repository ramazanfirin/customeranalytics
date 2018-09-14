(function() {
    'use strict';

    angular
        .module('customeranalyticsApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('stuff', {
            parent: 'entity',
            url: '/stuff?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'customeranalyticsApp.stuff.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/stuff/stuffs.html',
                    controller: 'StuffController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'id,asc',
                    squash: true
                },
                search: null
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        search: $stateParams.search
                    };
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('stuff');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('stuff-detail', {
            parent: 'stuff',
            url: '/stuff/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'customeranalyticsApp.stuff.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/stuff/stuff-detail.html',
                    controller: 'StuffDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('stuff');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Stuff', function($stateParams, Stuff) {
                    return Stuff.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'stuff',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('stuff-detail.edit', {
            parent: 'stuff-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/stuff/stuff-dialog.html',
                    controller: 'StuffDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Stuff', function(Stuff) {
                            return Stuff.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('stuff.new', {
            parent: 'stuff',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/stuff/stuff-dialog.html',
                    controller: 'StuffDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                name: null,
                                surname: null,
                                email: null,
                                phone: null,
                                image: null,
                                imageContentType: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('stuff', null, { reload: 'stuff' });
                }, function() {
                    $state.go('stuff');
                });
            }]
        })
        .state('stuff.edit', {
            parent: 'stuff',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/stuff/stuff-dialog.html',
                    controller: 'StuffDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Stuff', function(Stuff) {
                            return Stuff.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('stuff', null, { reload: 'stuff' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('stuff.delete', {
            parent: 'stuff',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/stuff/stuff-delete-dialog.html',
                    controller: 'StuffDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Stuff', function(Stuff) {
                            return Stuff.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('stuff', null, { reload: 'stuff' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
