
var myApp = angular.module('stream.services', [

])

.factory('myService', function($http) {
    'use strict';

    var myService = {
        trackedTerms: function() {
          // $http returns a promise, which has a then function, which also returns a promise
            // Return the promise to the controller
          return $http.get('controller/trackedTerms').then(function (response) {
              // The then function here is an opportunity to modify the response
              // We select the JSON parsed response data
              // The return value gets picked up in the controller.
              return response.data;
          }, function (data, status, headers, config) {
              // error handling
              var text;
              if (data.data) {
                  text = data.data.message;
              }
              throw new Error(text || "Can't talk to server - server down?");
          });
        }
      };
      return myService;
});