'use strict';

angular.module('traceApp')
    .controller('DriverDetailController', function ($scope, $rootScope, $stateParams, entity, Driver, Bike) {
        $scope.driver = entity;
        $scope.load = function (id) {
            Driver.get({id: id}, function(result) {
                $scope.driver = result;
            });
        };
        $rootScope.$on('traceApp:driverUpdate', function(event, result) {
            $scope.driver = result;
        });
    });
