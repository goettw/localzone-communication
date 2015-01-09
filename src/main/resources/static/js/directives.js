'use strict';

/* Directives */
angular.module('myApp.directives', [])
    .directive('appVersion', ['version', function(version) {
        return function(scope, elm, attrs) {
          elm.text(version);
        };
    }])
    .directive('header', function () {
        return {
            restrict: 'A',
            transclude : true,
            templateUrl:'partials/header.html'
        }
    })
    .directive('footer', function () {
        return {
            restrict: 'A',
            transclude : true,
            templateUrl:'partials/footer.html'
        }
    })
    .directive('sidebar', function () {
        return {
            restrict: 'A',
            transclude : true,
            templateUrl:'partials/sidebar.html'
        }
    })
    .directive('breadcrumb', function () {
        return {
            restrict: 'A',
            replace: true,
            templateUrl:'partials/breadcrumb.html'
        }
    })
    .directive('menuItems', function () {
        return {
            restrict: 'A',
            transclude : true,
            templateUrl:'partials/menu-items.html'
        }
    })
    .directive('menuItemsSm', function () {
        return {
            restrict: 'A',
            transclude : true,
            templateUrl:'partials/menu-items-sm.html'
        }
    })
    .directive('offCanvas', function () {
        return {
            restrict: 'A',
            link: function(scope, el, attrs) {
                var $el = $(el);
                var $targetName = $el.attr("data-target");
                var $target = $($targetName);
                if ($target) {
                    $el.click(function(){
                        $el.toggleClass("glyphicon-chevron-left glyphicon-chevron-right");
                    })
                }
            }
        }
    })
    .directive('currentTime', function($timeout, dateFilter) {
        return function(scope, element, attrs) {
            
          var format,
              timeoutId;
              
          function updateTime() {
            element.text(dateFilter(new Date(), format));
          }
          
          // watch the expression, and update the UI.
          scope.$watch(attrs.currentTime, function(value) {
            format = value;
            updateTime();
          });
          
          // schedule update in one second
          function updateLater() {
            // save the timeoutId for canceling
            timeoutId = $timeout(function() {
              updateTime(); // update DOM
              updateLater(); // schedule another update
            }, 1000);
          }
          
          // listen on DOM destroy (removal) event, and cancel the next UI update
          element.bind('$destroy', function() {
            $timeout.cancel(timeoutId);
          });
          
          // start updates
          updateLater();
        };
    })
    .directive('jasnyUpload', ['asyncScript', '$timeout', function (asyncScript, $timeout) {
        return {
            restrict: 'C',
            link: function(scope, el, attrs) {
                asyncScript.load('jasny',function(){
                    $timeout(function () {
                        
                    });
                });
            }
        }
    }])
    .directive('starrr', ['asyncScript', '$timeout', function (asyncScript, $timeout) {
        return {
            restrict: 'C',
            transclude : true,
            link: function(scope, el, attrs) {
                asyncScript.load('starr',function(){
                    $timeout(function () {
                        var $el = $(el);
                        var initValue = parseInt($el.attr("data-init-value"));
                        $el.starrr({
                            rating:initValue,
                            emptyStarClass:'fa fa-lg fa-star-o',
                            fullStarClass:'fa fa-lg fa-star'
                        }).on('starrr:change', function(e, value){
                            scope.$apply(function(){
                                scope.item.avgRating = value;
                            });
                        });
                    });
                });
            }
        }
    }])
    .directive('btnToggle', ['$timeout', function ($timeout) {
        return {
            restrict: 'C',
            link: function(scope, el, attrs) {
                var $el = $(el);
                $el.click(function() {
                    
                    $el.find('.btn').toggleClass('active');  
                    if ($el.find('.btn-primary').size()>0) {
                        $el.find('.btn').toggleClass('btn-primary');
                    }
                    if ($el.find('.btn-danger').size()>0) {
                        $el.find('.btn').toggleClass('btn-danger');
                    }
                    if ($el.find('.btn-success').size()>0) {
                        $el.find('.btn').toggleClass('btn-success');
                    }
                    if ($el.find('.btn-info').size()>0) {
                        $el.find('.btn').toggleClass('btn-info');
                    }
                    $el.find('.btn').toggleClass('btn-default');
                });
            }
        }
    }])
    .directive('flotLive', ['asyncScript', '$timeout', function (asyncScript, $timeout) {
        return {
            restrict: 'A',
            transclude : true,
            link: function(scope, el, attrs) {
                asyncScript.load('flot',function(){
                asyncScript.load('flot_resize',function(){
                    $timeout(function () {
                        var $el = $(el);
                        
                        // real-time chart
                        // we use an inline data source in the example, usually data would
                        // be fetched from a server
                        var data = [], totalPoints = 170;
                        function getRandomData(seed) {
                            
                            var seeder = (seed)||50;
                        
                            if (data.length > 0)
                              data = data.slice(1);
                            
                            // do a random walk
                            while (data.length < totalPoints) {
                              var prev = data.length > 0 ? data[data.length - 1] : seeder;
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
                            grid:{borderColor:'rgba(190,190,190,0.4)',borderWidth:1,hoverable:true,clickable:true,color:'rgba(190,190,190,0.9)'},
                            series:{shadowSize:0,lines:{show:true,fill:true,fillColor:{colors:['rgba(100,180,100,0.4)','rgba(82,174,82,1)']}},color:'#5cb85c'},
                            yaxis:{min:0,max:100,font:'8pt',color:'rgba(180,180,180,0.4)'},
                            xaxis:{show:true,font:'8pt',color:'rgba(180,180,180,0.4)'}
                        };
                        
                        var plot = $.plot($el, [ getRandomData(50) ], options);
                        var realTime;
                        function update() {
                            plot.setData([ getRandomData(50) ]);
                            plot.draw();
                            realTime = setTimeout(update, updateInterval);
                        }
                
                        setTimeout(function(){update()}
                        ,1000);
                        
                        $('.realtime').click(function() {
                            var activeBtn = $(this).find(".active");
                            
                            if (activeBtn.text()==="live"){
                              update();
                            }
                            else {
                              clearTimeout(realTime);
                            }
                        });//toggle click
                        
                    });
                }); //flot 
                }); //flot resize
            }
        }
    }])
    .directive('flotLine', ['asyncScript', '$timeout', function (asyncScript, $timeout) {
        return {
            restrict: 'A',
            transclude : true,
            link: function(scope, el, attrs) {
                var gridStyle = {borderColor:'rgba(170,170,170,0.6)',borderWidth:1,hoverable:true,clickable:true};
                
                asyncScript.load('flot',function(){
                asyncScript.load('flot_resize',function(){
                    $timeout(function () {
                        
                        var $el = $(el);
                        
                        // mock data sets
                        var d1 = [[0, 3], [4, 8], [8, 5], [9, 13]];
                        var d2 = [[0, 12], [7, 12], [12, 13], [9, 11]];
                        
                        // line
                        $.plot($el,[d1,d2],{
                            yaxis:{font:'rgba(180,180,180,0.6)'},
                            xaxis:{font:'rgba(180,180,180,0.6)'},
                            grid:gridStyle,series:{color:'#ff4444',lines:{show:true},points:{show:true}}
                        });
                    });
                });
                });
            }
        }
    }])
    .directive('flotBar', ['asyncScript', '$timeout', function (asyncScript, $timeout) {
        return {
            restrict: 'A',
            transclude : true,
            link: function(scope, el, attrs) {
                asyncScript.load('flot',function(){
                asyncScript.load('flot_resize',function(){
                    $timeout(function () {
                        var $el = $(el);
                        var d1 = [[0, 3], [2, 8], [4, 14], [6, 5], [8, 11], [10, 12], [12, 8], [14, 10], [16, 5], [18, 12], [20, 10]];
                        var d2 = [[1, 12], [3, 12], [5, 14], [7, 5], [9, 11], [11, 12], [13, 4], [15, 14], [17, 13], [19, 6]];
                        var data = [
                            {label: "France",data: d1, color:'#64aaec'},
                            {label: "Germany",data: d2, color:'#5bc0de'}
                        ];
                        $.plot($el,data,{
                            series:{bars:{show:true,barWidth:0.4,align:'center'}},
                            valueLabels:{show:true},
                            legend: {show:false},
                            yaxis:{font:'#ddd'},
                            xaxis:{font:'#ddd'},
                            grid:{borderColor:'rgba(170,170,170,0.6)',borderWidth:1,show:true,hoverable:true}});
                        });
                        
                }); //flot-resize
                }); //flot
            }
        }
    }])
    .directive('flotPie', ['asyncScript', '$timeout', function (asyncScript, $timeout) {
        return {
            restrict: 'A',
            transclude : true,
            link: function(scope, el, attrs) {
                asyncScript.load('flot',function(){
                asyncScript.load('flot_resize',function(){
                asyncScript.load('flot_pie',function(){
                    var $el = $(el);
                    $.plot($el,
                        [{label:'Small',data:20,color:'#5555ff'},{label:'Medium',data:21,color:'#5bc0de'},{label:'Large',data:65,color:'#428bca'}],
                        {
                        series:{
                            pie:{show:true,label:{show:true}}
                        },
                        legend:{show:false},
                        grid:{hoverable:true}
                    });
                });
                });
                });
            }
        }
    }])
    .directive('removeMe', function ($timeout) {
        return {
            restrict: 'A',
            link: function(scope, el, attrs) {
                $timeout(function () {
                    var $el = $(el);
                    setTimeout(function(){
                      // remove the alert after 7 secs
                      $el.fadeOut(3000,function(){
                        $el.remove();
                      });
                    },7000);
                }); //timeout
            }
        }
    })
    .directive('miniSlider', function ($timeout) {
        return {
            restrict: 'A',
            transclude : true,
            templateUrl:'partials/mini-slider.html',
            link: function(scope, el, attrs) {
                $timeout(function () {
                    var $el = $(el);
                    var $carousel = $el.find('.carousel');
                    var $carouselText = $el.find('.carousel-text');
                    var $slideContent = $el.find('.slide-content');
                    $carouselText.html($slideContent.find('[data-id="slide-content-0"]').html());
                    $carousel.on('slid.bs.carousel', function (e) {
                        var id = $('.item.active').data('slide-number');
                        $carouselText.html($slideContent.find('[data-id="slide-content-'+id+'"]').html());
                    });
                }); //timeout
            }
        }
    })
    .directive('selectpicker', ['asyncScript', '$timeout', function (asyncScript, $timeout) {
        return {
          restrict: 'E',
          replace: true,
          scope: {
              selected: '=',
              array: '=',
              title: '='
          },
          template: '<select class="form-control selectpicker" data-live-search="true" ng-title="title">' +
            '<option ng-repeat="item in array" ng-selected="selected == item.value" value="{{item.value}}">{{item.option}}</option>' +
            '</select>',
          link: function(scope, el, attrs) {
            asyncScript.load('selectpicker',function(){
              $timeout(function () {
                  var select = $(el).selectpicker();
                  select.change(function(evt) {
                      var val = $(el).selectpicker('val');
                      scope.selected = val;
                      scope.$apply();
                  });
              }); //timeout
            }); //script
          }
        };
    }])
    .directive('datepicker', ['asyncScript', '$timeout', function (asyncScript, $timeout) {
        return {
          restrict: 'A',
          transclude: true,
          scope: {},
          link: function(scope, el, attrs) {
            asyncScript.load('datepicker',function(){
                $timeout(function () {
                    $(el).datepicker({
                       autoclose:true,
                    }).on("changeDate", function(e){
                        console.log(e.date); // handle your date change event here
                    });
              }); //timeout
            }); //script
          }
        };
    }])
    .directive('tagpicker', ['asyncScript', '$timeout', function (asyncScript, $timeout) {
        return {
          restrict: 'E',
          replace: true,
          scope: {
              selectedList: '=',
              array: '='
          },
          template: '<select class="form-control selectpicker" multiple ng-title="title">' +
            '<option ng-repeat="item in array" data-content="<span class=\'label label-success\'>{{item.option}}</span>" ng-selected="selectedList.length>0 && selectedList.indexOf(item.value)!=-1" value="{{item.value}}">{{item.option}}</option>' +
            '</select>',
          link: function(scope, el, attrs) {
            asyncScript.load('selectpicker',function(){
              $timeout(function () {
                  var select = $(el).selectpicker();
                  scope.$apply();
              }); //timeout
            }); //script
          }
        };
    }])
    .directive('miniColors', ['asyncScript', '$timeout', function (asyncScript, $timeout) {
        return {
            restrict: 'A',
            transclude: true,
            link: function(scope, el, attrs) {
                asyncScript.load('minicolors',function(){
                    $(el).minicolors({
                        defaultValue: '#7dd0fa',
                        theme: 'bootstrap'
                    });
                }); //script loader
            }
        };//return
    }])
    .directive('vectorMap', ['asyncScript', '$timeout', function (asyncScript, $timeout) {
        return {
            restrict: 'A',
            transclude : true,
            link: function(scope, el, attrs) {
                
                var $el = $(el);
                
                scope.markerColor = attrs.markerColor||'#cc0000';
                scope.bgColor = attrs.bgColor||'transparent';
                scope.scale = attrs.scale||2;
                scope.scaleColors = attrs.scaleColors||["#454545","#5e5e5e"];
                
                asyncScript.load("jvectormap",function(){
                asyncScript.load("jvectormap_en",function(){
                      
                      $el.vectorMap({
                        map: 'world_mill_en',
                        backgroundColor: scope.bgColor,
                        zoomMin: 2,
                        zoomOnScroll: false,
                        regionStyle: {
                          initial: {
                            fill:"#333",
                            "fill-opacity":1,
                            stroke: '#222',
                            "stroke-width":0.4,
                            "stroke-opacity":1
                          },
                          hover: {
                            "fill-opacity": 0.8
                          },
                          selected: {
                            fill: 'blue'
                          },
                          selectedHover: {
                          }
                        },
                        focusOn:{x:0.5, y:0.5, scale:scope.scale},
                        markerStyle: {
                          initial: {
                            fill: scope.markerColor,
                            stroke: scope.markerColor
                          }
                        },
                        markers: [
                            {latLng:[41.90, 12.45],name:'Vatican City'},
                            {latLng:[43.73, 7.41],name:'Monaco'},
                            {latLng:[-0.52, 166.93],name:'Nauru'},
                            {latLng:[-8.51, 179.21],name:'Tuvalu'},
                            {latLng:[7.11,171.06],name:"Marshall Islands"},
                            {latLng:[17.3,-62.73],name:"Saint Kitts and Nevis"},
                            {latLng:[3.2,73.22],name:"Maldives"},
                            {latLng:[35.88,14.5],name:"Malta"},
                            {latLng:[41.0,-71.06],name:"New England"},
                            {latLng:[12.05,-61.75],name:"Grenada"},
                            {latLng:[13.16,-59.55],name:"Barbados"},
                            {latLng:[17.11,-61.85],name:"Antigua and Barbuda"},
                            {latLng:[-4.61,55.45],name:"Seychelles"},
                            {latLng:[7.35,134.46],name:"Palau"},
                            {latLng:[42.5,1.51],name:"Andorra"}
                            ],
                        series: {
                          regions: [{
                            values: {
                              AL: 11.58,
                              DZ: 158.97,
                              AO: 85.81,
                              AG: 1.1,
                              AR: 351.02,
                              AM: 8.83,
                              AU: 1219.72,
                              AT: 366.26,
                              BA: 16.2,
                              BW: 12.5,
                              BR: 2023.53,
                              BN: 11.96,
                              BG: 44.84,
                              BF: 8.67,
                              BI: 1.47,
                              KH: 11.36,
                              CF: 2.11,
                              TD: 7.59,
                              CL: 199.18,
                              CN: 5745.13,
                              CO: 283.11,
                              KM: 0.56,
                              CD: 12.6,
                              CG: 11.88,
                              CR: 35.02,
                              CI: 22.38,
                              HR: 59.92,
                              CY: 22.75,
                              CZ: 195.23,
                              DK: 304.56,
                              DJ: 1.14,
                              DM: 0.38,
                              DO: 50.87,
                              EC: 61.49,
                              UG: 17.12,
                              UA: 136.56,
                              AE: 239.65,
                              GB: 2258.57,
                              US: 14624.18,
                              UY: 40.71,
                              UZ: 37.72,
                              VU: 0.72},
                            scale: scope.scaleColors,
                            normalizeFunction: 'polynomial'
                          }]
                        }
                      });
                      
                }); // async
                }); // async
            } // link
        }
    }])
    .directive('dropZone', ['asyncScript', function(asyncScript) {
        return function(scope, element, attrs) {
            scope.files = [];
            asyncScript.load('dropzone',function(){
              element.dropzone({ 
                url: "/upload",
                maxFilesize: 100,
                paramName: "uploadfile",
                maxThumbnailFilesize: 5,
                init: function() {
                  scope.files.push({file: 'added'});
                  this.on('success', function(file, json) {
                  });
                  this.on('addedfile', function(file) {
                    scope.$apply(function(){
                      alert(file);
                      scope.files.push({file: 'added'});
                    });
                  });
                  this.on('drop', function(file) {
                    alert('file');
                  });
                  
                }
              });//dropzone
            });//script loader
        };
    }])
    .directive('gallery', ['asyncScript', '$timeout', function(asyncScript, $timeout) {
        return {
            restrict: 'A',
            link: function(scope, el, attrs) {
            
                var $caro, $thumbs = $(el).find('.thumbnail');
                var modalTemplate = '';
                    modalTemplate += '<div class="modal" id="lightboxModal" role="dialog">';
                    modalTemplate += '<div class="modal-dialog modal-lg">';
                    modalTemplate += '<div class="modal-content">';
                    modalTemplate += '<div class="modal-body">';
    		        modalTemplate += '<div id="modalCarousel" class="carousel">';
                    modalTemplate += '<div class="carousel-inner">';
                    
                    $thumbs.each(function(i,ele) {
                      var item = $('<div class="item"></div>');
                      var itemDiv = $(ele).parents('div');
                      var title = $(ele).parent('a').attr("title");
                      var hTitle = $('<h4>'+title+'</h4>');
                      hTitle.appendTo(item);
                      item.attr("title",title);
                      if (i==0){ // set first item active
                        item.addClass('active');
                      }
                      $(itemDiv.html()).appendTo(item);
                      modalTemplate += item[0].outerHTML;
                      
                    });
                    
                    
                    modalTemplate += '</div><!/--carousel-inner-->';
                    modalTemplate += '<a class="carousel-control left" data-target="#modalCarousel" data-slide="prev" href="javascript:;"><i class="glyphicon glyphicon-chevron-left"></i></a>';
                    modalTemplate += '<a class="carousel-control right" data-target="#modalCarousel" data-slide="next" href="javascript:;"><i class="glyphicon glyphicon-chevron-right"></i></a>';
                    modalTemplate += '</div><!/--carousel-->';
    	            modalTemplate += '</div><!/--body-->';
    	            modalTemplate += '<div class="modal-footer">';
    		        modalTemplate += '<button class="btn btn-default" data-dismiss="modal">Close</button>';
                    modalTemplate += '</div><!--/modal-footer-->';
                    modalTemplate += '</div><!--/modal-content-->';
                    modalTemplate += '</div><!--/modal-dialog-->';
                    modalTemplate += '</div><!--/modal-->';
                
                $(el).parent().parent().parent().append(modalTemplate);
                
                /* activate the carousel */
                $timeout(function () {
                    
                    /* find the carousel */
                    $caro = $(el).parent().find('#modalCarousel');
                    $caro.carousel({interval:false});
                    
                    /* when clicking a thumbnail, show the modal */
                    $thumbs.click(function(){
                      var idx = $(this).parents('div').index();
                      var id = parseInt(idx);
                      $caro.carousel(id); // slide carousel to selected
                      $('#lightboxModal').modal('show'); // show the modal
                    });
                });
              
            }//link
        };//return
    }])
    .directive('tabWizardBtn', function() {
        return {
            restrict: 'C',
            link: function(scope, el, attrs) {
                $(el).click(function(){
                    var nextId = $(this).parents('.tab-pane').next().attr("id")||'step1';
                    $("[data-target=#"+nextId+"]").tab('show');
                });
            }
        };
    })
    .directive('liveCharCount', function() {
        return {
            restrict: 'C',
            link: function(scope, el, attrs) {
                var $el = $(el);
                var textMax = parseInt($(el).attr("data-max"));
                var messageSelector = $(el).attr("data-target");
                $(messageSelector).html(textMax);
                $el.keyup(function() {
                  var text_length = $el.val().length;
                  var text_remaining = textMax - text_length;
                  $(messageSelector).html(text_remaining);
                });
            }
        };
    })
    .directive('toggle', function(){
        return {
            restrict: 'A',
            link: function(scope, element, attrs){
                if (attrs.toggle=="tooltip"){
                    $(element).tooltip();
                }
                if (attrs.toggle=="popover"){
                    $(element).popover();
                }
            }
        };
    })
    .directive('pageMe', function($timeout) {
        return {
            restrict: 'C',
            link: function(scope, el, attrs) {
                
            $timeout(function(){
                
                var jqEl = $(el);
                var ps = jqEl.attr("data-pager");
                var pp = jqEl.attr("data-per-page");
                
                jqEl.pageMe({
                 pagerSelector:ps,
                 showPrevNext:true,
                 hidePageNumbers:false,
                 perPage:pp
                });
                
            });

            }
        };
    })
    .directive('refreshMe', function() {
        return {
            restrict: 'C',
            link: function(scope, el, attrs) {
                var jqEl = $(el);
                var dt = jqEl.attr("data-target");
                // hookup refresh plugin
                jqEl.refreshMe({
                    selector:dt
                });
            }
        };
    })
    .directive('lvlDraggable', ['$rootScope', 'uuid', function($rootScope, uuid) {
          return {
            restrict: 'A',
            link: function(scope, el, attrs, controller) {
              angular.element(el).attr("draggable", "true");
              
              var id = angular.element(el).attr("id");
              
              if (!id) {
                id = uuid.new()
                angular.element(el).attr("id", id);
              }
              
              el.bind("dragstart", function(e) {
                e.dataTransfer.setData('text', id);
                $rootScope.$emit("LVL-DRAG-START");
              });
              
              el.bind("dragend", function(e) {
                $rootScope.$emit("LVL-DRAG-END");
              });
            }
          }
    }])
    .directive('lvlDropTarget', ['$rootScope', 'uuid', function($rootScope, uuid) {
      return {
        restrict: 'A',
        scope: {
          onDrop: '&'
        },
        link: function(scope, el, attrs, controller) {
          
          var id = angular.element(el).attr("id");
          
          if (!id) {
            id = uuid.new();
            angular.element(el).attr("id", id);
          }
          
          el.bind("dragover", function(e) {
            if (e.preventDefault) {
              e.preventDefault();
            }
            
            e.dataTransfer.dropEffect = 'move';
            return false;
          });
          
          el.bind("dragenter", function(e) {
            angular.element(e.target).addClass('lvl-over');
          });
          
          el.bind("dragleave", function(e) {
            angular.element(e.target).removeClass('lvl-over');  // this / e.target is previous target element.
          });
          
          el.bind("drop", function(e) {
            if (e.preventDefault) {
              e.preventDefault();
            }
            
            if (e.stopPropagation) {
              e.stopPropagation();
            }
            var data = e.dataTransfer.getData("text");
            var dest = document.getElementById(id);
            var src = document.getElementById(data);
            
            scope.onDrop({dragEl: src, dropEl: dest});
            $rootScope.$emit("LVL-DRAG-END");
          });
          
          $rootScope.$on("LVL-DRAG-START", function() {
            var el = document.getElementById(id);
            angular.element(el).addClass("lvl-target");
          });
          
          $rootScope.$on("LVL-DRAG-END", function() {
            var el = document.getElementById(id);
            angular.element(el).removeClass("lvl-target");
            
            var els = document.getElementsByClassName("lvl-over");
            for (var e in els) {
              //angular.element(els[e]).removeClass("lvl-over");
            }
          });
        }
      }
    }])
    .directive('todo', function () {
        return {
            restrict: 'A',
            transclude : true,
            templateUrl:'partials/todo.html'
        }
    })
    .directive('todoEscape', function () {
        var ESCAPE_KEY = 27;
        return function (scope, elem, attrs) {
            elem.bind('keydown', function (event) {
            if (event.keyCode === ESCAPE_KEY) {
                scope.$apply(attrs.todoEscape);
            }
            });
        };
    })
    .directive('todoFocus', function ($timeout) {
        return function (scope, elem, attrs) {
            scope.$watch(attrs.todoFocus, function (newVal) {
                if (newVal) {
                    $timeout(function () {
                        elem[0].focus();
                    }, 0, false);
                }
            });
        };
    })
    .directive('inbox', function () {
        return {
            restrict: 'A',
            transclude : true,
            templateUrl:'partials/inbox.html'
        }
    });