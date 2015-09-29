/* globals $ */
'use strict';

angular.module('traceApp')
    .directive('traceAppPager', function() {
        return {
            templateUrl: 'scripts/components/form/pager.html'
        };
    });
