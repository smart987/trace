'use strict';

angular.module('traceApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('bike', {
                parent: 'entity',
                url: '/bikes',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'traceApp.bike.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/bike/bikes.html',
                        controller: 'BikeController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('bike');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            })
            .state('bike.detail', {
                parent: 'entity',
                url: '/bike/{id}',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'traceApp.bike.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/bike/bike-detail.html',
                        controller: 'BikeDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('bike');
                        return $translate.refresh();
                    }],
                    entity: ['$stateParams', 'Bike', function($stateParams, Bike) {
                        return Bike.get({id : $stateParams.id});
                    }]
                }
            })
            .state('bike.new', {
                parent: 'bike',
                url: '/new',
                data: {
                    roles: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$modal', function($stateParams, $state, $modal) {
                    $modal.open({
                        templateUrl: 'scripts/app/entities/bike/bike-dialog.html',
                        controller: 'BikeDialogController',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {type: null, modelno: null, id: null};
                            }
                        }
                    }).result.then(function(result) {
                        $state.go('bike', null, { reload: true });
                    }, function() {
                        $state.go('bike');
                    })
                }]
            })
            .state('bike.edit', {
                parent: 'bike',
                url: '/{id}/edit',
                data: {
                    roles: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$modal', function($stateParams, $state, $modal) {
                    $modal.open({
                        templateUrl: 'scripts/app/entities/bike/bike-dialog.html',
                        controller: 'BikeDialogController',
                        size: 'lg',
                        resolve: {
                            entity: ['Bike', function(Bike) {
                                return Bike.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('bike', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            });
    });
