'use strict';

angular.module('traceApp')
    .controller('BikeController', function ($scope, Bike) {
        $scope.bikes = [];
        $scope.loadAll = function() {
            Bike.query(function(result) {
               $scope.bikes = result;
            });
        };
        $scope.loadAll();

        $scope.delete = function (id) {
            Bike.get({id: id}, function(result) {
                $scope.bike = result;
                $('#deleteBikeConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Bike.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteBikeConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.bike = {type: null, modelno: null, id: null};
        };
    });
