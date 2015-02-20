// create the controller and inject Angular's $scope
angular.module('zoneApp.controllers', []).controller("offerControl", function($scope, $http) {
		$http.get("/data/offers.json").success(function(data) {
			$scope.offers = data;
		});

	}).controller("placesControl",['$scope', 'GeolocationService', '$http',function($scope, geolocation, $http) {
		
		   $scope.position = null;
		    $scope.message = "Determining gelocation...";
		    geolocation().then(function (position) {
		        $scope.position = position;
		        $http.get("restGetPlaces?lon="+position.coords.longitude+"&lat="+position.coords.latitude).success(function(data) {
					$scope.places = data;
				});
		    }, function (reason) {
		        $scope.message = "Could not be determined."
		    });
		
		
		
		
		
		
		
	}]).controller("channelNavigationControl",function($scope, $http) {
		$http.get("/restGetChannels").success(function(data) {
				$scope.channelList = data;
		});
	});