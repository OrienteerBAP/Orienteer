var BpmnViewer = window.BpmnJS.Viewer;
var BpmnModeler = window.BpmnJS;


var container = $('#${componentId} .modeler');

var canvas = $('#${componentId} .canvas');

var xmlField = $('#${xmlFieldComponentId}');

var canEdit = ${canEdit};

//var viewer = new BpmnViewer({ container: '#${componentId}', height: 600 });
var modeler = canEdit ? new BpmnModeler({ container: canvas}) : new BpmnViewer({ container: canvas});
var xml = xmlField.val();

var newDiagram = '<?xml version="1.0" encoding="UTF-8"?>'+
'<bpmn2:definitions xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:bpmn2="http://www.omg.org/spec/BPMN/20100524/MODEL" xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI" xmlns:dc="http://www.omg.org/spec/DD/20100524/DC" xmlns:di="http://www.omg.org/spec/DD/20100524/DI" xsi:schemaLocation="http://www.omg.org/spec/BPMN/20100524/MODEL BPMN20.xsd" id="sample-diagram" targetNamespace="http://bpmn.io/schema/bpmn">'+
'  <bpmn2:process id="Process_1" isExecutable="false"><bpmn2:startEvent id="StartEvent_1"/></bpmn2:process>'+
'  <bpmndi:BPMNDiagram id="BPMNDiagram_1">'+
'    <bpmndi:BPMNPlane id="BPMNPlane_1" bpmnElement="Process_1">'+
'      <bpmndi:BPMNShape id="_BPMNShape_StartEvent_2" bpmnElement="StartEvent_1">'+
'        <dc:Bounds height="36.0" width="36.0" x="412.0" y="240.0"/>'+
'      </bpmndi:BPMNShape>'+
'    </bpmndi:BPMNPlane>'+
'  </bpmndi:BPMNDiagram>'+
'</bpmn2:definitions>';

if(xml == null || xml == '') xml = newDiagram;

function openDiagram(xml) {

  modeler.importXML(xml, function(err) {

    if (err) {
      container
        .removeClass('with-diagram')
        .addClass('with-error');

      container.find('.error pre').text(err.message);

      console.error(err);
    } else {
      container
        .removeClass('with-error')
        .addClass('with-diagram');
    }


  });
}

function saveSVG(done) {
  modeler.saveSVG(done);
}

function saveDiagram(done) {

  modeler.saveXML({ format: true }, function(err, xml) {
    done(err, xml);
  });
}



function registerFileDrop(container, callback) {

  function handleFileSelect(e) {
    e.stopPropagation();
    e.preventDefault();

    var files = e.dataTransfer.files;

    var file = files[0];

    var reader = new FileReader();

    reader.onload = function(e) {

      var xml = e.target.result;

      callback(xml);
    };

    reader.readAsText(file);
  }

  function handleDragOver(e) {
    e.stopPropagation();
    e.preventDefault();

    e.dataTransfer.dropEffect = 'copy'; // Explicitly show this is a copy.
  }

  container.get(0).addEventListener('dragover', handleDragOver, false);
  container.get(0).addEventListener('drop', handleFileSelect, false);
}

if (!window.FileList || !window.FileReader) {
  window.alert(
    'Looks like you use an older browser that does not support drag and drop. ' +
    'Try using Chrome, Firefox or the Internet Explorer > 10.');
} else {
  registerFileDrop(container, openDiagram);
}


var resetLink = $('#${componentId} .js-reset');
var downloadLink = $('#${componentId} .js-download-diagram');
var downloadSvgLink = $('#${componentId} .js-download-svg');

$('.buttons a').click(function(e) {
  if (!$(this).is('.active')) {
    e.preventDefault();
    e.stopPropagation();
  }
});

resetLink.click(function(e) {
	openDiagram(newDiagram);
	return false;
});

function setEncoded(link, name, data) {
  var encodedData = encodeURIComponent(data);

  if (data) {
    link.addClass('active').attr({
      'href': 'data:application/bpmn20-xml;charset=UTF-8,' + encodedData,
      'download': name
    });
  } else {
    link.removeClass('active');
  }
}

function debounce(func, wait, immediate) {
	var timeout;
	return function() {
		var context = this, args = arguments;
		var later = function() {
			timeout = null;
			if (!immediate) func.apply(context, args);
		};
		var callNow = immediate && !timeout;
		clearTimeout(timeout);
		timeout = setTimeout(later, wait);
		if (callNow) func.apply(context, args);
	};
};

var exportArtifacts = debounce(function() {

	saveSVG(function(err, svg) {
	  setEncoded(downloadSvgLink, 'diagram.svg', err ? null : svg);
	});

	saveDiagram(function(err, xml) {
		xmlField.val(xml);
		setEncoded(downloadLink, 'diagram.bpmn', err ? null : xml);
    });
}, 500);

modeler.on('commandStack.changed', exportArtifacts);

openDiagram(xml);
exportArtifacts();