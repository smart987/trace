'use strict';

angular.module('traceApp').controller('BikeDialogController',
    ['$scope', '$stateParams', '$modalInstance', 'entity', 'Bike', 'Driver',
        function($scope, $stateParams, $modalInstance, entity, Bike, Driver) {

        $scope.bike = entity;
        $scope.drivers = Driver.query();
        $scope.load = function(id) {
            Bike.get({id : id}, function(result) {
                $scope.bike = result;
            });
        };

        var onSaveFinished = function (result) {
            $scope.$emit('traceApp:bikeUpdate', result);
            $modalInstance.close(result);
        };

        $scope.save = function () {
            if ($scope.bike.id != null) {
                Bike.update($scope.bike, onSaveFinished);
            } else {
                Bike.save($scope.bike, onSaveFinished);
            }
        };

        $scope.clear = function() {
            $modalInstance.dismiss('cancel');
        };
}]);
