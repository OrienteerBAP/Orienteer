
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
    this.filterInput = {};

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
 * object - save filter input
 */
OFilterPanel.prototype.filterInput = null;

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

OFilterPanel.prototype.saveFilterInput = function (inputIds) {
    if (inputIds != null && inputIds.length > 0) {
        for (var i = 0; i < inputIds.length; i++) {
            this.filterInput[inputIds[i]] = $('#' + inputIds[i]).val();
        }
    }
};

OFilterPanel.prototype.restoreFilterInput = function () {
    for (var id in this.filterInput) {
        $('#' + id).val(this.filterInput[id]);
    }
    this.filterInput = {};
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

function removeFilter(containerId) {
    filtersOnPage[containerId] = undefined;
}

function saveInput(containerId, inputIds) {
    var filter = filtersOnPage[containerId];
    if (filter != null) filter.saveFilterInput(inputIds);
}

function restoreInput(containerId) {
    var filter = filtersOnPage[containerId];
    if (filter != null) filter.restoreFilterInput();
}
