function insertNewToDo(todo, todoRef) {
    var request = new Object();
    request["toDo"] = todo;
    request["refList"] = [1];

    var json = JSON.stringify(request);

    $.get({
        url: "/insertNewToDo",
        type: 'POST',
        data: json
    }).done(function (res) {
        alert("done: " + res);
        var result = res['toDoReqResult'];
        if (result != 0) {
            alert("Error: " + result)
        } else {
            var toDo = res['toDo'];
            alert(toDo);
        }

    }).fail(function (xhr, status, errorThrown) {
        alert("insertNewToDo fail: " + xhr + ", " + status + ", " + errorThrown);
    })
}