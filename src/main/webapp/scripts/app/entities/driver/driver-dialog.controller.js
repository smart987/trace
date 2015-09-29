'use strict';

angular.module('traceApp').controller('DriverDialogController',
    ['$scope', '$stateParams', '$modalInstance', 'entity', 'Driver', 'Bike',
        function($scope, $stateParams, $modalInstance, entity, Driver, Bike) {

        $scope.driver = entity;
        $scope.bikes = Bike.query();
        $scope.load = function(id) {
            Driver.get({id : id}, function(result) {
                $scope.driver = result;
            });
        };

        var onSaveFinished = function (result) {
            $scope.$emit('traceApp:driverUpdate', result);
            $modalInstance.close(result);
        };

        $scope.save = function () {
            if ($scope.driver.id != null) {
                Driver.update($scope.driver, onSaveFinished);
            } else {
                Driver.save($scope.driver, onSaveFinished);
            }
        };

        $scope.clear = function() {
            $modalInstance.dismiss('cancel');
        };
}]);
