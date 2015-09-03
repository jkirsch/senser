'use strict';

/**
 * Enables nice animations
 */
angular.module('stream')
    .directive('zodiac', function () {
        return {
            template: '<canvas id="zodiac"></canvas>',
            restrict: 'E',
            link: function postLink(scope, element) {

                // start the animation
                new Zodiac(element.children()[0],                      // HTMLCanvasElement or id
                    {                                      //// OPTIONS
                        directionX: 0,
                        directionY: -1,
                        velocityX: [.1, .3],
                        velocityY: [.3, .7],
                        bounceX: !0,
                        bounceY: !1,
                        parallax: .2,
                        pivot: 0,
                        density: 9999,
                        dotRadius: [1, 5],
                        linkColor: "#ffc545",
                        linkDistance: 55,
                        linkWidth: 2
                    });
            }
        };
    });
