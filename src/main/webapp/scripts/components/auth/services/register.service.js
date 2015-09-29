'use strict';

angular.module('traceApp')
    .factory('Register', function ($resource) {
        return $resource('api/register', {}, {
        });
    });


