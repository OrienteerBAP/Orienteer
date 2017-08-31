
var currentZindex = 1000;
var maxZindex     = currentZindex + 100000;

var filtersOnPage = {};

/**
 * Class which represents draggable filter bubble
 */
var OFilterPanel = function (containerId, currentTabId) {
    this.containerId = containerId;
    this.currentTabId = currentTabId;
    this.currentPosition = {};

    if (this.containerId != null) this.init()
};

/**
 * string - contains panel id
 */
OFilterPanel.prototype.containerId = null;

/**
 * string - container current active tab id. See <a href="https://getbootstrap.com/docs/3.3/javascript/#tabs">Bootstrap tab</a>
 */
OFilterPanel.prototype.currentTabId = null;

/**
 * object - contains offset position of filter panel left and top
 */
OFilterPanel.prototype.currentPosition = null;

/**
 * Init filter panel.
 */
OFilterPanel.prototype.init = function () {
    var position = this.currentPosition;
    var draggable = $('#' + this.containerId).draggable({
        cursor: "move",
        containment: "document",
        zIndex: maxZindex,
        stop: function(event, ui) {
            if (currentZindex === maxZindex) currentZindex = 1000;
            else currentZindex++;
            draggable.css("z-index", currentZindex);
        },
        drag: function (event, ui) {
            position.left = ui.offset.left;
            position.top = ui.offset.top;
        }
    });
    if (this.currentPosition.left == null || this.currentPosition.top == null) {
        var currentPosition = $(window).width() - draggable.width();
        if (!(currentPosition > draggable.offset().left)) {
            draggable.offset({
                left: currentPosition - 15
            });
        }
    } else {
        draggable.offset(this.currentPosition);
    }

    if (this.currentTabId != null) this.showCurrentTab();
};

OFilterPanel.prototype.setCurrentTabAndShow = function (currentTabId) {
    this.currentTabId = currentTabId;
    this.showCurrentTab();
};

OFilterPanel.prototype.showCurrentTab = function () {
    $("#" + this.currentTabId).tab('show');
};

function initFilter(containerId, currentTabId) {
    var filter = filtersOnPage[containerId];
    if (filter == null) {
        filtersOnPage[containerId] = new OFilterPanel(containerId, currentTabId);
    } else {
        filter.currentTabId = currentTabId;
        filter.init();
    }
}

function switchFilterTab(containerId, tabId) {
    var filter = filtersOnPage[containerId];
    if (filter != null) filter.setCurrentTabAndShow(tabId);
}

function saveFilterPosition(containerId) {
    var filter = filtersOnPage[containerId];
    if (filter != null) filter.saveCurrentPosition();
}

function removeFilter(containerId) {
    filtersOnPage[containerId] = undefined;
}
