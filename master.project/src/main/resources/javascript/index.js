/**
 * bpmn-js-seed
 *
 * This is an example script that loads an embedded diagram file <diagramXML>
 * and opens it using the bpmn-js viewer.
 */

  var diagramWidthForView = null;
  var diagramHeightForView = null;


 function sayHello(msg) {
	alert(msg);
 };

 function removeBpmnDiagram(){
		$('#canvas').html('');
 }

 function showBpmnDiagramSize(){
 	  alert("width: "+this._box.width);
 	  //alert("width: "+canvas.viewbox().width+" height: "+canvas.viewbox().height+" x: "+canvas.viewbox().x+" y: "+canvas.viewbox().y);

 }

 var bpmnViewer;
function showBpmnDiagram(filePath){
  // import function
  function importXML(xml) {

	// create viewer
	var BpmnViewer = window.BpmnJS;
	bpmnViewer = new BpmnViewer({
		container: '#canvas'
	});

	// import diagram
    bpmnViewer.importXML(xml, function(err) {

      if (err) {
        return console.error('could not import BPMN 2.0 diagram', err);
      }

      var canvas = bpmnViewer.get('canvas'),
          overlays = bpmnViewer.get('overlays'),
		  eventBus = bpmnViewer.get('eventBus');

      // set zoom value
      canvas.zoom(1.0);

	  // box covers the diagram, when the zoom factor is 1.0
	  var box = canvas.viewbox().inner;

	  //$('#show_size').html('x: '+box.x+'; y: '+box.y+'; width: '+box.width+'; height: '+box.height);
	  diagramWidthForView = box.width;
	  diagramHeightForView = box.height;

	  // bridgeSize is a bridgeSize class to the JavaFX application and is created by this application
	  if(typeof(bridgeSize) !== "undefined") {bridgeSize.set(box.x, box.y, box.width, box.height);}

	  var markNewLT = null, markNewLB = null, markNewRT = null, markNewRB = null;
	  var markOldLT = null, markOldLB = null, markOldRT = null, markOldRB = null;

	  // event handler
	  eventBus.on('element.click', function(e) {
	    // write selected element.id into html document
		$('#show_id').html(e.element.id);
		// bridge is a bridge class to the JavaFX application and is created by this application
		if(typeof(bridgeObjID) !== "undefined") {bridgeObjID.setObjectID(e.element.id);}

		// elemente markieren begin
		markOldLT = markNewLT;
		markOldLB = markNewLB;
		markOldRT = markNewRT;
		markOldRB = markNewRB;
		if(markOldLT != null) { overlays.remove(markOldLT); }
		if(markOldLB != null) { overlays.remove(markOldLB); }
		if(markOldRT != null) { overlays.remove(markOldRT); }
		if(markOldRB != null) { overlays.remove(markOldRB); }
        markNewLT = overlays.add(e.element.id, 'note', {
          position: { top: 0, left: 0 },
          html: '<div style="width: 10px; height: 10px; background: red;"></div>'
        });
        markNewLB = overlays.add(e.element.id, 'note', {
          position: { bottom: 0, left: 0 },
          html: '<div style="width: 10px; height: 10px; background: red;"></div>'
        });
        markNewRT = overlays.add(e.element.id, 'note', {
          position: { top: 0, right: 0 },
          html: '<div style="width: 10px; height: 10px; background: red;"></div>'
        });
        markNewRB = overlays.add(e.element.id, 'note', {
          position: { bottom: 0, right: 0 },
          html: '<div style="width: 10px; height: 10px; background: red;"></div>'
        });
		// elemente markieren end
	  });
    });
  }

  // load external bpmn diagram file via AJAX and import it
  $.get(filePath, importXML, 'text');
};

