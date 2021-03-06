﻿var inputFormats = [];

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

function onInputTypeChange()
{
	$("label[for='input']").next().remove();
	$("label[for='input']").next().remove();
	var input;
	if ($("#in_type").val() == "wfs")
		input = $('<input/>', {'name': "input", "id": "input", "type": "text"});
	else
		input = $('<input/>', {'name': "input", "id": "input", "type": "file", "multiple": "multiple"});
	var br = $('<br>');
	br.insertAfter($("label[for='input']"));
	input.insertAfter(br);
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
		url: CARTE_URL + '/transConfig?jsonp=transConfigJSON',
		dataType : "jsonp",
		jsonpCallback: 'transConfigJSON',
        async: false,
	});
}

function receiveMessage(event)
{
	if (event.origin !== CARTE_URL)
		return;
	
	var json = event.data;
	json = JSON.parse(json);
	var text = "";
	for (var i = 0; i < json.length; ++i)
		text += "<span class='error_title'>" + json[i]["ErrorTitle"] + ": </span>" + 
				json[i]["ErrorText"] + "<br>";
				
	dhtmlx.alert({
		title: "Ошибки запроса",
		ok:	"OK",
		text: text,
	});
}

$(document).ready(function() 
{
	$("#in_type").change(onInputTypeChange);
	onInputTypeChange();
	window.addEventListener("message", receiveMessage, false);
	$("#carte_form").attr("action", CARTE_URL + "/startTrans/");
	for (var i in SRS_ARR)
	{
		var tmp = SRS_ARR[i];
		$("#in_srs").append($('<option/>', {'value': tmp.id, 'text': tmp.desc + " (EPSG: " + tmp.id + ")"}));
	}
	$("#in_srs").searchable({maxMultiMatch: 200});
	$("#trans_id").change(onTransIDChange);
	loadTransConfig();
});
