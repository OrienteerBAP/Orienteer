
var messageCounter = 0;

/**
 * Class which represents message for {@link OArchitectEditor}
 * @param message - string with message
 * @param error - boolean true is need to show error message. Default false
 * @param msDuration - duration in milliseconds which represents how much message will be show. Default 3000
 * @constructor
 */
var OArchitectMessage = function (message, error, msDuration) {
    this.message = message;
    this.error = error != null ? error : false;
    this.msDuration = msDuration != null ? msDuration : 3000;
    this.markupId = null;
};

/**
 * @type string - contains message which will be displayed for user
 */
OArchitectMessage.prototype.message = null;

/**
 * @type boolean - true if need to show error message. Default false
 */
OArchitectMessage.prototype.error = false;

/**
 * @type number - contains duration in milliseconds which represents how much message will be show. Default 3000
 */
OArchitectMessage.prototype.msDuration = 3000;

/**
 * @type string - contains markupId for message. Generated automatically
 */
OArchitectMessage.prototype.markupId = null;

/**
 * Show message
 */
OArchitectMessage.prototype.show = function () {
    var message = this;
    var container = message.createContainer();
    $('#' + app.editorId).append(container);
    var containerJquery = $('#' + this.markupId).hide();
    containerJquery.show(1000);
    setTimeout(function () {
        containerJquery.hide(1000, function () {
            containerJquery.remove();
        });
        messageCounter--;
        message.markupId = null;
    }, this.msDuration);
};

OArchitectMessage.prototype.createContainer = function () {
    var div = document.createElement('div');
    this.markupId = 'architectMessage' + messageCounter++;
    div.setAttribute('id', this.markupId);
    div.classList.add(OArchitectConstants.ALERT);
    div.classList.add(this.error ? OArchitectConstants.ALERT_DANGER : OArchitectConstants.ALERT_INFO);
    div.classList.add(OArchitectConstants.MESSAGE_CLASS);
    div.innerHTML = this.message;
    div.style.zIndex = '10000';
    return div;
};