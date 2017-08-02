
var modalWindowCounter = 0;

var SchemeEditorModalWindow = function (value, containerId) {
    this.CANCEL = 'CANCEL';
    this.OK     = 'OK';

    this.value = value;
    this.containerId = containerId;
    this.isShow = false;
    this.markupId = null;
};

SchemeEditorModalWindow.prototype.show = function (x, y) {
    if (!this.isShow) {
        console.log('modal x: ' + x);
        console.log('modal y: ' + y);
        console.log('modal markup id: ' + this.getMarkupId());
        var id = '#' + this.containerId;
        $(id).append(this.createModalElement(x, y));
        $('#' + this.getMarkupId()).draggable({
            containment: id
        });
        this.isShow = true;
    } else throw new Error('Scheme editor modal window is already show!');
};

SchemeEditorModalWindow.prototype.destroy = function (event) {
    if (this.isShow) {
        modalWindowCounter--;
        this.onDestroy(this.value, event);
        $('#' + this.getMarkupId()).remove();
        this.markupId = null;
        this.isShow = false;
    } else throw new Error('Can\'t destroy modal window, because it is not show!');
};

SchemeEditorModalWindow.prototype.onDestroy = function (value, event) {

};

SchemeEditorModalWindow.prototype.createModalElement = function (x, y) {
    var panel = document.createElement('div');
    panel.style.width = '300px';
    panel.style.position = 'absolute';
    panel.classList.add('panel');
    panel.classList.add('panel-default');
    panel.setAttribute('id', this.getMarkupId());
    panel.style.left = x + 'px';
    panel.style.top = y + 'px';
    var head = document.createElement('div');
    head.classList.add('panel-heading');
    var body = document.createElement('div');
    body.classList.add('panel-body');
    panel.appendChild(head);
    panel.appendChild(body);
    this.createContent(panel, head, body);
    return panel;
};

SchemeEditorModalWindow.prototype.getMarkupId = function () {
    if (!this.markupId) {
        this.markupId = 'schemeEditorModalWindow' + modalWindowCounter;
        modalWindowCounter++;
    }
    return this.markupId;
};

SchemeEditorModalWindow.prototype.createContent = function (panel, head, body) {
    head.innerHTML = 'No head content';
    body.innerHTML = 'No body content';
};


var createOPropertyCreateModalWindow = function (property, containerId) {
    var modal = new SchemeEditorModalWindow(property, containerId);
    modal.createContent = function (panel, head, body) {
        var select = newSelect();
        var input = newInput();
        addValueBlock(body, input, select);
        addButtonBlock(this, body, input, select);
        addHead(head);
    };

    var addValueBlock = function (body, input, select) {
        var valueBlock = newValueBlock();
        valueBlock.appendChild(newLabel(NAME + ':'));
        valueBlock.appendChild(input);
        valueBlock.appendChild(newLabel(TYPE + ':'));
        valueBlock.appendChild(select);
        body.appendChild(valueBlock);
    };

    var addButtonBlock = function (modal, body, input, select) {
        var buttonBlock = newButtonBlock();
        var okBut = newOkButton(OK, modal, input, select);
        var cancelBut = newCancelButton(CANCEL, modal);
        buttonBlock.appendChild(cancelBut);
        buttonBlock.appendChild(okBut);
        body.appendChild(buttonBlock);
    };

    var addHead = function (head) {
        head.innerHTML = 'Create OProperty';
    };

    var newButtonBlock = function () {
        var div = document.createElement('div');
        div.style.marginTop = '5px';
        return div;
    };

    var newValueBlock = function () {
        var div = document.createElement('div');
        div.style.marginBottom = '5px';
        return div;
    };

    var newSelect = function () {
        var select = document.createElement('select');
        select.classList.add('form-control');
        for (var i = 0; i < OType.size(); i++) {
            var option = document.createElement('option');
            option.setAttribute('value', OType.get(i));
            option.innerHTML = OType.get(i);
            select.appendChild(option);
        }
        return select;
    };

    //TODO: validate user input
    var newInput = function () {
        var input = document.createElement('input');
        input.classList.add('form-control');
        input.setAttribute('type', 'text');
        return input;
    };

    var newOkButton = function (label, modal, nameField, typeSelect) {
        var button = newButton(label, 'btn-primary');
        button.addEventListener('click', function () {
            console.log('click ok');
            console.log('field value: ' + nameField.value);
            console.log('select value: ' + typeSelect.options[typeSelect.selectedIndex].value);
            if (nameField.value.length > 0) {
                modal.value.setName(nameField.value);
                modal.value.setType(typeSelect.options[typeSelect.selectedIndex].value);
                modal.destroy(modal.OK);
            }
        });
        button.style.float = 'right';
        return button;
    };

    var newCancelButton = function (label, modal) {
       var button = newButton(label, 'btn-danger');
       button.style.float = 'left';
       button.addEventListener('click', function () {
          modal.destroy(modal.CANCEL);
       });
       return button;
    };

    var newButton = function (label, typeCssClass) {
        var but = document.createElement('a');
        but.classList.add('btn');
        but.classList.add(typeCssClass);
        but.classList.add('btn-sm');
        but.innerHTML = label;
        return but;
    };

    var newLabel = function (label) {
        var element = document.createElement('label');
        element.innerHTML = label;
        return element;
    };

    return modal;
};