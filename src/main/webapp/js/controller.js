angular.module('stream.controllers', ['stream.services', 'toaster'])


.controller('StreamController', function($scope, myService, toaster, $timeout, $log, $window) {

    'use strict';

    var socket;
    var stompClient;

    $scope.series = [];
    $scope.labels = [];
    $scope.data = [[]];
    $scope.options = {
        animation: false,
        showScale: true,
        showTooltips: false,
        pointDot: false,
        datasetStrokeWidth: 0.5
    };

    // load list of tracked terms
    myService.trackinfo().then(function(d) {
        $scope.trackinfo = d;
    });

    var initialConnect = function(frame) {
          toaster.info("Info","Now Reading Messages");
          $log.log('Connected ' + frame);

          stompClient.subscribe("/topic/stats", function(message) {
            var res = angular.fromJson(message.body);
              $log.log(res);

              var pos = $scope.series.indexOf(res.name);
              if(pos < 0) {
                  $scope.series.push(res.name);
                  pos = $scope.series.indexOf(res.name);
              }

              if(typeof $scope.data[pos] === 'undefined') {
                  $scope.data[pos] = [];
              }

              $scope.data[pos].push(res.count);
              if($scope.labels.length < $scope.data[pos].length) {
                  $scope.labels.push('');
              }

              if($scope.labels.length > 20) {
                  $scope.labels.shift();
                  var i;
                  for (i = 0; i < $scope.data.length; i++) {
                      $scope.data[i].shift();
                  }
              }

              $scope.$apply();

          });

    };

        var stompFailureCallback = function (error) {
            // set the handler to 0
            $log.log('STOMP: ' + error);
            $timeout(stompConnect, 10000);
            $log.log('STOMP: Reconnecting in 10 seconds');
        };

        function stompConnect() {
            $log.log('STOMP: Attempting connection');
            // recreate the stompClient to use a new WebSocket
            socket = new SockJS('/stream');
            stompClient = Stomp.over(socket);
            stompClient.connect({}, initialConnect, stompFailureCallback);
        }


    stompConnect();

});