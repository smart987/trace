'use strict';

angular.module('traceApp')
    .controller('LogoutController', function (Auth) {
        Auth.logout();
    });
