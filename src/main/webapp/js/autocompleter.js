$(document).ready(function() {
        $(function() {
                $("#search").autocomplete({     
                source : function(request, response) {
                $.ajax({
                        url : "searchcontroller",
                        type : "GET",
                        data : {
                                term : request.term
                        },
                        dataType : "json",
                        success : function(data) {
                        	console.log(data)
                                response(data);
                        }
                });
        }
});
});
});