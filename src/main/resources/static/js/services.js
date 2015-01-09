'use strict';

/* Services */

// Demonstrate how to register services
// In this case it is a simple value service.
angular.module('myApp.services', []).
value('version', '0.1').
factory('userRepo', ['$http', function($http) {
    return new userRepositoryMock($http); // for example data stored in /data/user.json
    //return new userRepository($http); (call your real user data API here)
}]).
factory('inboxRepo', ['$http', function($http) {
    return new inboxRepositoryMock($http);
    //return new inboxRepository($http); (call your real inbox data API here)
}]).
factory('resourceRepo', ['$http', function($http) {
    return new resourceRepositoryMock($http);
    //return new resourceRepository($http); (call your real inbox data API here)
}]).
factory('networkRepo', ['$http', function($http) {
    return new networkRepositoryMock($http);
}]).
factory('eventsRepo', ['$http', function($http) {
    return new eventsRepositoryMock($http);
}]).
factory('todoStorage', function () {
    var STORAGE_ID = 'todos-angularjs-perf';
    
    return {
        get: function () {
            return JSON.parse(localStorage.getItem(STORAGE_ID) || '[{"title":"Something that needs to get done.","completed":false}]');
        },
        put: function (todos) {
            localStorage.setItem(STORAGE_ID, JSON.stringify(todos));
        }
    };
}).
factory('uuid', function() {
    var svc = {
        new: function() {
            function _p8(s) {
                var p = (Math.random().toString(16)+"000000000").substr(2,8);
                return s ? "-" + p.substr(0,4) + "-" + p.substr(4,4) : p ;
            }
            return _p8() + _p8(true) + _p8(true) + _p8();
        },
         
        empty: function() {
          return '00000000-0000-0000-0000-000000000000';
        }
    };
    return svc;
}).
service('asyncScript', ['$window', function($window) {
    
    /* on-demand async script loader using jQuery getScript */
    
    // central config for dependencies - manage libs and versions here..
    var libs = {
        
        flot:        '//cdnjs.cloudflare.com/ajax/libs/flot/0.8.2/jquery.flot.min.js',
        flot_resize: 'http://www.flotcharts.org/flot/jquery.flot.resize.js',
        flot_pie:    '//cdnjs.cloudflare.com/ajax/libs/flot/0.8.2/jquery.flot.pie.min.js',
        raphael:     '//cdnjs.cloudflare.com/ajax/libs/raphael/2.1.0/raphael-min.js',
        morris:      '//cdnjs.cloudflare.com/ajax/libs/morris.js/0.5.0/morris.min.js',
        hotkeys:     'http://mindmup.github.io/bootstrap-wysiwyg/external/jquery.hotkeys.js',
        wysiwyg:     'http://mindmup.github.io/bootstrap-wysiwyg/bootstrap-wysiwyg.js',
        datepicker:  '//cdnjs.cloudflare.com/ajax/libs/bootstrap-datepicker/1.3.0/js/bootstrap-datepicker.min.js',
        moment:      '//cdnjs.cloudflare.com/ajax/libs/moment.js/2.6.0/moment.min.js',
        bootstrapdatetimepicker:'//cdnjs.cloudflare.com/ajax/libs/bootstrap-datetimepicker/3.0.0/js/bootstrap-datetimepicker.min.js',
        jasny:       '//cdnjs.cloudflare.com/ajax/libs/jasny-bootstrap/3.1.3/js/jasny-bootstrap.min.js',
        jvectormap:  '/lib/jquery-jvectormap-1.2.2.min.js',
        jvectormap_en:'http://jvectormap.com/js/jquery-jvectormap-world-mill-en.js',
        dropzone:    '//cdnjs.cloudflare.com/ajax/libs/dropzone/3.8.4/dropzone.min.js',
        minicolors:  '//cdn.jsdelivr.net/jquery.minicolors/2.1.2/jquery.minicolors.js',
        starr:       '/lib/starrr.js',
        selectpicker:'//cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.5.4/bootstrap-select.min.js',
        fullcalandar:'//cdnjs.cloudflare.com/ajax/libs/fullcalendar/1.6.4/fullcalendar.min.js',
        gcal:        '//cdnjs.cloudflare.com/ajax/libs/fullcalendar/1.6.4/gcal.js',
        angular_calendar:'//cdnjs.cloudflare.com/ajax/libs/angular-ui-calendar/0.8.0/calendar.min.js'
        
    };
    
    return {

        load: function(name,cb) {
            
            /* async load if not already attached to $window */
            if (!$window["loader_"+name]) {
                // load it
                $.getScript(libs[name],function(){
                    $window["loader_"+name] = true;
                    cb();
                });
            }
            else {
                // script is already available to window - do callback
                cb();
            }
        }
    }

}]).
service('googleMapsSvc', ['$window', function($window) {
    
    var gAddress, gMapElement, gOptions, gUseSv;

    function initialize(address, mapElement, options, useStreetView) {

        var google = $window.google;
        var geocoder = new $window.google.maps.Geocoder();
        var map, center, mapOptions = {
            zoom: 13,
            mapTypeId: google.maps.MapTypeId.ROADMAP
        };
        $.extend(mapOptions, options); // merge the defaults with options passed in
        
        map = new google.maps.Map(document.getElementById(mapElement),mapOptions);
        
        if (geocoder) {
          geocoder.geocode({'address': address}, function(results, status) {
            if (status == google.maps.GeocoderStatus.OK) {
              
                /* find and set the center */
                center = results[0].geometry.location;
                map.setCenter(center);
                
                /* wait for the map to be ready */
                google.maps.event.addListener(map, 'idle', function()
                {
                    if (useStreetView && center.lat()) {
                        var latlng = new google.maps.LatLng(center.lat(), center.lng());
                        var mapOptions = {
                             position: latlng,
                             pov: {
                              heading: 165,
                              pitch: 0
                            },
                            zoom : 1
                        };
                        map = new google.maps.StreetViewPanorama(document.getElementById(mapElement),mapOptions);
                        map.setVisible(true);
                    }
                });
                
                /* uncomment this to display an InfoWindow
                new google.maps.InfoWindow(
                    {
                      content: address,
                      map: map,
                      position: results[0].geometry.location,
                    });
                */
                
                /* display a marker */
                new google.maps.Marker({
                    position: results[0].geometry.location,
                    map: map, 
                    title:address
                });
            }
          });
        } // if geocoder
    }
    
    /* init map must be attached to the $window so that google callback can access it */
    $window.initializeMap = function () {
        initialize(gAddress, gMapElement, gOptions, gUseSv);
    }
    
    return {
        init: function(address, mapElement, options, useSv) {
            
            gAddress = address;
            gMapElement = mapElement;
            gOptions = options;
            gUseSv = useSv||false;
            
            /* load google maps if not already attached to $window */
            $window.initializeMap();
           
        }
    } // return
}]).
service('noty', ['$rootScope', function($rootScope) {
    var queue = $rootScope.queue||[];
     
    return {
        queue: queue,
        add: function( item ) {
            //setTimeout(function(){
                queue.push(item);
                setTimeout(function(){
                    // remove the alert after 5 sec using jq
                    var ele = $('.alerts .alert').eq(0);
                    ele.fadeOut(3000,function(){
                        ele.remove();
                    });
                    queue.shift();
                    $rootScope.$apply();
                },5000);
            //},150);
        },
        pop: function(){
            return queue.pop();       
        }
    };
}]);

/* data repositories -------------------------------- */
window.userRepository = function($http) {
  this.$http = $http;
  
  //TODO: implement real data API call here
  this.fetchUsers = function() {
   var url = '/api/users/list';
   // invoke real service
   return this.$http.get(url)
    .success(function(data) {
      return data;
    });
  };
};
window.userRepositoryMock = function($http) {
  this.$http = $http;
  this.fetchUsers = function() {
    // just get data from a flat JSON file
    return $http.get('data/user.json');
  };
};

window.inboxRepository = function($http) {
  this.$http = $http;
  //TODO: implement real data API call here
};
window.inboxRepositoryMock = function($http) {
  this.$http = $http;
  this.getList = function() {
    return $http.get('data/inbox.json');
  };
  this.getOne = function(idx) {
    $http.get('data/inbox.json').success(function(data) {
        return data[idx]||{};
    }).error(function(data, status) {
        alert('inbox data error!');
    });
  };
};

window.resourceRepository = function($http) {
  this.$http = $http;
  //TODO: implement real data API call here
};
window.resourceRepositoryMock = function($http) {
  this.$http = $http;
  this.repo = $http.get('data/resource.json');
  this.getList = function() {
    return $http.get('data/resource.json');
  };
  this.getOne = function(idx) {
    $http.get('data/resource.json').success(function(data) {
        return data[idx]||{};
    }).error(function(data, status) {
        alert('resource data error!');
    });
  };
}

window.networkRepositoryMock = function($http) {
  this.$http = $http;
  this.repo = $http.get('data/network.json');
  this.getList = function() {
    return $http.get('data/network.json');
  };
  this.getOne = function(idx) {
    $http.get('data/network.json').success(function(data) {
        return data[idx]||{};
    }).error(function(data, status) {
        alert('network data error!');
    });
  };
}

window.eventsRepositoryMock = function($http) {
  this.$http = $http;
  this.repo = $http.get('data/events.json');
  this.getList = function() {
    return $http.get('data/events.json');
  };
  this.getOne = function(idx) {
    $http.get('data/events.json').success(function(data) {
        return data[idx]||{};
    }).error(function(data, status) {
        alert('events data error!');
    });
  };
}
/* end data repositories -------------------------------- */
