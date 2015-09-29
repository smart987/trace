'use strict';

angular.module('traceApp')
    .controller('BikeDetailController', function ($scope, $rootScope, $stateParams, entity, Bike, Driver) {
        $scope.bike = entity;
        $scope.load = function (id) {
            Bike.get({id: id}, function(result) {
                $scope.bike = result;
            });
        };
        $rootScope.$on('traceApp:bikeUpdate', function(event, result) {
            $scope.bike = result;
        });
    });
