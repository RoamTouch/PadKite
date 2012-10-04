<!doctype html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>Canvas Resize  Demo</title>
<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.3.2/jquery.min.js"></script>
<script type="text/javascript">
	
	//debugger;
	
	var connection, 
	users = {}, 
	mouseX = 0, 
	mouseY = 0, 
	oldMouseX = 0, 
	oldMouseY = 0, 
	mouseDown = false, 
	mouseUp = false,			
	panning = false, 
	mouseXOnPan = 0, 
	mouseYOnPan = 0, 
	canvasXOnPan = 0, 
	canvasYOnPan = 0,
	commands = [], 
	messagesArray = [], 
	lastMessage, 
	connected = false,			
	consoleLog,
	theCanvas,
	context;
	
	var SCREEN_WIDTH = window.innerWidth,
	SCREEN_HEIGHT = window.innerHeight;		

	var COMMAND 		= 0,
	COMMAND_OPENED	 	= 1, 
	COMMAND_MOUSEDOWN 	= 2,
	COMMAND_POSITION 	= 3, 			 
	COMMAND_MOUSEUP	 	= 4, 
	COMMAND_MOUSEDOUBLE	= 5,
	COMMAND_MESSAGE_URL	= 6; 	
	COMMAND_MESSAGE_KEY = 7; 	
	
	var proportion;

window.addEventListener('load', eventWindowLoaded, false);	

function eventWindowLoaded() {

	
	
	canvasApp();
}


function canvasApp() {
		
	theCanvas = document.getElementById('canvasOne');
	if (!theCanvas || !theCanvas.getContext) { 
		return;	
	}
	context = theCanvas.getContext('2d'); 	
	//theCanvas.width = 50;
	//theCanvas.height = 70;		
	//drawScreen();
	
	theCanvas.addEventListener('selectstart', function(e) { 
		e.preventDefault(); return false; 
	}, false);
	
	//http://stackoverflow.com/questions/1067464/need-to-cancel-click-mouseup-events-when-double-click-event-detected/1067484#1067484
	
	//MOUSEDOWN
	theCanvas.addEventListener( 'mousedown', function(e) {	
	
		if ( mouseDown == false ){										
			event.preventDefault();			
			mouseDown = true;				
			mouseUp = false;
			panning = true;
			mouseX = event.clientX - theCanvas.offsetLeft;
			mouseY = event.clientY - theCanvas.offsetTop;
			commands.push( COMMAND_MOUSEDOWN, mouseX.toString(), mouseY.toString() );					
				//writeToScreen("single");
		}	
				
	}, false);	
	
	//MOUSEMOVE
	theCanvas.addEventListener( 'mousemove', function(e) {
	
		if ( mouseDown == true && mouseUp == false && panning == true ){	
			theCanvas.style.cursor = "crosshair";											
			oldMouseX = mouseX;
			oldMouseY = mouseY;
			mouseX = event.clientX - theCanvas.offsetLeft;
			mouseY = event.clientY - theCanvas.offsetTop;				
			if ( !commands.length ) {
				commands.push( COMMAND_POSITION, mouseX.toString(), mouseY.toString() );
			} else if ( mouseDown ) {
				var deltaX = mouseX - oldMouseX;
				var deltaY = mouseY - oldMouseY;
				deltaX = deltaX == 0 ? "" : deltaX;
				deltaY = deltaY == 0 ? "" : deltaY;
				commands.push( COMMAND_POSITION, deltaX.toString(), deltaY.toString() );
			}	
		}
	
	}, true);				
	
	//MOUSEUP
	theCanvas.addEventListener('mouseup', function(e) {
		if ( mouseDown == true ){	
			panning = false;				
			mouseUp = true;
			mouseDown = false;
			commands.push( COMMAND_MOUSEUP, 0 );	
		}		
	}, true);
	
	//MOUSEOUT
	theCanvas.addEventListener( 'mouseout', function(e){
	
		if ( mouseDown == true ){	
			panning = false;				
			mouseUp = true;
			mouseDown = false;
			commands.push( COMMAND_MOUSEUP, 0 );				
		}
		
	}, true);	
	
	//DOUBLECLICK	
	theCanvas.addEventListener('dblclick', function(e) {
		commands.push( COMMAND_MOUSEDOUBLE, 0 );		
	}, true);
	
	/**END OF EVENTS**/
	
	 consoleLog = document.getElementById("consoleLog");
	
	var formElement = document.getElementById("canvasWidth")
	formElement.addEventListener('change', canvasWidthChanged, false);		

	drawScreen();			
}

		function connectSocket() {   
					
				if ( window["WebSocket"] ) {
				
					logToConsole("<span style='color: gray;'><strong>Info:</strong> Connecting...</span>");					
					
					mouseDown = false;
					mouseUp = false;			
					panning = false;

					var url = document.getElementById('uri').value;			
					connection	= new WebSocket("ws://"+url+"/");	

					connection.onclose = function( event ) {		
						theCanvas.width = 50;
						theCanvas.height = 100;							
						connected=false;
					}
					
					connection.onopen = function( event ) {		
						commands.push( COMMAND_OPENED, 0 );		
						logToConsole("<span style='color: green;'>Connected</span>");		
						connected=true;
					}
					
					connection.onclose = function( event ) {
						logToConsole("<span style='color: red; text-align:center;'>Disconnected, device not responding.<br>Please start the server on the landing page link and try again.</span>");								
						connected=false;
					}
					
					connection.onmessage = function( event ) {
						
						if (event.data.contains("size")) {
							var dataArray = event.data.split( ':' );
							var dataLength = dataArray.length;							
							var height = dataArray[ 1 ];
							var width = dataArray[ 2 ];			
							proportion = height/width;
							theCanvas.width = width;
							theCanvas.height = height;									
							drawScreen();
							logToConsole("<span style='color: black;'> height: " + height + " width: " + width + "</span>");	
						}	
					}							
				
				} else if (!window.WebSocket) {
					logToConsole('<span style="color: red;"><strong>Error:</strong> This browser does not have support for WebSocket</span>');
					return;
				}
			}	
	
			function disconnect(){
				connection.close();
				logToConsole("<span style='color: green;'>Disconnected</span>");		
			}		
	
			setInterval( broadcast, 50 );	
			
			function broadcast() {
			
				if (connected){
				
					if ( !commands.length || connection.readyState != 1 /*WebSocket.OPEN*/ ) {
						return;
					}
		
					switch(commands[0])
					{
						case COMMAND_OPENED:
							var ipaddress = "<? echo $_SERVER['REMOTE_ADDR']; ?>";
							connection.send( "opened:" + ipaddress );
							logToConsole("<span style='color: black;'>Connection Opened at " + ip + " .</span>");	
							break;
							
						case COMMAND_MOUSEDOWN:
							if ( mouseDown == true && mouseUp == false ){
								connection.send( "down:" + commands[1] + ":" + commands[2] );
								logToConsole("<span style='color: black;'>Mouse down at: " + commands[1] + ":" + commands[2] + "</span>");								
							}
							break;
							
						case COMMAND_POSITION:
							if ( mouseDown == true && mouseUp == false ){
								connection.send( commands[1] + ":" + commands[2] );
								//logToConsole("<span style='color: black;'>" + commands[1] + ":" + commands[2] + "</span>");								
							}
							break;
							
						case COMMAND_MOUSEUP:
							connection.send( "up" );
							logToConsole("<span style='color: black;'>Mouse up At:" + commands[1] + ":" + commands[2] + "</span>");							
							break;
							
						case COMMAND_MOUSEDOUBLE:
							connection.send( "double" );
							logToConsole("<span style='color: black;'>Double click detected, opeining Circular Menu...</span>");								
							break;	
							
						case COMMAND_MESSAGE_URL:
							connection.send( "message_url:" + commands[1] );
							logToConsole("<span style='color: black;'>Loading web page" + commands[1] + "...</span>");									
							break;	
						
						case COMMAND_MESSAGE_KEY:
							connection.send( "message_key:" + commands[1] );
							logToConsole("<span style='color: black;'>Loading web page" + commands[1] + "...</span>");									
							break;								
					  
					}			
					commands = [];
				}
			}			

			function sendMessageUrl( value ) {
				connection.send( COMMAND_MESSAGE_URL + ',' + value );
			}
			
			function sendMessageKey( value ) {
				connection.send( COMMAND_MESSAGE_KEY + ',' + value );
			}
			
			String.prototype.endsWith = function(str){ 
				return (this.match(str)+"$") == src;
			}	
		
			String.prototype.contains = function(it) { 
				return this.indexOf(it) != -1; 
			};	
			
			function logToConsole(message) {
			
				var pre = document.createElement("p");
				pre.style.wordWrap = "break-word";
				//pre.innerHTML = getSecureTag()+message;
				pre.innerHTML = message;
				consoleLog.appendChild(pre);

				while (consoleLog.childNodes.length > 50)
				{
				  consoleLog.removeChild(consoleLog.firstChild);
				}    
				consoleLog.scrollTop = consoleLog.scrollHeight;
			}
			
			function clearLog(){
				while (consoleLog.childNodes.length > 0){
					consoleLog.removeChild(consoleLog.lastChild);
				}
			}		
	
	
		function drawScreen() {			
			
			context.fillStyle = '#dddddd';
			context.fillRect(0, 0, theCanvas.width, theCanvas.height);
			context.strokeStyle = '#000000'; 
			
			context.strokeRect(5,  5, theCanvas.width-10, theCanvas.height-10);				
		
		}
		
		function canvasWidthChanged(e) {
			var target =  e.target;		
			theCanvas.width =  target.value;
			theCanvas.height =  target.value + proportion;
			drawScreen();
		}	

		function logToConsole(message) {			
			var pre = document.createElement("p");
			//pre.style.wordWrap = "break-word";
			pre.style.wordWrap = "normal";
			//pre.innerHTML = getSecureTag()+message;
			pre.innerHTML = message;
			consoleLog.appendChild(pre);
			while (consoleLog.childNodes.length > 50){
			  consoleLog.removeChild(consoleLog.firstChild);
			}    
			consoleLog.scrollTop = consoleLog.scrollHeight;
		}
			
		function clearLog(){
			while (consoleLog.childNodes.length > 0){
				consoleLog.removeChild(consoleLog.lastChild);
			}
		}
		
		function handleKeyPress(event){	
			var keyCode = event.keyCode;
			connection.send( COMMAND_MESSAGE_KEY + ',' + value );			
		}

		function keyFocus(event){					
			document.getElementById("keyField").value = "";		
		}
		
		function urlFocus(event){					
			document.getElementById("urlField").value = "";		
		}
		
</script> 

<style type="text/css"> 
			body {
				background-color:#f0f0f0;
				margin: 0px;
				overflow: hidden;
			}					
			
			#canvas {
				cursor: crosshair;											
				color: #606060;
				background-color:#ffffff;
				border-right: 3px solid #ddd;
				float: left;				
			}	
					
			#consoleLog {
				border: solid 1px #999999;
				border-top-color: #CCCCCC;
				border-left-color: #CCCCCC;
				padding: 5px;				
				height: 30px;
				overflow-y: scroll;
			}	
	
			#log {				
				padding: 5px;				
				height: 10px;				
			}	
			
		</style>		
		
</head>
<body>

		<p>Here the adress on your PadKite landing page and connect:</p>
		
		<div>
			<div>
				<input type="text" id="uri" value="192.168.33.100:8887" style="width:200px;"> 
				<input type="submit" id="connect" value="Connect"  onClick="connectSocket()" style="width:70px;">
				<input type="button" id="disconnect" value="Disconnect" disabled="disabled" onClick="disconnect()" style="width:70px;">	
					<br>
					
				<input type="text" id="urlField" value="URL to PadKite" style="width:145px;">
				<input type="button" value="Load" onClick="sendMessageUrl(document.getElementById('urlField').value)" onfocus="urlFocus(event)">	
				<input type="text" id="keyField" value="Send key stroke" style="width:140px;" onkeypress="handleKeyPress(event)" onfocus="keyFocus(event)">				
				
				<!--http://davidbcalhoun.com/2011/implementing-iphone-slider-unlock-with-input-type--->	
				<!--<input type="range" id="canvasWidth"/> -->
				<div id="consoleLog" style="width:340px; height:150px;"></div> 	   
				
			</div>					
		</div>		  		
			
				   
		<canvas id="canvasOne" width="500" height="300"/>			

	
          


</body>
</html>




