var currentPage = 1;

function insertNewToDo(todo, todoRef) {
    var request = new Object();
    request["toDo"] = todo;
    request["refList"] = todoRef.split(',');

    var json = JSON.stringify(request);

    $.get({
        url: "/insertNewToDo",
        type: 'POST',
        data: json
    }).done(function (res) {
        var result = res['toDoReqResult'];
        if (result != 0) {
            alert("Error: " + result)
        } else {
            location.reload();
        }

    }).fail(function (xhr, status, errorThrown) {
        alert("insertNewToDo fail: " + xhr + ", " + status + ", " + errorThrown);
    });
}

function finishToDo(toDo) {
    $.get({
        url: "/finishToDo/" + toDo,
        type: 'PUT'
    }).done(function (res) {
        var result = res['isDone'];
        if (result == 0) {
            alert("Error: " + res['errorLog'])
        } else {
            location.reload();
        }

    }).fail(function (xhr, status, errorThrown) {
        alert("finishToDo fail: " + xhr + ", " + status + ", " + errorThrown);
    });
}

function updateToDo(id, toDo) {
    var request = new Object();
    request["id"] = id;
    request["toDo"] = toDo;
    var json = JSON.stringify(request);

    $.get({
        url: "/updateToDo/",
        type: 'PUT',
        data: json
    }).done(function (res) {
        var result = res['isDone'];
        if (result == 0) {
            alert("Error: " + res['errorLog'])
        } else {
            location.reload();
        }

    }).fail(function (xhr, status, errorThrown) {
        alert("finishToDo fail: " + xhr + ", " + status + ", " + errorThrown);
    });
}

function prevPageButtonClick() {
    if (currentPage <= 1) {
        currentPage = 1;
        return;
    }

    pageButtonClick(--currentPage);
}

function nextPageButtonClick() {
    var p = document.getElementById('totalPageP').innerText;
    p *= 1;

    if (currentPage >= p) {
        currentPage = p;
        return;
    }

    pageButtonClick(++currentPage);
}

function lastPageButtonClick() {
    var p = document.getElementById('totalPageP').innerText;
    p *= 1;

    pageButtonClick(p);
}

function pageButtonClick(page) {
    location.href = '/todo/' + page;
}

function bodyOnLoad() {
    var p = document.getElementById('currentPageP').innerText;
    p *= 1;

    currentPage = p;
}