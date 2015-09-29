/* globals $ */
'use strict';

angular.module('traceApp')
    .directive('traceAppPagination', function() {
        return {
            templateUrl: 'scripts/components/form/pagination.html'
        };
    });
