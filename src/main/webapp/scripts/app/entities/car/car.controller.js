'use strict';

angular.module('traceApp')
    .controller('CarController', function ($scope, $rootScope, Car, ParseLinks) {
    	$scope.selectedOwnerId = $rootScope.selectedCarOwnerId;
        $scope.cars = [];
        $scope.page = 1;
        
        $scope.loadAll = function() {
            Car.query({page: $scope.page, per_page: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                $scope.cars = result;
            });
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.delete = function (id) {
            Car.get({id: id}, function(result) {
                $scope.car = result;
                $('#deleteCarConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Car.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteCarConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.car = {type: null, seats: null, id: null};
        };
    });
