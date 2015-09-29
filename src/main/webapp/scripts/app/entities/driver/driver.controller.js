'use strict';

angular.module('traceApp')
    .controller('DriverController', function ($scope, Driver, ParseLinks) {
        $scope.drivers = [];
        $scope.page = 1;
        $scope.loadAll = function() {
            Driver.query({page: $scope.page, per_page: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                $scope.drivers = result;
            });
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.delete = function (id) {
            Driver.get({id: id}, function(result) {
                $scope.driver = result;
                $('#deleteDriverConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Driver.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteDriverConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.driver = {name: null, gender: null, id: null};
        };
    });
