	//debugger;
	var inputCheck = new Boolean();
	var positionUp = new Boolean();
    var avoidToogle = new Boolean();
	
	
	var type, width, height, input, fC, 
	charr, fCHeight, remoteContainer, url, remoteStatus, 
	body, body, logoX, logoY, logoHeight, logoWidth, actionToggle;
    
    var initInputY;
	
	
	/*setInterval( "takeFooterDown();", 100);

	function takeFooterDown(){		
		var footer = document.getElementById("fc-container");
		footer.style.padding-bottom = "0px"; 	
	}*/
	
	function getInputText(){
		var text = document.forms["urlform"].elements["box"].value;
		if (text.length){
			pBridge.getText(text);
		} else {
			pBridge.getText("");
		}	
	}       
 
	
    window.addEventListener ("load", function () {	
		
        fC = document.getElementById("fc-container");
        
        var input = document.getElementById("pk-input");

        initInputY = findPosY(input);						
        
	}, false);
	
	function bodyLoaded(){
		var inputArray = new Array(4);			
		var input = document.getElementById("pk-input");		
		inputArray[0] =  findPosX(input);
		inputArray[1] =  findPosY(input);
		inputArray[2] =  screen.width;
		inputArray[3] =  screen.width;			
		pBridge.getJavascriptVariables(inputArray);		
	}
	
	function loadData(){	
		
	}		
	
	function currentSearch(){
		var theUrl = document.forms["urlform"].elements["box"].value;
		pBridge.currentSearch(theUrl);			
		document.forms["urlform"].elements["box"].value="";
		type="";
		theUrl="";
		return true;
	}	

	function setInputPortrait(){
	
		var input = document.getElementById("input-container");
		input.style.top = "120px";
		
		input.style.width =	"90%";		
		input.style.left =	"5%";	
		input.style.right =	"5%";		
		
		var panel = document.getElementById("panel-container");
		panel.style.top = "220px";
		
		var panelLink = document.getElementById("panel-link");
		panelLink.style.width =	"90%";		
		panelLink.style.left =	"5%";	
		panelLink.style.right =	"5%";
		
	}
	
	function setInputLandscape(){	
	
		var input = document.getElementById("input-container");
		input.style.top = "80px";
		
		input.style.width =	"60%";		
		input.style.left =	"20%";	
		input.style.right =	"20%";					
						
		
		var panel = document.getElementById("panel-container");
		panel.style.top = "130px";
		
		var panelLink = document.getElementById("panel-link");
		panel.style.top = "220px";
		panelLink.style.width =	"60%";		
		panelLink.style.left =	"20%";	
		panelLink.style.right =	"20%";			
		
	}
	
	function sendType(t){
		type = t;
		document.forms["urlform"].elements["box"].focus();
	}

	function handleKeyPress(event){		
	
		var inputTextArray = new Array(3);		
		var keyCode = event.keyCode;				
		var letter = String.fromCharCode(keyCode);
		var value = document.getElementById("pk-input").value;	
		
		inputTextArray[0] = keyCode;		
		inputTextArray[1] = letter;
		inputTextArray[2] = value;		
		
		//toogleFCContainer();
		
		pBridge.getKeyboardInput(inputTextArray);
		
	}
	
	function writeMemUse(mem){
		pBridge.getText(mem);
		document.getElementById("mem-use").value = mem;
		
	}
	
	/*var charNumber = event.keyCode;		
		var keynum = event.which;
		var keychar = String.fromCharCode(keynum);
		var word = document.getElementById("pk-input").value;*/
	
	/*function handleFocus(event){		
		if (inputCheck){
            toogleFCContainer();
        }    
	}
    
    function handleFocusLost(event){
		if (inputCheck){
			toogleFCContainer();						
		}
	} */ 
	
    function toogleFCContainer(){ 

		var fC = document.getElementById("fc-container");			
		var ver = fC.style.display;
		var remoteContainer = document.getElementById("remote-container");	 
		var feedbackContainer = document.getElementById("feedback-container");		
		
		if ( fC.style.display != "none") {		
			
			fC.style.display = "none"; 	
			fCHeight = fC.style.height;				
			fC.style.height = 0; 
			
			var input_container = document.getElementById("input-container");
			input_container.style.top = '30px';
			
			var panel_container = document.getElementById("panel-container");				
			panel_container.style.display = "none"; 
			
			var body_container = document.getElementById("body-container");				
			body_container.style.display = "none"; 
						 
			remoteContainer.style.display = "none"; 
			feedbackContainer.style.display = "none"; 			
			
			pBridge.fCContVisibility("hidden");		
			
			var footer_container = document.getElementById("footer-container");	
			footer_container.style.bottom = '0px';	

			inputCheck = false;					
			
		}
		
		else {
		
			fC.style.display = "block";	
			fC.style.height = fCHeight; 

			var input_container = document.getElementById("input-container");
			input_container.style.top = '120px';
			
			var panel_container = document.getElementById("panel-container");				
			panel_container.style.display = "block"; 			
			
			var body_container = document.getElementById("body-container");				
			body_container.style.display = "block"; 	
			
			remoteContainer.style.display = "block"; 			
			feedbackContainer.style.display = "block"; 			
			
			var input = document.getElementById("pk-input");		
			input.blur();			

			inputCheck = true;			
		
			pBridge.fCContVisibility("visible");
		}	
	
    }
	
	function hideFooter(){
	
		pBridge.flat("visible");
			
		//var footer_container = document.getElementById("footer-container");			
		//footer_container.style.display = "none"; 
		
		var feedback_container = document.getElementById("feedback-container");			
		feedback_container.style.display = "none"; 
				
		//REMOTE		
		var remote_container = document.getElementById("remote-container");			
		remote_container.style.display = "none"; 
		
		var remote_control = document.getElementById("remote-control");			
		remote_control.style.display = "none"; 
		
		var server_status = document.getElementById("server-status");			
		server_status.style.display = "none"; 
		
		var client_status = document.getElementById("client-status");			
		client_status.style.display = "none"; 
		
		
	}
	
	function checkInputPosition() {	
		var input_container = document.getElementById("input-container");	
		if ( input_container.style.top == "30px" && inputCheck == false ) {			
			var input = document.getElementById("pk-input");		
			pBridge.inputTouch(findPosX(input), findPosY(input));			
			inputCheck = true;	
			positionUp = true;				
		} else {
			positionUp = false;								
		}
	}

	function setInputFocus(){
		document.getElementById("pk-input").focus();
	}

	function setInputKey(key){
		document.getElementById("pk-input").SendKeys("{"+key+"}");
	}
	
	// fires every half a second
	setInterval(checkInputPosition, 500);
	
	function clearInput(){
		document.forms["urlform"].elements["box"].value="";
	}			
				
	function switchPanel (pText, n) {
		
		var back;
		var borderColor;
		var tab = parseInt(n);
		
		switch(tab) {		
			case -1:
			  back = "-webkit-gradient(linear, left top, left bottom, color-stop(0%,rgba(5,171,224,1)), color-stop(53%,rgba(194,229,239,1)), color-stop(100%,rgba(5,171,224,1)))";
			  borderColor = "#05abe0";
			  break;
			  
			case 0:
			  back = "-webkit-gradient(linear, left top, left bottom, color-stop(0%,rgba(143,212,0,1)), color-stop(51%,rgba(226,255,214,1)), color-stop(100%,rgba(143,212,0,1)))";
			  borderColor = "#8fd400";
			  break;
			case 1:
			  back = "-webkit-gradient(linear, left top, left bottom, color-stop(0%,rgba(244,170,0,1)), color-stop(50%,rgba(242,236,222,1)), color-stop(100%,rgba(244,170,0,1)))";
			  borderColor = "#f4aa00";
			  break;
			case 2:
			  back = "-webkit-gradient(linear, left top, left bottom, color-stop(1%,rgba(28,41,167,1)), color-stop(51%,rgba(212,216,244,1)), color-stop(100%,rgba(28,41,167,1)))";
			  borderColor = "#1c29a7";
			  break;
			case 3:
			  back = "-webkit-gradient(linear, left top, left bottom, color-stop(2%,rgba(0,133,207,1)), color-stop(52%,rgba(209,231,255,1)), color-stop(99%,rgba(0,133,207,1)))";
			  borderColor = "#00aee7";
			  break;
			case 4:
			  back = "-webkit-gradient(linear, left top, left bottom, color-stop(2%,rgba(113,127,129,1)), color-stop(53%,rgba(229,229,229,1)), color-stop(99%,rgba(113,127,129,1)))";
			  borderColor = "#717f81";
			  break;		
		}
					
		var panelText = document.getElementById("panel-text");		
		panelText.innerHTML = pText; 			
		
		var panelLink = document.getElementById("panel-link");
		panelLink.style.borderColor = borderColor;
		panelLink.style.background = back;		
		
		var panelDiv = document.getElementById("panel-div");
		panelDiv.style.borderColor = borderColor;
		
		
		var passed = new Array(2);
		passed[0] = pText;
		passed[1] = bColor;
		
		pBridge.passedData(passed);
		
		
	}
	
	function clientConected(ipAddress){	
		var clientStatus = document.getElementById("client-status");
		clientStatus.innerHTML = "connected with " + ipAddress;		 		
		clientStatus.style.color = "green";
	}
	
	function clientDisconnected(){
		var clientStatus = document.getElementById("client-status");
		clientStatus.display = "none";
	}
	
	function startServer() {
		var serverStatus = document.getElementById("server-status");
		serverStatus.innerHTML  = "server started";
		serverStatus.style.color = "green";
	}
	
	function stopServer() {
		var serverStatus = document.getElementById("server-status");
		serverStatus.innerHTML = "server stopped";
		serverStatus.style.color = "red";
	}
		

	/**ABSOLUTE POSITION OF ELEMENT**/
	function findPosX(obj){
		var curleft = 0;
		if(obj.offsetParent)
			while(1) 
			{
			  curleft += obj.offsetLeft;
			  if(!obj.offsetParent)
				break;
			  obj = obj.offsetParent;
			}
		else if(obj.x)
			curleft += obj.x;
		return curleft;
	}

  function findPosY(obj){
    var curtop = 0;
    if(obj.offsetParent)
        while(1){
          curtop += obj.offsetTop;
          if(!obj.offsetParent)
            break;
          obj = obj.offsetParent;
        }
    else if(obj.y)
        curtop += obj.y;
    return curtop;
  } 

  function drawCanvas(X, Y, W, H, R){
  
		var my = new Array(5);
		my[0] = X;
		my[1] = Y;
		my[2] = W;
		my[3] = H;
		my[4] = R;
		
		pBridge.what(my);	
  
		var c = document.createElement("canvas");
		document.body.appendChild(c);
		c.style.position = "absolute";
		c.style.left="0px";
		c.style.top="0px";
		c.style.zIndex="100";
		c.style.width="100%";
		c.style.height="100%"; 		
		c.width = c.offsetWidth;
		c.height = c.offsetHeight;
		
		var context = c.getContext("2d");		
		
		context.fillStyle = "rgba(0, 0, 0, .2)";
		context.fillRect(0,0,c.width,c.height);
		context.fill();
		
		context.globalCompositeOperation = "destination-out";	

		/*var x 		= parseInt('100');
		var y 		= parseInt('100');
		var width 	= parseInt('150');
		var height 	= parseInt('25');
		var radius 	= parseInt('20');*/		
		
		var x 		= parseInt(X);
		var y 		= parseInt(Y);
		var width 	= parseInt(W);
		var height 	= parseInt(H);
		var radius 	= parseInt(R);
		
		context.fillStyle = "rgba(0, 0, 0, 1)";
		context.beginPath();
		context.moveTo(x + radius, y);
		context.lineTo(x + width - radius, y);
		context.quadraticCurveTo(x + width, y, x + width, y + radius);
		context.lineTo(x + width, y + height - radius);
		context.quadraticCurveTo(x + width, y + height, x + width - radius, y + height);
		context.lineTo(x + radius, y + height);
		context.quadraticCurveTo(x, y + height, x, y + height - radius);
		context.lineTo(x, y + radius);
		context.quadraticCurveTo(x, y, x + radius, y);
		context.closePath();		
		context.fill();
		
		
}
  
  