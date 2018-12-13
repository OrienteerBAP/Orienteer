/* =========================================================
 * bootstrap-tabdrop.js
 * http://www.eyecon.ro/bootstrap-tabdrop
 * =========================================================
 * Copyright 2012 Stefan Petre
 * Copyright 2013 Jenna Schabdach
 * Copyright 2014 Jose Ant. Aranda
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ========================================================= */

!function ($) {

    var WinResizer = (function () {
        var registered = [];
        var inited = false;
        var timer;
        var notify = function () {
        	for (var i = 0, cnt = registered.length; i < cnt; i++) {
        		registered[i].apply();
        	}
        };
        var resize = function () {
            clearTimeout(timer);
            timer = setTimeout(notify, 100);
        };
        return {
            register: function (fn) {
                registered.push(fn);
                if (inited === false) {
                    $(window).on('resize', resize);
                    inited = true;
                }
            },
            unregister: function (fn) {
                var registeredFnIndex = registered.indexOf(fn);
                if (registeredFnIndex > -1) {
                    registered.splice(registeredFnIndex, 1);
                }
            }
        }
    }());

    var TabDrop = function (element, options) {
        this.element = $(element);
        this.options = options;

        if (options.align === "left") {
            this.dropdown = $('<li class="dropdown d-none pull-left tabdrop nav-item">' +
                '<a class="dropdown-toggle nav-link" data-toggle="dropdown" href="javascript:;">' +
                '<span class="display-tab"></span><b class="caret"></b></a><ul class="dropdown-menu tabdrop-menu"></ul></li>');
        } else {
            this.dropdown = $('<li class="dropdown d-none pull-right tabdrop nav-item">' +
                '<a class="dropdown-toggle nav-link" data-toggle="dropdown" href="javascript:;"><span class="display-tab">' +
                '</span><b class="caret"></b></a><ul class="dropdown-menu tabdrop-menu"></ul></li>');
        }

        this.dropdown.prependTo(this.element);
        if (this.element.parent().is('.tabs-below')) {
            this.dropdown.addClass('dropup');
        }

        var boundLayout = $.proxy(this.layout, this);

        WinResizer.register(boundLayout);
        this.element.on('shown.bs.tab', function (e) {
            boundLayout();
        });

        this.teardown = function () {
            WinResizer.unregister(boundLayout);
            this.element.off('shown.bs.tab', function (e) {
                boundLayout();
            });
        };

        this.layout();
    };

    TabDrop.prototype = {
        constructor: TabDrop,

        layout: function () {
            var self = this;
            var collection = [];
            var isUsingFlexbox = function(el){
                return el.element.css('display').indexOf('flex') > -1;
            };

            function setDropdownText(text) {
                self.dropdown.find('a span.display-tab').html(text);
            }

            function setDropdownDefaultText(collection) {
                var text;
                if (jQuery.isFunction(self.options.text)) {
                    text = self.options.text(collection);
                } else {
                    text = self.options.text;
                }
                setDropdownText(text);
            }

            // Flexbox support
            function handleFlexbox(){
                if (isUsingFlexbox(self)){
                    if (self.element.find('li.tabdrop').hasClass('pull-right')){
                        self.element.find('li.tabdrop').css({position: 'absolute', right: 0});
                        self.element.css('padding-right', self.element.find('.tabdrop').outerWidth(true));
                    }
                }
            }

            function checkOffsetAndPush(recursion) {
                self.element.find('> li:not(.tabdrop)')
                    .each(function () {
                        if (this.offsetTop > self.options.offsetTop) {
                            collection.push(this);
                        }
                    });

                if (collection.length > 0) {
                    if (!recursion) {
                        self.dropdown.removeClass('d-none');
                        self.dropdown.find('ul').empty();
                    }
                    self.dropdown.find('ul').prepend(collection);

                    var $active = self.dropdown.find('.active');
                    if ($active.length > 0) {
                        self.dropdown.find('a').first().addClass('active');
                        setDropdownText($active.html());
                    } else {
                        self.dropdown.find('a').first().removeClass('active');
                        setDropdownDefaultText(collection);
                    }
                    handleFlexbox();
                    collection = [];
                    checkOffsetAndPush(true);
                } else {
                    if (!recursion) {
                        self.dropdown.addClass('d-none');
                    }
                }
            }

            self.element.append(self.dropdown.find('li'));
            checkOffsetAndPush();
        }
    };

    $.fn.tabdrop = function (option) {
        return this.each(function () {
            var $this = $(this),
                data = $this.data('tabdrop'),
                options = typeof option === 'object' && option;
            if (!data) {
                options = $.extend({}, $.fn.tabdrop.defaults, options);
                data = new TabDrop(this, options);
                $this.data('tabdrop', data);
            }
            if (typeof option == 'string') {
                data[option]();
            }
        })
    };

    $.fn.tabdrop.defaults = {
        text: '<i class="glyphicon glyphicon-menu-hamburger"></i>',
        offsetTop: 0
    };

    $.fn.tabdrop.Constructor = TabDrop;

}(window.jQuery);
