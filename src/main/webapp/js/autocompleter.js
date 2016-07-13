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
		        console.log()
		    }
		}
	
	
	xhr.onerror = function() {
	    displayError("Unable to load RSS");
	}
	
	xhr.send();
	
};