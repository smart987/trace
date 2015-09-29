'use strict';

angular.module('traceApp')
    .controller('CarDetailController', function ($scope, $rootScope, $stateParams, entity, Car, Owner) {
        $scope.car = entity;
        $scope.load = function (id) {
            Car.get({id: id}, function(result) {
                $scope.car = result;
            });
        };
        $rootScope.$on('traceApp:carUpdate', function(event, result) {
            $scope.car = result;
        });
    });
