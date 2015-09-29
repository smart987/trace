'use strict';

angular.module('traceApp').controller('CarDialogController',
    ['$scope', '$stateParams', '$modalInstance', 'entity', 'Car', 'Owner',
        function($scope, $stateParams, $modalInstance, entity, Car, Owner) {

        $scope.car = entity;
        $scope.owners = Owner.query();
        $scope.load = function(id) {
            Car.get({id : id}, function(result) {
                $scope.car = result;
            });
        };

        var onSaveFinished = function (result) {
            $scope.$emit('traceApp:carUpdate', result);
            $modalInstance.close(result);
        };

        $scope.save = function () {
            if ($scope.car.id != null) {
                Car.update($scope.car, onSaveFinished);
            } else {
                Car.save($scope.car, onSaveFinished);
            }
        };

        $scope.clear = function() {
            $modalInstance.dismiss('cancel');
        };
}]);
