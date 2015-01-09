angular.module('myApp', [
  'ui.router',
  'myApp.services',
  'myApp.directives',
  'myApp.controllers',
  'ui.calendar',
])
.config(['$stateProvider', '$urlRouterProvider', '$locationProvider', function ($stateProvider, $urlRouterProvider, $locationProvider)
{
    $stateProvider
    .state('index', {
        url: '/',
        templateUrl: 'main.html',
        abstract: true
    })
    .state('index.home', {                      // rename or remove to use an alternative home dashboard
        url: '',
        templateUrl: 'pages/home.html',
        data : {
            title: 'Home'
        }
    })
    /* alternate dashboard or home layouts */
    .state('index.home-1', {                    // change to 'index.home' to use as home dashboard
        url: 'dashboard-1',
        templateUrl: 'pages/home-1.html',
        data : {
            title: 'Home',                      // set the page title here
            breadcrumb: [{label:"Dashboard"}]   // remove this to hide the breadcrub
        }
    })
    .state('index.home-2', {
        url: 'dashboard-2',
        templateUrl: 'pages/home-2.html',
        data : {
            title: 'Home',
            breadcrumb: [{label:"Home"}]
        }
    })
    .state('index.todo', {
        url: 'todo',
        templateUrl : 'pages/tasks.html',
        data : {
            title: 'Todo',
            breadcrumb  : [{label:"Home",link:"/"},{label:"To-do List"}]
        }
    })
    .state('index.blank', {
	    url: 'blank',
		templateUrl : 'pages/blank.html',
		data : {
		    title: 'Blank',
		    breadcrumb  : [{label:"Home",link:"/"},{label:"Blank"}]
		}
	})
	.state('index.elements', {
	    url: 'elements',
		templateUrl : 'pages/elements.html',
		data : {
		    title: 'UI Kit',
		    breadcrumb  : [{label:"Home",link:"/"},{label:"UI Kit"}]
		}
	})
	.state('index.elements-2', {
	    url: 'elements-2',
		templateUrl : 'pages/elements-2.html',
		data : {
		    title: 'UI Kit - Bootstrap Elements',
		    breadcrumb  : [{label:"Home",link:"/"},{label:"Bootstrap Elements"}]
		}
	})
	.state('index.contact', {
	    url: 'contact',
		templateUrl : 'pages/contact.html',
		controller  : 'contactCtrl',
		data : {
		    title: 'Contact',
		    breadcrumb  : [{label:"Home",link:"/"},{label:"Contact Page"}]
		}
	})
	.state('index.users', {
	    url: 'users',
		templateUrl : 'pages/users.html',
		controller  : 'usersCtrl',
		data : {
		    title: 'Users',
		    breadcrumb  : [{label:"Home",link:"/"},{label:"Users"}]
		}
	})
	.state('index.user-profile', {
	    url: 'user-profile',
		templateUrl : 'partials/user-profile.html',
		data : {
		    title: 'Users',
		    breadcrumb  : [{label:"Home",link:"/"},{label:"Users","link":"/users"},{label:"User Profile"}]
		}
	})
	.state('index.charts', {
	    url: 'charts',
		templateUrl : 'pages/charts.html',
		data : {
		    title: 'Charts',
		    breadcrumb  : [{label:"Home",link:"/"},{label:"Charts"}]
		}
	})
	.state('index.charts-flot', {
	    url: 'charts-flot',
		templateUrl : 'pages/charts-flot.html',
		data : {
		    title: 'Charts - Flot charts',
		    breadcrumb  : [{label:"Home",link:"/"},{label:"Charts","link":"/charts"},{label:"Flot Charts"}]
		}
	})
	.state('index.charts-morris', {
	    url: 'charts-morris',
		templateUrl : 'pages/charts-morris.html',
		data : {
		    title: 'Charts - Morris charts',
		    breadcrumb  : [{label:"Home",link:"/"},{label:"Charts","link":"/charts"},{label:"Morris Charts"}]
		}
	})
	.state('index.forms', {
	    url: 'forms',
		templateUrl : 'pages/forms.html',
		controller  : 'formsCtrl',
		data : {
		    title: 'Forms',
		    breadcrumb  : [{label:"Home",link:"/"},{label:"Forms"}]
		}
	})
	.state('index.forms-advanced', {
        url: 'forms-advanced',
		templateUrl : 'pages/forms-advanced.html',
		data : {
		    title: 'Forms with plugins and advanced features',
		    breadcrumb  : [{label:"Home",link:"/"},{label:"Forms",link:"/forms"},{label:"Advanced"}]
		}
	})
	.state('index.maps', {
	    url: 'maps',
		templateUrl : 'pages/maps.html',
		data : {
		    title: 'Maps and Geolocation',
		    breadcrumb  : [{label:"Home",link:"/"},{label:"Maps"}]
		}
	})
	.state('index.maps-vector', {
	    url: 'maps-vector',
		templateUrl : 'pages/maps-vector.html',
		data : {
		    title: 'Vector Maps'
		}
	})
	.state('index.inbox', {
	    url: 'inbox',
		templateUrl : 'pages/messages.html',
		data : {
		    title: 'Inbox',
		    breadcrumb  : [{label:"Home",link:"/"},{label:"Messages"}]
		}
	})
	.state('index.faq', {
	    url: 'faq',
		templateUrl : 'pages/faq.html',
		data : {
		    title: 'Frequently Asked Questions',
		    breadcrumb  : [{label:"Home",link:"/"},{label:"FAQ's"}]
		}
	})
	.state('index.layout-grid', {
	    url: 'layout-grid',
		templateUrl : 'pages/layout-grid.html',
		data : {
		    title: 'Grid',
		    breadcrumb  : [{label:"Home",link:"/"},{label:"Bootstrap Grid"}]
		}
	})
	.state('index.layout-image-grid', {
	    url: 'layout-image-grid',
		templateUrl : 'pages/layout-image-grid.html',
		data : {
		    title: 'Text and Image Grid',
		    breadcrumb  : [{label:"Home",link:"/"},{label:"Photo Grid"}]
		}
	})
	.state('index.resources', {
	    url: 'resources',
		templateUrl : 'pages/resource-list.html',
		data : {
		    title: 'Results',
		    breadcrumb  : [{label:"Home",link:"/"},{label:"Resources List"}]
		}
	})
	.state('index.resource-tag', {
	    url: 'resources/:tag',
		templateUrl : 'pages/resource-list.html',
		data : {
		    title: 'Results',
		    breadcrumb  : [{label:"Home",link:"/"},{label:"Resources",link:"/resources"},{label:"Tags"}]
		}
	})
	.state('index.resource-id', {
	    url: 'resource/:id',
		templateUrl : 'partials/resource-detail.html',
		data : {
		    title: 'Detail',
		    breadcrumb  : [{label:"Home",link:"/"},{label:"Resources",link:"/resources"},{label:"Detail"}]
		}
	})
	.state('index.widgets', {
	    url: 'widgets',
		templateUrl : 'pages/widgets.html',
		controller  : 'widgetsCtrl',
		data : {
		    title: 'Widgets',
		    breadcrumb  : [{label:"Home",link:"/"},{label:"Widgets"}]
		}
	})
	.state('index.pages', {
	    url: 'pages',
		templateUrl : 'pages/pages.html',
		data : {
		    title: 'Pages',
		    breadcrumb  : [{label:"Home",link:"/"},{label:"Example Pages"}]
		}
	})
	.state('index.bonus', {
	    url: 'bonus',
		templateUrl : 'pages/bonus.html',
		data : {
		    title: 'Bonus',
		    breadcrumb  : [{label:"Home",link:"/"},{label:"Extras"}]
		}
	})
	.state('index.calendar', {
	    url: 'calendar',
		templateUrl : 'pages/calendar.html',
		data : {
		    title: 'Full Calendar',
		    breadcrumb  : [{label:"Home",link:"/"},{label:"Calendar",link:"/calendar"}]
		}
	})
	.state('index.timeline', {
	    url: 'timeline',
		templateUrl : 'pages/timeline.html',
		data : {
		    title: 'Events Timeline',
		    breadcrumb  : [{label:"Home",link:"/"},{label:"Timeline"}]
		}
	})
    .state('index.gallery', {
	    url: 'gallery',
		templateUrl : 'pages/gallery.html',
		data : {
		    title: 'Gallery',
		    breadcrumb  : [{label:"Home",link:"/"},{label:"Gallery"}]
		}
	})
	.state('index.filemanager', {
	    url: 'filemanager',
		templateUrl : 'pages/filemanager.html',
		data : {
		    title: 'File Manager',
		    breadcrumb  : [{label:"Home",link:"/"},{label:"File Manager"}]
		}
	})
	.state('index.dropzone', {
	    url: 'dropzone',
		templateUrl : 'partials/dropzone.html',
		data : {
		    title: 'Dropzone Uploader',
		    breadcrumb  : [{label:"Home",link:"/"},{label:"File Uploader"}]
		}
	})
	.state('index.fonts', {
        url: 'fonts',
		templateUrl : 'pages/fonts.html',
		data : {
		    title: 'Fonts',
		    breadcrumb  : [{label:"Home",link:"/"},{label:"Fonts"}]
		}
	})
	.state('index.fonts-fa', {
        url: 'fonts-fa',
		templateUrl : 'pages/fonts-fa.html',
		data : {
		    title: 'Font Awesome Fonts',
		    breadcrumb  : [{label:"Home",link:"/"},{label:"Fonts",link:"/fonts"},{label:"Font Awesome"}]
		}
	})
	.state('index.social', {
        url: 'social',
		templateUrl : 'pages/social.html',
		data : {
		    title: 'Social Extras',
		    breadcrumb  : [{label:"Home",link:"/"},{label:"Social Color Pack"}]
		}
	})
	.state('index.signup', {
        url: 'signup',
		templateUrl : 'pages/signup.html',
		data : {
		    title: 'Signup',
		    breadcrumb  : [{label:"Home",link:"/"},{label:"Register"}]
		}
	})
	.state('index.error-404', {
        url: 'error-404',
        templateUrl: 'pages/error-404.html',
        data: {title: '404 Not Found'}
    })
    .state('index.error-500', {
        url: 'error-500',
        templateUrl: 'pages/error-500.html',
        data: {title: '500 Server Error'}
    })

    .state('login', {
        url: '/login',
        templateUrl: 'login.html'
    });
        
    $urlRouterProvider.otherwise('/');
    $locationProvider.html5Mode(true);
    
}])
.run(['$location', '$rootScope', '$state', function($location, $rootScope, $state) {
    
    $rootScope.$on('$stateChangeStart', function(event, current, previous){ 
        if (typeof current.data != "undefined") {
            $rootScope.title = current.data.title;
            $rootScope.breadcrumb = current.data.breadcrumb;
        }
    });
    
}]);

// init jquery functions and plugins
$.fn.refreshMe = function(opts){
  
    var $this = this,
      defaults = {
        ms:1500,
        selector:"parents('.panel')",
        started:function(){},
        completed:function(){}
    },
    settings = $.extend(defaults, opts);
    
    var container = $(settings.selector);
    var panelToRefresh = container.find('.refresh-container');
    var dataToRefresh = container.find('.refresh-data');
    
    var ms = settings.ms;
    var started = settings.started;		// function before timeout
    var completed = settings.completed;	// function after timeout
    
    var spinIcon;
    if ($this.hasClass("fa")) {
        spinIcon=$this;
    }
    else{
        spinIcon=$this.find('.fa');
    }
    
    var containerCss = {position:"relative"};
    container.css(containerCss);
    
    var overlay = $('<div class="refresh-overlay"><i class="refresh-spinner fa fa-spinner fa-spin fa-5x" style="margin-top:10%;opacity:0.7"></i></div>');
    var css = {
     position:"absolute",
     top:0,
     right:0,
     background:"rgba(200,200,200,0.25)",
     width:"100%",
     height:"100%",
     "text-align":"center",
     "z-index":4
    };
    overlay.css(css);
    
    $this.on('click', function(){
        spinIcon.addClass("fa-spin");
        overlay.insertBefore(panelToRefresh);
        if (dataToRefresh) {
          started(dataToRefresh);
        }
        setTimeout(function(){
          if (dataToRefresh) {
              completed(dataToRefresh);
          }
          overlay.remove();
          spinIcon.removeClass("fa-spin");
        },ms);
        return false;
    }); //click
      
}; /* end refreshMe plugin */


$.fn.pageMe = function(opts){
    var $this = this,
        defaults = {
            perPage: 7,
            showPrevNext: false,
            numbersPerPage: 5,
            hidePageNumbers: false,
            pagerSelector: ".pagination"
        },
        settings = $.extend(defaults, opts);
    
    var listElement = $this;
    var perPage = parseInt(settings.perPage); 
    var children = listElement.children();
    var pager = $(settings.pagerSelector);
    
    if (typeof settings.childSelector!="undefined") {
        children = listElement.find(settings.childSelector);
    }
    
    if (typeof settings.pagerSelector!="undefined") {
        pager = $(settings.pagerSelector);
    }
    
    var numItems = children.size();
    var numPages = Math.ceil(numItems/perPage);

    pager.data("curr",0);
    
    if (settings.showPrevNext){
        $('<li class="disabled"><a href="javascript:;" class="prev_link">«</a></li>').appendTo(pager);
    }
    
    var curr = 0;
    while(numPages > curr && (settings.hidePageNumbers==false)){
       $('<li><a href="javascript:;" class="page_link">'+(curr+1)+'</a></li>').appendTo(pager);
       curr++;
    }
  
    if (settings.numbersPerPage>1) {
       $('.page_link').hide();
       $('.page_link').slice(pager.data("curr"), settings.numbersPerPage).show();
    }
    
    if (settings.showPrevNext){
       $('<li><a href="javascript:;" class="next_link">»</a></li>').appendTo(pager);
    }
    
    pager.find('.page_link:first').addClass('active');
    if (numPages<=1) {
        pager.find('.next_link').parent().removeClass("disabled");
    }
  	pager.children().eq(1).addClass("active");
    
    children.hide();
    children.slice(0, perPage).show();
    
    pager.find('li .page_link').click(function(){
        var clickedPage = $(this).html().valueOf()-1;
        goTo(clickedPage);
        return false;
    });
    pager.find('li .prev_link').click(function(){
      if ($(this).parent().hasClass("disabled")){
      	return false;
      }
      previous();
      return false;
    });
    pager.find('li .next_link').click(function(){
      if ($(this).parent().hasClass("disabled")){
        return false;
      }
      next();
      return false;
    });
    
    function previous(){
      var goToPage = parseInt(pager.data("curr")) - 1;
      goTo(goToPage);
    }
     
    function next(){
      var goToPage = parseInt(pager.data("curr")) + 1;
      goTo(goToPage);
    }
    
    function goTo(page){
        var startAt = page * perPage,
            endOn = startAt + perPage;
        
        children.css('display','none').slice(startAt, endOn).show();
      
        if (page>=1) {
            pager.find('.prev_link').parent().removeClass("disabled");
        }
        else {
            pager.find('.prev_link').parent().addClass("disabled");
        }
        
        if (page<(numPages-1)) {
            pager.find('.next_link').parent().removeClass("disabled");
        }
        else {
            pager.find('.next_link').parent().addClass("disabled");
        }
        
        pager.data("curr",page);
       
        if (settings.numbersPerPage>1) {
            $('.page_link').slice(page, settings.numbersPerPage+page).show();
        }
        pager.children().removeClass("active");
        pager.children().eq(page+1).addClass("active"); 
    }
};
/* end pageMe plugin */


$.fn.aniMe = function(opts){
    var $this = this,
        defaults = {
            aniClass: 'slide-down',
            container: 'window',
            repeat: false
        },
        settings = $.extend(defaults, opts);
    
    var ele = $this;
    var aniClass = settings.aniClass;
    var container = settings.container;
    var repeat = settings.repeat;
    
    if (typeof ele.offset()!="undefined") {
    
        var pos = ele.offset().top;
        
        var topOfWindow = $(container).scrollTop();
        
        if (pos < topOfWindow+500) {
          ele.addClass(aniClass);
          
          if (repeat) {
            setTimeout(function(){
              ele.removeClass(aniClass);
            },1500);
          }
        }
    
    }
};

$(document).ready(function(){
    jQuery.event.props.push('dataTransfer'); //prevent conflict with drag-drop
   
    $('#main-wrapper').scroll(function() {
      $('.list-group, tbody').aniMe({
        aniClass:'pull-down',
        container:'#main-wrapper'
      });
    });
    
});
  
