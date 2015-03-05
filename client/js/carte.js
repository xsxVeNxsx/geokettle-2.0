var inputFormats = [];

function onTransIDChange()
{
	var trans_id = 1 * $("#trans_id option:selected").val();
	while ($("#carte_params").children().length != 0)
		$("#carte_params").children()[0].remove();
	if (trans_id == REDESIGN_TRANS)
	{
		$("#carte_params").append($('<br>'));
		$("#carte_params").append($('<span/>', {'class': "form_title", 'text': "Выходные данные:"}));
		$("#carte_params").append($('<br>'), $('<br>'));
		$("#carte_params").append($('<label/>', {'for': "out_format", "text": "Формат:"}));
		$("#carte_params").append($('<br>'));
		$("#carte_params").append($('<select/>', {'name': "out_format", "id": "out_format"}));
		for (var i = 0; i < inputFormats.length; ++i)
			$("#out_format").append($('<option/>', {'value': i, 'text': inputFormats[i]}));
		$("#carte_params").append($('<br>'), $('<br>'));
		$("#carte_params").append($('<label/>', {'for': "out_srs", "text": "Система координат:"}));
		$("#carte_params").append($('<br>'));
		$("#carte_params").append($('<select/>', {'name': "out_srs", "id": "out_srs"}));
		for (var i in SRS_ARR)
		{
			var tmp = SRS_ARR[i];
			$("#out_srs").append($('<option/>', {'value': tmp.id, 'text': tmp.desc + " (EPSG: " + tmp.id + ")"}));
		}
		$("#out_srs").searchable({maxMultiMatch: 200});
	}
	else {}
}
		
function transConfigJSON(json)
{
	for (var i = 0; i < json['transNames'].length; ++i)
		$("#trans_id").append($('<option/>', {'value': i, 'text': json['transNames'][i]}));
	for (var i = 0; i < json['basicFormats'].length; ++i)
		inputFormats.push(json['basicFormats'][i]);
	onTransIDChange();
}

function loadTransConfig()
{
	$.ajax({
		method: 'GET',
		url: CARTE_URL + 'transConfig?jsonp=transConfigJSON',
		dataType : "jsonp",
		jsonpCallback: 'transConfigJSON',
        async: false,
	});
}

function testAjax()
{
    var formData = new FormData();    
	for (var i = 0; i < $('#input').prop('files').length; ++i)
		formData.append('input[]', $('#input').prop('files')[i]);
	var children = $("#carte_form input, #carte_form select");
	for (var i = 0; i < children.length; ++i)
	{
		if ($(children[i]).is("input[type='file']") || $(children[i]).is("input[type='submit']"))
			continue;
		formData.append($(children[i]).attr("name"), $(children[i]).val());
	}
	$.ajax({
		type: 'post',
		url: CARTE_URL + "startTrans/",
		processData: false,
		contentType: false,
        success: function(data){
			console.log(data);
		},
		error: function(data){
			console.log(data);
		},
		data: formData,
	});
}

$(document).ready(function() 
{
	$("#carte_form").attr("action", CARTE_URL + "startTrans/");
	$("#carte_test").attr("action", CARTE_URL + "startTrans/");
	for (var i in SRS_ARR)
	{
		var tmp = SRS_ARR[i];
		$("#in_srs").append($('<option/>', {'value': tmp.id, 'text': tmp.desc + " (EPSG: " + tmp.id + ")"}));
	}
	$("#in_srs").searchable({maxMultiMatch: 200});
	$("#trans_id").change(onTransIDChange);
	$("#test").click(testAjax);
	loadTransConfig();
});
