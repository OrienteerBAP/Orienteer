
var messageCounter = 0;

/**
 * Class which represents message for {@link OArchitectEditor}
 * @param message - string with message
 * @param msDuration - duration in milliseconds which represents how much message will be show. Default 3000
 * @constructor
 */
var OArchitectMessage = function (message, msDuration) {
    this.message = message;
    this.msDuration = msDuration != null ? msDuration : 3000;
    this.markupId = null;
};

/**
 * Show message
 */
OArchitectMessage.prototype.show = function () {
    var container = this.createContainer();
    var msg = this;
    $('#' + app.editorId).append(container);
    var containerJquery = $('#' + this.markupId).hide();
    containerJquery.show(1000);
    setTimeout(function () {
        containerJquery.hide(1000, function () {
            containerJquery.remove();
        });
        messageCounter--;
        msg.markupId = null;
    }, this.msDuration);
};

OArchitectMessage.prototype.createContainer = function () {
    var div = document.createElement('div');
    this.markupId = 'architectMessage' + messageCounter++;
    div.setAttribute('id', this.markupId);
    div.classList.add(OArchitectConstants.ALERT);
    div.classList.add(OArchitectConstants.ALERT_INFO);
    div.classList.add(OArchitectConstants.MESSAGE_CLASS);
    div.innerHTML = this.message;
    div.style.zIndex = '10000';
    return div;
};