'use strict';

angular.module('traceApp')
    .controller('OwnerDetailController', function ($scope, $rootScope, $stateParams, entity, Owner, Car) {
        $scope.owner = entity;
        $scope.load = function (id) {
            Owner.get({id: id}, function(result) {
                $scope.owner = result;
            });
        };
        $rootScope.$on('traceApp:ownerUpdate', function(event, result) {
            $scope.owner = result;
        });
    });
