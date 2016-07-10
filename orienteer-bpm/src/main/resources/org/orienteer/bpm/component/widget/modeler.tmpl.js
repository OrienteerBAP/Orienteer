var BpmnViewer = window.BpmnJS.Viewer;
var BpmnModeler = window.BpmnJS;

//var viewer = new BpmnViewer({ container: '#${componentId}', height: 600 });
var viewer = new BpmnModeler({ container: '#${componentId}'});
var xml = ${xml};

viewer.importXML(xml, function(err) {

  if (err) {
    console.log('error rendering', err);
  } else {
    console.log('rendered');
  }
});