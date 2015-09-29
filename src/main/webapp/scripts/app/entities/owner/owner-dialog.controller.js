'use strict';

angular.module('traceApp').controller('OwnerDialogController',
    ['$scope', '$stateParams', '$modalInstance', 'entity', 'Owner', 'Car',
        function($scope, $stateParams, $modalInstance, entity, Owner, Car) {

        $scope.owner = entity;
        $scope.cars = Car.query();
        $scope.load = function(id) {
            Owner.get({id : id}, function(result) {
                $scope.owner = result;
            });
        };

        var onSaveFinished = function (result) {
            $scope.$emit('traceApp:ownerUpdate', result);
            $modalInstance.close(result);
        };

        $scope.save = function () {
            if ($scope.owner.id != null) {
                Owner.update($scope.owner, onSaveFinished);
            } else {
                Owner.save($scope.owner, onSaveFinished);
            }
        };

        $scope.clear = function() {
            $modalInstance.dismiss('cancel');
        };
}]);
