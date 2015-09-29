'use strict';

angular.module('traceApp')
    .controller('OwnerController', function ($scope, $state, $rootScope, Owner, ParseLinks) {
    	$rootScope.carsForSelectedOwnerFlag = false;
    	$rootScope.selectedCarOwnerId = -1;    	
        $scope.owners = [];
        $scope.page = 1;
        $scope.loadAll = function() {
            Owner.query({page: $scope.page, per_page: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                $scope.owners = result;
            });
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.delete = function (id) {
            Owner.get({id: id}, function(result) {
                $scope.owner = result;
                $('#deleteOwnerConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Owner.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteOwnerConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.owner = {name: null, age: null, id: null};
        };
        
        $scope.getCarsByOwnerId = function(ownerId){
        	$rootScope.carsForSelectedOwnerFlag = true;
        	$rootScope.selectedCarOwnerId = ownerId;
        	
        	$state.go("car");
        }
        
    });
