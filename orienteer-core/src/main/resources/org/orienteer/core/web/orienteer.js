'use strict';

(function () {
    var SCREEN_SMALL       = 576;
    var SCREEN_MEDIUM      = 768;
    var SCREEN_LARGE       = 992;
    var SCREEN_EXTRA_LARGE = 1200;

    var previousSize;

    $(function() {
        handleOrienteerUI();
        $(window).resize(handleOrienteerUI);
    });

    function handleOrienteerUI() {
        var width = $(window).width();
        var func = handler(width);
        if (func) {
            var ui = new OrienteerUI();
            func(ui);
        }

        function handler(width) {
            var res;
            if (width <= SCREEN_SMALL) {
                res = handlerFromPrevious(smallHandler, SCREEN_SMALL);
            } else if (width <= SCREEN_MEDIUM) {
                res = handlerFromPrevious(mediumHandler, SCREEN_MEDIUM);
            } else if (width <= SCREEN_LARGE) {
                res = handlerFromPrevious(largeHandler, SCREEN_LARGE);
            } else res = handlerFromPrevious(extraLargeHandler, SCREEN_EXTRA_LARGE);

            return res;
        }

        function handlerFromPrevious(f, size) {
            if (previousSize !== size) {
                previousSize = size;
                return f;
            }
            return null;
        }
    }

    function smallHandler(ui) {
        var header = ui.getHeader();
        var content = ui.getMainContent();
        header.sideBarToggler.addClass('mr-auto');
        header.brand.addClass('mx-auto');
        content.data.addClass('px-2');
    }

    function mediumHandler(ui) {
        console.log('mediumHandler');
    }

    function largeHandler(ui) {
        console.log('largeHandler');
    }

    function extraLargeHandler(ui) {
        console.log('extraLargeHandler');
    }

    function OrienteerUI() {


        this.getDashBoard = function () {
            return $('')
        };

        this.getMainContent = function () {
            var content = this;
            var mainContent = $('.main>div');
            content.header = mainContent.first();
            content.data = mainContent.last();
            return content;
        };

        this.getHeader = function () {
            var header = this;
            var container = $('header');
            header.sideBarToggler = container.find('.mobile-sidebar-toggler');
            header.brand = container.find('.navbar-brand');
            header.orienteerMenu = container.find('ul').last();
            return header;
        };

    }
})();