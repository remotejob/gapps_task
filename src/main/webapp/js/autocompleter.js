$(document).ready(function() {
        $(function() {
                $("#search").autocomplete({ minLength: 3,    
                source : function(request, response) {
                $.ajax({
                        url : "searchcontroller",
                        type : "GET",
                        data : {
                                term : request.term
                        },
                        dataType : "json",
                        success : function(data) {                        	
                                response(data);
                        }
                });
        }
});
});
});

function getPreviewPageAsync(url) {
	
	var dfilename = document.getElementById("search").value;
	
	console.log(dfilename)
	
	var xhr = new XMLHttpRequest();
		
	xhr.open('GET', url);
	
	xhr.setRequestHeader('X-Dfilename', dfilename);
	
	xhr.onload = function(){
		if (xhr.status === 200) {
		        var myFile = JSON.parse(xhr.responseText);
		        
		        document.getElementById("id").innerHTML = myFile.id;
		        document.getElementById("name").innerHTML = myFile.name;
		        document.getElementById("mimetype").innerHTML = myFile.mimetype;
		        
		        if (myFile.mimetype ==="image/jpeg") {
		        	
		        	 document.getElementById("showimage").innerHTML = "<img src='https://docs.google.com/uc?id="+myFile.id+"'  class='img-rounded' style='width:304px;height:228px;'>"; 
		        	
		        }
		        
		    
		}
	
	}
	xhr.onerror = function() {
	    
	    console.log("Something wrong!! in getPreviewPageAsync");
	}
	
	xhr.send();
	
	
};