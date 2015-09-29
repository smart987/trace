'use strict';

angular.module('traceApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('driver', {
                parent: 'entity',
                url: '/drivers',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'traceApp.driver.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/driver/drivers.html',
                        controller: 'DriverController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('driver');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            })
            .state('driver.detail', {
                parent: 'entity',
                url: '/driver/{id}',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'traceApp.driver.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/driver/driver-detail.html',
                        controller: 'DriverDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('driver');
                        return $translate.refresh();
                    }],
                    entity: ['$stateParams', 'Driver', function($stateParams, Driver) {
                        return Driver.get({id : $stateParams.id});
                    }]
                }
            })
            .state('driver.new', {
                parent: 'driver',
                url: '/new',
                data: {
                    roles: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$modal', function($stateParams, $state, $modal) {
                    $modal.open({
                        templateUrl: 'scripts/app/entities/driver/driver-dialog.html',
                        controller: 'DriverDialogController',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {name: null, gender: null, id: null};
                            }
                        }
                    }).result.then(function(result) {
                        $state.go('driver', null, { reload: true });
                    }, function() {
                        $state.go('driver');
                    })
                }]
            })
            .state('driver.edit', {
                parent: 'driver',
                url: '/{id}/edit',
                data: {
                    roles: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$modal', function($stateParams, $state, $modal) {
                    $modal.open({
                        templateUrl: 'scripts/app/entities/driver/driver-dialog.html',
                        controller: 'DriverDialogController',
                        size: 'lg',
                        resolve: {
                            entity: ['Driver', function(Driver) {
                                return Driver.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('driver', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            });
    });
