'use strict';

angular.module('traceApp')
    .factory('Bike', function ($resource, DateUtils) {
        return $resource('api/bikes/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    });
