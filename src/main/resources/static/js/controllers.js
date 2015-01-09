// create the controller and inject Angular's $scope
angular.module('myApp.controllers', [])
.controller('appCtrl', ['$scope', '$window', '$location', '$sce', 'userRepo', 'noty', function($scope, $window, $location, $sce, userRepo, noty){
    $scope.noty = noty;                             // notify service
    
    $scope.date = Date.now();                       // make date accessible to all pages
    
    $scope.reload = function(){                     // force reload
        $window.location.reload(); 
    }
    
    $scope.go = function (path) {                 // redir to a route
        $location.path(path);
    };
    
    $scope.renderHtml = function(html)
    {
        return $sce.trustAsHtml(html);
    };
    
    $scope.isActive = function(path) {           //  monitor path to set active class in menu
      if ($location.path() == path) {
        return true;
      } else {
        return false;
      }
    };
    
    userRepo.fetchUsers().success(function(data) {
        $scope.users = data;
    });
    
    //attach utils to global app scope
    $scope.timeAgo = utils.timeAgo;
    
    // show once
    $window.setTimeout(function(){
        $scope.$apply(function(){
            $scope.noty.add({title:'Welcome Back',body:'You have 3 new messages.'});
        });
    },4000);
    
}])
.controller('widgetsCtrl', ['$scope', function($scope) {

}])
.controller('contactCtrl', ['$scope', 'googleMapsSvc', function($scope, googleMapsSvc) {
    /* the #map-input on the contact page
     * contains the address to center and mark on the map
    **/
    googleMapsSvc.init($('#map-input').text(),'mapGoogle');
}])
.controller('chartsCtrl', ['$scope', 'asyncScript', function($scope, asyncScript) {
    
    asyncScript.load('flot',function(){
    asyncScript.load('flot_resize',function(){
    asyncScript.load('flot_pie',function(){
       
        // styles for all flot charts
        var gridStyle = {borderColor:'rgba(170,170,170,0.4)',borderWidth:1,hoverable:true,clickable:true};
        
        // static charts
        var d1 = [];
        for (var i = 0; i < 14; i += 0.2) {
         d1.push([i, Math.sin(i)]);
        } 
        var d2 = [[0, 3], [4, 8], [8, 5], [9, 13]];
        var d3 = [[0, 12], [7, 12], [12, 13]];
        
        // line
        $.plot("#chart2",[d1],{yaxis:{show:false},xaxis:{font:'#ddd'},grid:gridStyle,series:{color:'#eea236',lines:{show:true}}});
        $.plot("#chart3",[d2,d3],{yaxis:{show:false},xaxis:{font:'#ddd'},grid:gridStyle,series:{color:'#d9534f',lines:{show:true},points:{show:true}}});
        $.plot("#chart4",[d3],{yaxis:{show:false},xaxis:{font:'#ddd'},grid:gridStyle,series:{color:'#428bca'}});
        $.plot("#chart5",[d3,d2],{yaxis:{show:false},xaxis:{font:'#ddd'},grid:gridStyle,series:{color:'#5cb85c',lines:{show:true},points:{show:true}}});
        
        // pie
        $.plot("#donut1",[{data:70,color:'#5bc0de'},{data:20,color:'#ddd'}],{series:{pie:{show:true,innerRadius:0.7}},grid:{hoverable:true}});
        $.plot("#donut2",[{data:20,color:'#5bc0de'},{data:55,color:'#ddd'}],{series:{pie:{show:true,innerRadius:0.7}},grid:{hoverable:true}});
        $.plot("#donut3",[{data:4,color:'#5bc0de'},{data:70,color:'#ddd'}],{series:{pie:{show:true,innerRadius:0.7}},grid:{hoverable:true}});
        $.plot("#donut4",[{data:30,color:'#5bc0de'},{data:70,color:'#ddd'}],{series:{pie:{show:true,innerRadius:0.7}},grid:{hoverable:true}});
        
        // mini bar
        var b1 = [[0, 3], [1, 5], [2, 2], [3, 9], [4, 10], [5, 8], [6, 6], [7, 4], [8, 2]];
        var b2 = [[0, 3], [1, 8], [2, 5], [3, 13], [4, 10], [5, 3], [6, 5], [7, 8], [8, 2]];
        var b3 = [[0, 1], [1, 4], [2, 4], [3, 6], [4, 7], [5, 3], [6, 5], [7, 11], [8, 9]];
        
        $.plot("#bar1",[b1],{
                series:{color:'#ffffff',bars:{show:true}},
                bars:{lineWidth:3,fillColor:'#d9534f'},
                grid:{show:false,hoverable:true}});
        $.plot("#bar2",[b2],{
                series:{color:'#ffffff',bars:{show:true}},
                bars:{lineWidth:3,fillColor:'#428bca'},
                grid:{show:false,hoverable:true}});
        $.plot("#bar3",[b3],{
                series:{color:'#ffffff',bars:{show:true}},
                bars:{lineWidth:3,fillColor:'#5cb85c'},
                grid:{show:false,hoverable:true}});
        
        // mini pies
        $.plot("#p1",[{data:12,color:'#428bca'},{data:78,color:'#d9534f'},{data:80,color:'#f0ad4e'}],{series:{pie:{show:true}},grid:{hoverable:true}});
        $.plot("#p2",[{data:32,color:'#428bca'},{data:18,color:'#d9534f'},{data:55,color:'#f0ad4e'}],{series:{pie:{show:true}},grid:{hoverable:true}});
        $.plot("#p3",[{data:19,color:'#428bca'},{data:54,color:'#d9534f'},{data:25,color:'#f0ad4e'}],{series:{pie:{show:true}},grid:{hoverable:true}});
      
      
        // real-time chart
        // usually data would be fetched from a server
        var data = [], totalPoints = 200;
        function getRandomData() {
        
            if (data.length > 0)
              data = data.slice(1);
            
            // do a random walk
            while (data.length < totalPoints) {
              var prev = data.length > 0 ? data[data.length - 1] : 50;
              var y = prev + Math.random() * 10-5;
              if (y < 0)
                y = 0;
              if (y > 100)
                y = 100;
              data.push(Math.round(y*100)/100);
            }
          
            // zip the generated y values with the x values
            var res = [];
            for (var i = 0; i < data.length; ++i){
              res.push([i, data[i]]);
            }
            return res;
        } //getRandomData
      
        // setup control widget
        var updateInterval = 300;
        $("#updateInterval").val(updateInterval).change(function () {
            var $this = $(this);
            var v = $this.val();
            if (v && !isNaN(+v)) {
                updateInterval = +v;
                if (updateInterval < 1)
                  updateInterval = 1;
                if (updateInterval > 2000)
                  updateInterval = 2000;
                $this.val("" + updateInterval);
            }
        });
        
        // realtime plot
        var options = {
            grid:gridStyle,
            series:{shadowSize:0,lines:{show:true,fill:true,fillColor:'rgba(92,184,92,.5)'},color:'#5cb85c'},       
            yaxis:{min:0,max:100,font:'#ddd'},
            xaxis:{show:true,font:'#ddd'}
        };
        
        var plot = $.plot($("#chart1"), [ getRandomData() ], options);
        var realTime;
        function update() {
            plot.setData([ getRandomData() ]);
            plot.draw();
            realTime = setTimeout(update, updateInterval);
        }

        setTimeout(function(){update()}
        ,1000);
      
        $("#chart1").bind("plothover", function (event, pos, item) {
            $("#tooltip").remove();
            if (item) {
                var tooltip = item.series.data[item.dataIndex][1];
                $('<a href="#" class="tooltip" id="tooltip">' + tooltip + '</a>')
                .css({
                  top: item.pageY + 5,
                  left: item.pageX + 5
                })
                .appendTo("body").fadeIn(200);
            }
        });
      
        $('.realtime').click(function() {
            var activeBtn = $(this).find(".active");
            
            if (activeBtn.text()==="live"){
              update();
            }
            else {
              clearTimeout(realTime);
            }
        });//toggle click
        
    }); //script
    }); //script
    }); //script flot
    
}])
.controller('morrisChartsCtrl', ['$scope', 'asyncScript', function($scope, asyncScript) {
    
    asyncScript.load('raphael',function(){
    asyncScript.load('morris',function(){

      Morris.Area({
        element: 'area-example',
        data: [
          { y: '1.1.', a: 100, b: 90 },
          { y: '2.1.', a: 75,  b: 65 },
          { y: '3.1.', a: 50,  b: 40 },
          { y: '4.1.', a: 75,  b: 65 },
          { y: '5.1.', a: 50,  b: 40 },
          { y: '6.1.', a: 75,  b: 65 },
          { y: '7.1.', a: 100, b: 90 }
        ],
        xkey: 'y',
        ykeys: ['a', 'b'],
        labels: ['Series A', 'Series B']
      });
      
      Morris.Line({
        element: 'line-example',
        data: [
          {year: '2010', value: 20},
          {year: '2011', value: 10},
          {year: '2012', value: 5},
          {year: '2013', value: 2},
          {year: '2014', value: 20}
        ],
        xkey: 'year',
        ykeys: ['value'],
        labels: ['Value']
      });
      
      Morris.Donut({
        element: 'donut-example',
        data: [
         {label: "Android", value: 12},
         {label: "iPhone", value: 30},
         {label: "Other", value: 20}
        ]
      });
      
      Morris.Bar({
         element: 'bar-example',
         data: [
            {y: 'Jan 2014', a: 100, b: 90},
            {y: 'Feb 2014', a: 75,  b: 65},
            {y: 'Mar 2014', a: 50,  b: 40},
            {y: 'Apr 2014', a: 75,  b: 65},
            {y: 'May 2014', a: 50,  b: 40},
            {y: 'Jun 2014', a: 75,  b: 65}
         ],
         xkey: 'y',
         ykeys: ['a', 'b'],
         labels: ['Visitors', 'Conversions']
      });
      
    }); //script
    }); //script
}])
.controller('wysiwygCtrl', ['$scope', 'asyncScript', function($scope, asyncScript) {
    
    asyncScript.load('hotkeys',function(){
    asyncScript.load('wysiwyg',function(){

        $('#editor').wysiwyg();
        $('#editor').cleanHtml();
    
    }); //script
    }); //script
    
}])
.controller('formsCtrl', ['$scope', 'asyncScript', function($scope, asyncScript) {
    
    var tagsData = [
        {value:1,option:'Apple'},
        {value:2,option:'Banana'},
        {value:3,option:'Cherry'},
        {value:4,option:'Cantelope'},
        {value:5,option:'Grapefruit'},
        {value:6,option:'Grapes'},
        {value:7,option:'Lemon'},
        {value:8,option:'Lime'},
        {value:9,option:'Melon'},
        {value:10,option:'Orange'},
        {value:11,option:'Strawberry'},
        {value:12,option:'Watermelon'}
    ];
    
    
    /* set data for selectpicker and tagpicker */
    $scope.items = tagsData;
    $scope.selected = "8";            // for single selects
    $scope.selectedList = [4,5,8];    // for multiple selects, tagpicker
    
    
    /* live character count ----------------------------------
     * - input should have class 'counter'
     * - count display should have class 'counter-message'
     * - both 'counter' and 'counter-message' should be in 
     *   same parent container
    **/
    var text_max = 200;
    $('.counter-message').html(text_max);
    $('.counter').keyup(function() {
      var text_length = $('.counter').val().length;
      var text_remaining = text_max - text_length;
      $(this).parent().find('.counter-message').html(text_remaining);
    });
    /* live character count ---------------------------------*/
    
    
    /* load dependencies on demand if not alread loaded */
    asyncScript.load('datepicker',function(){
    asyncScript.load('moment',function(){
    asyncScript.load('bootstrapdatetimepicker',function(){
    asyncScript.load('jasny',function(){
        
        /* datepicker init */
        $('#datepicker').datepicker({
            autoclose:true,
        }).on("changeDate", function(e){
            console.log(e.date); // handle your date change event here
        });
        
        $('.input-daterange').datepicker({
            autoclose:true
        }).on("changeDate", function(e){
            console.log(e.date); // handle your date change event here
        });
        
        
        /* datetimepicker init */
        $('#timepicker').datetimepicker({
            pickDate: false
        });
        
        
        /* monitor keystrokes in inputs to trigger validation changes */
        $("input").bind('keyup change',function() {
            var $t = $(this);
            var $par = $t.parent();
            var match = $t.attr("data-valid-match");
            
            if (typeof match!="undefined"){
                if ($t.val()!=$('#'+match).val()) {
                    $par.removeClass('has-success').addClass('has-error');
                }
                else {
                    $par.removeClass('has-error').addClass('has-success');
                }
            }
            else if (!this.checkValidity()) { //html5 required
                $par.removeClass('has-success').addClass('has-error');
            }
            else {
                $par.removeClass('has-error').addClass('has-success');
            }
            
            if ($par.hasClass("has-success")) {
                $par.find('.form-control-feedback').removeClass('fade');
            }
            else {
                $par.find('.form-control-feedback').addClass('fade');
            }
        });

    }); //script
    }); //script
    }); //script
    }); //script

}])
.controller('faqCtrl', ['$scope', function($scope) {
    
    // faq
    $scope.items = [
        {question:'Is the template easy to customize?',answer:'Yes. The template was built for customization. A standard release version of Bootstrap is included. Style overrides and customizations can be made in the `css/styles.css` file, or you can use LessJS and change the variables in `styles.less`.'},
        {question:'How is the AngularJS app structured?',answer:'The AngularJS app structure has been extended from the well known angular-seed project. There is an `app.js` file that contains the configuration and routing. The `app.js` also load the other `services`, `directives` and `controllers` modules which are defined respectively in the `services.js`, `directives.js` and `controllers.js` files'},  
        {question:'How do I get started?',answer:'Take a look at the included CSS and HTML files. You`ll find various well-commented examples. The `/pages` folder contains full page examples. The `/partials` folder contains standalone widgets that are designed for inclusion inside other pages.'},  
        {question:'How do I customize the style (look-and-feel)?',answer:'You can modify the included `styles.css` file with CSS markup. Since this file follows the `bootstrap.css` any styles you define here will override the Bootstrap CSS classes.'},  
        {question:'How do I customize the AngularJs app?',answer:'The app template include various AngularJS controllers, directives and services. In most cases you can re-use the existing services and directives, and customize the controllers accordingly. Several mock data repository examples are included as part of the `services.js`. You can replace these with your own "real" data source or API calls.'},  
        {question:'How are 3rd party libraries and dependencies included?',answer:'Third party microframeworks such as Bootstrap DatePicker, Flot Charts and Jasny are loaded on-demand using the "AsyncScript" service that is defined in the `services.js` file. You can modify the url reference and version of these libraries accordingly in this file. The Google Maps and FullCalendar libraries are not loaded dynamically and therefore must be included as SCRIPT references in the main `index.html` template file.'},  
        {question:'How does the AngularJS templating work?',answer:'The app template uses nested templates starting with `index.html` at the top(base) level. The `index.html` contains the overall HTML HEAD and BODY tags. The `index.html` BODY includes a `ui-view` placeholder tag where the `main.html` template is loaded. The `main.html` template includes the overall content layout for site components such as the header, menus and sidebar. Finally, individual `/pages` and `/partials` templates are loaded according to the `app.js` routing definition and directives.'},  
        {question:'How does the AngularJS routing work?',answer:'The app template utilizes the well known `ui-router` for defining app routes. The `app.js` file contains the route definitions in that are managed using Angular`s $stateProvider. '},  
    ];
    
}])
.controller('timelineCtrl', ['$scope', 'eventsRepo', function($scope, eventsRepo) {
    
    // init
    $scope.sortingOrder = 'dt';
    $scope.reverse = false;
    $scope.items = [];
    
    eventsRepo.repo.success(function(data) {
        $scope.items = data;
    }).error(function(data, status) {
        alert('data error!');
    });
    
    $scope.addEvent = function () {
        
    };
}])
.controller('fullcalendarCtrl', ['$scope', '$filter', function($scope, $filter) {
    
    var date = new Date();
    var d = date.getDate();
    var m = date.getMonth();
    var y = date.getFullYear();
    
    $scope.eventStart = $filter("date")(Date.now(), 'yyyy-MM-dd');
    $scope.eventSource = {
        className: 'gcal-event',          
        currentTimezone: 'America/Chicago' // set the default time zone here
    };
    
    /* event source that contains custom events on the scope */
    $scope.events = [
      {title: 'All Day Event',start: new Date(y, m, 1)},
      {title: 'Long Event',start: new Date(y, m, d - 5),end: new Date(y, m, d - 2)},
      {id: 999,title: 'Repeating Event',start: new Date(y, m, d - 3, 16, 0),allDay: false},
      {id: 999,title: 'Repeating Event',start: new Date(y, m, d + 4, 16, 0),allDay: false},
      {title: 'Birthday Party',start: new Date(y, m, d + 1, 19, 0),end: new Date(y, m, d + 1, 22, 30),allDay: false},
      {title: 'Click for Google',start: new Date(y, m, 28),end: new Date(y, m, 29),url: 'http://google.com/'}
    ];
    
    /* event source that calls a function on every view switch */
    $scope.eventsF = function (start, end, callback) {
      var s = new Date(start).getTime() / 1000;
      var e = new Date(end).getTime() / 1000;
      var m = new Date(start).getMonth();
      var events = [{title: 'Feed Me ' + m,start: s + (50000),end: s + (100000),allDay: false, className: ['customFeed']}];
      callback(events);
    };

    $scope.calEventsExt = {
       color: '#f00',
       textColor: 'yellow',
       events: [ 
          {type:'party',title: 'Lunch',start: new Date(y, m, d, 12, 0),end: new Date(y, m, d, 14, 0),allDay: false},
          {type:'party',title: 'Lunch 2',start: new Date(y, m, d, 12, 0),end: new Date(y, m, d, 14, 0),allDay: false},
          {type:'party',title: 'Click for Google',start: new Date(y, m, 28),end: new Date(y, m, 29),url: 'http://google.com/'}
        ]
    };
    /* alert on eventClick */
    $scope.alertOnEventClick = function( event, allDay, jsEvent, view ){
        $scope.alertMessage = (event.title + ' was clicked ');
    };
    /* alert on Drop */
     $scope.alertOnDrop = function(event, dayDelta, minuteDelta, allDay, revertFunc, jsEvent, ui, view){
       $scope.alertMessage = ('Event Droped to make dayDelta ' + dayDelta);
    };
    /* alert on Resize */
    $scope.alertOnResize = function(event, dayDelta, minuteDelta, revertFunc, jsEvent, ui, view ){
       $scope.alertMessage = ('Event Resized to make dayDelta ' + minuteDelta);
    };
    /* add and removes an event source of choice */
    $scope.addRemoveEventSource = function(sources,source) {
      var canAdd = 0;
      angular.forEach(sources,function(value, key){
        if(sources[key] === source){
          sources.splice(key,1);
          canAdd = 1;
        }
      });
      if(canAdd === 0){
        sources.push(source);
      }
    };

    $scope.addEvent = function() {
        var newEvent = {};
		
		newEvent.title = $scope.eventTitle.trim();
		newEvent.start = $scope.eventStart;
		newEvent.end = $scope.eventEnd;
		newEvent.className = $scope.eventClass;
		
        $scope.events.push(newEvent);
        
        // notify user
        $scope.noty.add({type:'success',title:'Success',body:'The event was added to the calendar.'});
    };

    $scope.remove = function(index) {
      $scope.events.splice(index,1);
    };
   
    $scope.changeView = function(view,calendar) {
      calendar.fullCalendar('changeView',view);
    };
   
    $scope.viewToday = function(calendar) {
      calendar.fullCalendar('changeView','agendaDay');
      calendar.fullCalendar('today');
    };
   
    $scope.renderCalender = function(calendar) {
      if(calendar){
        calendar.fullCalendar('render');
      }
    };
    
    $scope.eventRender = function( event, element, view ) { 
        element.attr('tooltip', event.title);
        $compile(element)($scope);
    };
    
    /* next */
    $scope.goNext = function(calendar) {
        if(calendar){
            calendar.fullCalendar('next');
        }
    };
    
    /* prev */
    $scope.goPrev = function(calendar) {
        if(calendar){
            calendar.fullCalendar('prev');
        }
    };
    
    /* config object */
    $scope.uiConfig = {
      calendar:{
        height: 480,
        width: '100%',
        editable: true,
        header:{
          left: '',
          center: 'title',
          right: ''
        },
        eventClick: $scope.alertOnEventClick,
        eventDrop: $scope.alertOnDrop,
        eventResize: $scope.alertOnResize
      }
    };
    
    /* event sources array*/
    $scope.eventSources = [$scope.events, $scope.eventSource, $scope.eventsF];
    
}])
.controller('googleMapsCtrl', ['$scope', 'googleMapsSvc', function($scope, googleMapsSvc) {
    
    googleMapsSvc.init('810 Park Ave, New York NY', 'mapGoogle', {
        zoom: 10
    }, false);
    
}])
.controller('googleMapsStreetViewCtrl', ['$scope', '$window', 'googleMapsSvc', function($scope, $window, googleMapsSvc) {

    googleMapsSvc.init('E 75th St, New York NY', 'mapGoogleStreetView', {
    }, true);
    
}])
.controller('listCtrl', function($scope, $rootScope) {
    // mock data -- get this from a real data service
    $scope.list = [
      {username:"Adelle",action:"moved Test Board 1 to Shopping Ideas",timeAgo:"2 minutes ago",category:"Topics","img":"img/people/women11.jpg"},
      {username:"Garbiel",action:"deleted My Board",timeAgo:"2 minutes ago",category:"Discussions","img":"img/people/women8.jpg"},
      {username:"Abby",action:"liked your post What Happened to the..",timeAgo:"5 minutes ago",category:"Discussions","img":"img/people/women19.jpg"},
      {username:"Abby",action:"joined ACME Project Team",timeAgo:"6 minutes ago",category:"Collaboration","img":"img/people/women19.jpg"},
      {username:"Joe",action:"deleted My Board",timeAgo:"10 minutes ago",category:"Topics","img":"img/people/men4.jpg"},
      {username:"Marcus",action:"moved New Post onto Ideas Board",timeAgo:"1 hour ago",category:"Topic","img":"img/people/men5.jpg"},
      {username:"Gary",action:"deleted ProjectBoard6",timeAgo:"1 hour ago",category:"Discussions","img":"img/people/men11.jpg"},
      {username:"Gary",action:"deleted My Board1",timeAgo:"2 hours ago",category:"Discussions","img":"img/people/men11.jpg"},
      {username:"Kensington",action:"deleted MyBoard3",timeAgo:"2 hours ago",category:"Discussions","img":"img/people/men18.jpg"},
      {username:"John",action:"deleted My Board1",timeAgo:"2 hours ago",category:"Discussions","img":"img/people/men22.jpg"},
      {username:"Skell",action:"deleted his post Look at Why this is..",timeAgo:"4 hours ago",category:"Discussions","img":"img/people/men13.jpg"},
    ];
    $scope.deleteItem = function(idx) {
        $scope.list.splice(idx,1);
    };
})
.controller('dualListCtrl', ['$scope', 'userRepo', function($scope, userRepo) {
    $scope.selectedA = [];
    $scope.selectedB = [];
    
    $scope.checkedA = false;
    $scope.checkedB = false;
    
    userRepo.fetchUsers().success(function(data) {
        $scope.users = data;
        $scope.listA = $scope.users.slice(0,4);
        $scope.listB = $scope.users.slice(5,9);
        $scope.items = $scope.users;
    });
    
    function arrayObjectIndexOf(myArray, searchTerm, property) {
      for(var i = 0, len = myArray.length; i < len; i++) {
          if (myArray[i][property] === searchTerm) return i;
      }
      return -1;
    }
    
    $scope.aToB = function() {
        for (var i in $scope.selectedA) {
          var moveId = arrayObjectIndexOf($scope.items, $scope.selectedA[i], "id"); 
          $scope.listB.push($scope.items[moveId]);
          var delId = arrayObjectIndexOf($scope.listA, $scope.selectedA[i], "id"); 
          $scope.listA.splice(delId,1);
        }
        reset();
    };
    
    $scope.bToA = function() {
        for (var i in $scope.selectedB) {
          var moveId = arrayObjectIndexOf($scope.items, $scope.selectedB[i], "id"); 
          $scope.listA.push($scope.items[moveId]);
          var delId = arrayObjectIndexOf($scope.listB, $scope.selectedB[i], "id"); 
          $scope.listB.splice(delId,1);
        }
        reset();
    };
    
    function reset(){
        $scope.selectedA=[];
        $scope.selectedB=[];
        $scope.toggle=0;
    };
    
    $scope.toggleA = function() {
        if ($scope.selectedA.length>0) {
          $scope.selectedA=[];
        }
        else {
          for (var i in $scope.listA) {
            $scope.selectedA.push($scope.listA[i].id);
          }
        }
    };
    
    $scope.toggleB = function() {
        if ($scope.selectedB.length>0) {
          $scope.selectedB=[];
        }
        else {
          for (var i in $scope.listB) {
            $scope.selectedB.push($scope.listB[i].id);
          }
        }
    };
    
    $scope.drop = function(dragEl, dropEl, direction) {
        var drag = angular.element(dragEl);
        var drop = angular.element(dropEl);
        var id = drag.attr("data-id");
        var el = document.getElementById(id);
        
        if(!angular.element(el).attr("checked")){
            angular.element(el).triggerHandler('click');
        }
        
        // noty is accessible from app ctrl
        $scope.noty.add({title:'Success',body:'The item was moved.'});
        
        direction();
        $scope.$apply();
    };
}]) // end dualListCtrl
.controller('todoCtrl', ['$scope', '$location', '$filter', 'todoStorage', function ($scope, $location, $filter, todoStorage) {
	var todos = $scope.todos = todoStorage.get();

	$scope.newTodo = '';
	$scope.remainingCount = $filter('filter')(todos, {completed: false}).length;
    $scope.completedCount = $filter('filter')(todos, {completed: true}).length;
	$scope.editedTodo = null;

	if ($location.path() === '') {
		$location.path('/');
	}

	$scope.location = $location;
	$scope.$watch('location.path()', function (path) {
		$scope.statusFilter = { '/active': {completed: false}, '/completed': {completed: true} }[path];
	});

	$scope.$watch('remainingCount == 0', function (val) {
		$scope.allChecked = val;
	});

	$scope.addTodo = function () {
		var newTodo = $scope.newTodo.trim();
      
		if (newTodo.length === 0) {
			return;
		}

		todos.push({
			title: newTodo,
			completed: false
		});
		todoStorage.put(todos);

		$scope.newTodo = '';
		$scope.remainingCount++;
	};

	$scope.editTodo = function (todo) {
		$scope.editedTodo = todo;
		// Clone the original todo to restore it on demand.
		$scope.originalTodo = angular.extend({}, todo);
	};

	$scope.doneEditing = function (todo) {
		$scope.editedTodo = null;
		todo.title = todo.title.trim();

		if (!todo.title) {
			$scope.removeTodo(todo);
		}

		todoStorage.put(todos);
	};

	$scope.revertEditing = function (todo) {
		todos[todos.indexOf(todo)] = $scope.originalTodo;
		$scope.doneEditing($scope.originalTodo);
	};

	$scope.removeTodo = function (todo) {
		$scope.remainingCount -= todo.completed ? 0 : 1;
		todos.splice(todos.indexOf(todo), 1);
		todoStorage.put(todos);
	};

	$scope.todoCompleted = function (todo) {
        $scope.remainingCount += todo.completed ? 0 : -1;
        $scope.completedCount += todo.completed ? 0 : 1;
		todoStorage.put(todos);
	};

	$scope.clearCompletedTodos = function () {
		$scope.todos = todos = todos.filter(function (val) {
			return !val.completed;
		});
        $scope.completedCount = 0;
		todoStorage.put(todos);
	};

	$scope.markAll = function (completed) {
		todos.forEach(function (todo) {
			todo.completed = !completed;
		});
		$scope.remainingCount = completed ? todos.length : 0;
		todoStorage.put(todos);
	};
}]) // end todoCtrl
.controller('usersCtrl', ['$scope', 'userRepo', '$filter', function ($scope, userRepo, $filter) {
 
    // init
    $scope.sortingOrder = 'firstName';
    $scope.pageSizes = [5,10,25,50];
    $scope.reverse = false;
    $scope.filteredItems = [];
    $scope.groupedItems = [];
    $scope.itemsPerPage = 5;
    $scope.pagedItems = [];
    $scope.currentPage = 0;
    
    userRepo.fetchUsers().success(function(data) {
        $scope.items = data;
        
        // init the filtered items
        $scope.search = function () {
            $scope.filteredItems = $filter('filter')($scope.items, function (item) {
              for(var attr in item) {
                if (searchMatch(item[attr], $scope.query))
                  return true;
              }
              return false;
            });
            $scope.currentPage = 0;
            // now group by pages
            $scope.groupToPages();
        };
        $scope.search();
        
    }).error(function(data, status) {
        //return data;
        alert('data error!');
    })

    var searchMatch = function (haystack, needle) {
        if (!needle) {
          return true;
        }
        return haystack.toLowerCase().indexOf(needle.toLowerCase()) !== -1;
    };
    
    // show items per page
    $scope.perPage = function () {
        $scope.groupToPages();
    };
    
    // calculate page in place
    $scope.groupToPages = function () {
        $scope.pagedItems = [];
        
        for (var i = 0; i < $scope.filteredItems.length; i++) {
          if (i % $scope.itemsPerPage === 0) {
            $scope.pagedItems[Math.floor(i / $scope.itemsPerPage)] = [ $scope.filteredItems[i] ];
          } else {
            $scope.pagedItems[Math.floor(i / $scope.itemsPerPage)].push($scope.filteredItems[i]);
          }
        }
    };
    
    $scope.range = function (start, end) {
        var ret = [];
        if (!end) {
          end = start;
          start = 0;
        }
        for (var i = start; i < end; i++) {
          ret.push(i);
        }
        return ret;
    };
    
    $scope.prevPage = function () {
        if ($scope.currentPage > 0) {
            $scope.currentPage--;
        }
        return false;
    };
    
    $scope.nextPage = function () {
        if ($scope.currentPage < $scope.pagedItems.length - 1) {
            $scope.currentPage++;
        }
        return false;
    };
    
    $scope.setPage = function () {
        $scope.currentPage = this.n;
    };
    
    $scope.approveUser = function (id) {
        //$scope.items.approved = true;
        for(var item in $scope.items) {
            if (item["id"]==id){
              item.approved = true;
            }
        }
    };
    
}])// end usersCtrl
.controller('resourceCtrl', ['$scope', 'resourceRepo', '$filter', function ($scope, resourceRepo, $filter) {
 
    $scope.tag = "";
    
    // init
    $scope.sortingOrder = 'id';
    $scope.pageSizes = [5,10,25,50];
    $scope.reverse = false;
    $scope.filteredItems = [];
    $scope.groupedItems = [];
    $scope.itemsPerPage = 5;
    $scope.pagedItems = [];
    $scope.currentPage = 0;
    
    // get data and init the filtered items
    $scope.init = function () {
        resourceRepo.getList().success(function(data) {
            $scope.items = data;
            $scope.search();
        }).error(function(data, status) {
            //return data;
            alert('data error!');
        });
    }
    
    // init the filtered items
    $scope.search = function () {
        $scope.filteredItems = $filter('filter')($scope.items, function (item) {
          for(var attr in item) {
            if (searchMatch(item[attr], $scope.query))
              return true;
          }
          return false;
        });
        $scope.currentPage = 0;
        // now group by pages
        $scope.groupToPages();
    };
        
    var searchMatch = function (haystack, needle) {
        if (!needle) {
          return true;
        }
        return haystack.toLowerCase().indexOf(needle.toLowerCase()) !== -1;
    };
    
    // show items per page
    $scope.perPage = function () {
        $scope.groupToPages();
    };
    
    // calculate page in place
    $scope.groupToPages = function () {
        $scope.pagedItems = [];
        
        for (var i = 0; i < $scope.filteredItems.length; i++) {
          if (i % $scope.itemsPerPage === 0) {
            $scope.pagedItems[Math.floor(i / $scope.itemsPerPage)] = [ $scope.filteredItems[i] ];
          } else {
            $scope.pagedItems[Math.floor(i / $scope.itemsPerPage)].push($scope.filteredItems[i]);
          }
        }
    };
    
    $scope.range = function (start, end) {
        var ret = [];
        if (!end) {
          end = start;
          start = 0;
        }
        for (var i = start; i < end; i++) {
          ret.push(i);
        }
        return ret;
    };
    
    $scope.prevPage = function () {
        if ($scope.currentPage > 0) {
            $scope.currentPage--;
        }
        return false;
    };
    
    $scope.nextPage = function () {
        if ($scope.currentPage < $scope.pagedItems.length - 1) {
            $scope.currentPage++;
        }
        return false;
    };
    
    $scope.setPage = function () {
        $scope.currentPage = this.n;
    };
 
    // initialize..
    $scope.init();
    
}])// end resourceCtrl
.controller('resourceDetailCtrl', ['$scope', 'resourceRepo', '$stateParams', function ($scope, resourceRepo, $stateParams) {

    $scope.id = parseInt($stateParams.id);
    resourceRepo.repo.success(function(data) {
        $scope.item = data[$scope.id]||{};
    }).error(function(data, status) {
        alert('resource data error!');
    });
    
}])// end resourceDetailCtrl
.controller('networkCtrl', ['$scope', 'networkRepo', '$filter', function ($scope, networkRepo, $filter) {
    
    // init
    $scope.sortingOrder = 'name';
    $scope.pageSizes = [5,10,25,50];
    $scope.reverse = false;
    $scope.filteredItems = [];
    $scope.groupedItems = [];
    $scope.itemsPerPage = 5;
    $scope.pagedItems = [];
    $scope.currentPage = 0;
    
    // get data and init the filtered items
    $scope.init = function () {
        networkRepo.getList().success(function(data) {
            $scope.items = data;
            $scope.search();
        }).error(function(data, status) {
            //return data;
            alert('data error!');
        });
    }
    
    // init the filtered items
    $scope.search = function () {
        $scope.filteredItems = $filter('filter')($scope.items, function (item) {
          for(var attr in item) {
            if (searchMatch(item[attr], $scope.query))
              return true;
          }
          return false;
        });
        $scope.currentPage = 0;
        // now group by pages
        $scope.groupToPages();
    };
        
    var searchMatch = function (haystack, needle) {
        if (!needle) {
          return true;
        }
        return haystack.toLowerCase().indexOf(needle.toLowerCase()) !== -1;
    };
    
    // show items per page
    $scope.perPage = function () {
        $scope.groupToPages();
    };
    
    // calculate page in place
    $scope.groupToPages = function () {
        $scope.pagedItems = [];
        
        for (var i = 0; i < $scope.filteredItems.length; i++) {
          if (i % $scope.itemsPerPage === 0) {
            $scope.pagedItems[Math.floor(i / $scope.itemsPerPage)] = [ $scope.filteredItems[i] ];
          } else {
            $scope.pagedItems[Math.floor(i / $scope.itemsPerPage)].push($scope.filteredItems[i]);
          }
        }
    };
    
    $scope.range = function (start, end) {
        var ret = [];
        if (!end) {
          end = start;
          start = 0;
        }
        for (var i = start; i < end; i++) {
          ret.push(i);
        }
        return ret;
    };
    
    $scope.prevPage = function () {
        if ($scope.currentPage > 0) {
            $scope.currentPage--;
        }
        return false;
    };
    
    $scope.nextPage = function () {
        if ($scope.currentPage < $scope.pagedItems.length - 1) {
            $scope.currentPage++;
        }
        return false;
    };
    
    $scope.setPage = function () {
        $scope.currentPage = this.n;
    };
 
    // change sorting order
    $scope.sortBy = function(newSortingOrder) {
        if ($scope.sortingOrder == newSortingOrder)
            $scope.reverse = !$scope.reverse;

        $scope.sortingOrder = newSortingOrder;
    };
 
    // initialize..
    $scope.init();
    
}])// end networkCtrl
.controller('inboxCtrl', ['$scope', 'inboxRepo', '$filter', '$sce', function ($scope, inboxRepo, $filter, $sce) {
 
    $scope.sortingOrder = 'id';
    $scope.pageSizes = [10,20,50,100];
    $scope.reverse = false;
    $scope.filteredItems = [];
    $scope.groupedItems = [];
    $scope.itemsPerPage = 10;
    $scope.pagedItems = [];
    $scope.currentPage = 0;
    
    /* inbox functions -------------------------------------- */
    
    // get data and init the filtered items
    $scope.init = function () {
        inboxRepo.getList().success(function(data) {
            $scope.items = data;
            $scope.search();
        }).error(function(data, status) {
            //return data;
            alert('data error!');
        });
    }

    var searchMatch = function (haystack, needle) {
        if (!needle) {
          return true;
        }
        return haystack.toLowerCase().indexOf(needle.toLowerCase()) !== -1;
    };
    
    // filter the items
    $scope.search = function () {
        $scope.filteredItems = $filter('filter')($scope.items, function (item) {
          for(var attr in item) {
            if (searchMatch(item[attr], $scope.query))
              return true;
          }
          return false;
        });
        $scope.currentPage = 0;
        // now group by pages
        $scope.groupToPages();
    };
    
    // calculate page in place
    $scope.groupToPages = function () {
        $scope.selected = null;
        $scope.pagedItems = [];
        
        for (var i = 0; i < $scope.filteredItems.length; i++) {
          if (i % $scope.itemsPerPage === 0) {
            $scope.pagedItems[Math.floor(i / $scope.itemsPerPage)] = [ $scope.filteredItems[i] ];
          } else {
            $scope.pagedItems[Math.floor(i / $scope.itemsPerPage)].push($scope.filteredItems[i]);
          }
        }
    };
    
    $scope.range = function (start, end) {
        var ret = [];
        if (!end) {
          end = start;
          start = 0;
        }
        for (var i = start; i < end; i++) {
          ret.push(i);
        }
        return ret;
    };
    
    $scope.prevPage = function () {
        if ($scope.currentPage > 0) {
            $scope.currentPage--;
        }
        return false;
    };
    
    $scope.nextPage = function () {
        if ($scope.currentPage < $scope.pagedItems.length - 1) {
            $scope.currentPage++;
        }
        return false;
    };
    
    $scope.setPage = function () {
        $scope.currentPage = this.n;
    };
    
    $scope.deleteItem = function (idx) {
        var itemToDelete = $scope.pagedItems[$scope.currentPage][idx];
        var idxInItems = $scope.items.indexOf(itemToDelete);
        $scope.items.splice(idxInItems,1);
        $scope.search();
        
        return false;
    };
    
    $scope.isMessageSelected = function () {
        if (typeof $scope.selected!=="undefined" && $scope.selected!==null) {
            return true;
        }
        else {
            return false;
        }
    };
    
    $scope.readMessage = function (idx) {
        $scope.items[idx].read = true;
        $scope.selected = $scope.items[idx];
    };
    
    $scope.readAll = function () {
        for (var i in $scope.items) {
            $scope.items[i].read = true;
        }
    };
    
    $scope.closeMessage = function () {
        $scope.selected = null;
    };
    
    $scope.renderMessageBody = function(html)
    {
        return $sce.trustAsHtml(html);
    };
    
    /* end inbox functions ---------------------------------- */
    
    // hookup refresh plugin
    $('#refreshInbox').refreshMe({
        selector:'#inboxPanel',
        completed:function(ele){$scope.init();}
    });
    
    // initialize..
    $scope.init();
    
}])// end inboxCtrl
.controller('messageCtrl', ['$scope', 'inboxRepo', function ($scope, inboxRepo) {
    
    $scope.message = function(idx) {
        return inboxRepo.getOne(idx);
    };
    
}]);
// end messageCtrl




/* utility functions ----------------------------------------------------------*/
var utils = {};
utils.timeAgo = function(date_str){
    date_str = date_str.replace('+0000','Z');
    var time_formats = [
        [60, 'just now', 1],
        [120, '1 minute ago', '1 minute from now'],
        [3600, 'minutes ago', 60], 
        [7200, '1 hour ago', '1 hour from now'],
        [86400, 'hours ago', 3600], 
        [172800, 'yesterday', 'tomorrow'], 
        [604800, 'days ago', 86400], 
        [1209600, 'last week', 'next week'], 
        [2419200, 'weeks ago', 604800], 
        [4838400, 'last month', 'next month'], 
        [29030400, 'months ago', 2419200], 
        [58060800, 'last year', 'next year'], 
        [2903040000, 'years ago', 29030400], 
        [5806080000, 'last century', 'next century'], 
        [58060800000, 'centuries ago', 2903040000] 
    ];
    var time = ('' + date_str).replace(/-/g,"/").replace(/[TZ]/g," ").replace(/^\s\s*/, '').replace(/\s\s*$/, '');
    if(time.substr(time.length-4,1)==".") time =time.substr(0,time.length-4);
    var seconds = (new Date - new Date(time)) / 1000;
    var token = '', list_choice = 1;
    if (seconds < 0) {
    seconds = Math.abs(seconds);
    token = '';
    list_choice = 2;
    }
    var i = 0, format;
    while (format = time_formats[i++])
    if (seconds < format[0]) {
      if (typeof format[2] == 'string')
        return format[list_choice];
      else
        return Math.floor(seconds / format[2]) + ' ' + format[1] + ' ' + token;
    }
    return time;
};


