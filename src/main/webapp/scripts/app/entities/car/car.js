'use strict';

angular.module('traceApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('car', {
                parent: 'entity',
                url: '/cars',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'traceApp.car.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/car/cars.html',
                        controller: 'CarController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('car');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            })
            .state('car.detail', {
                parent: 'entity',
                url: '/car/{id}',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'traceApp.car.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/car/car-detail.html',
                        controller: 'CarDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('car');
                        return $translate.refresh();
                    }],
                    entity: ['$stateParams', 'Car', function($stateParams, Car) {
                        return Car.get({id : $stateParams.id});
                    }]
                }
            })
            .state('car.new', {
                parent: 'car',
                url: '/new',
                data: {
                    roles: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$modal', function($stateParams, $state, $modal) {
                    $modal.open({
                        templateUrl: 'scripts/app/entities/car/car-dialog.html',
                        controller: 'CarDialogController',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {type: null, seats: null, id: null};
                            }
                        }
                    }).result.then(function(result) {
                        $state.go('car', null, { reload: true });
                    }, function() {
                        $state.go('car');
                    })
                }]
            })
            .state('car.edit', {
                parent: 'car',
                url: '/{id}/edit',
                data: {
                    roles: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$modal', function($stateParams, $state, $modal) {
                    $modal.open({
                        templateUrl: 'scripts/app/entities/car/car-dialog.html',
                        controller: 'CarDialogController',
                        size: 'lg',
                        resolve: {
                            entity: ['Car', function(Car) {
                                return Car.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('car', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            });
    });
