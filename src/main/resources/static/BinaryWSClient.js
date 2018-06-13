var ws;

function setConnected(connected) {
  $("#connect").prop("disabled", connected);
  $("#disconnect").prop("disabled", !connected);
  if (connected) {
    $("#conversation").show();
  } else {
    $("#conversation").hide();
  }
  $("#greetings").html("");
}

function connect() {
  let uploadSessionId = getUniqueSessionId(new Date().getTime);
  console.log("WSClient Conectado " + uploadSessionId);
  ws = new WebSocket(
    "ws://192.168.20.26:7000/binary?uploadSessionId=" + uploadSessionId
  );

  ws.onmessage = function(data) {
    console.log(data);
    showGreeting(data);
  };

  setConnected(true);
}

function disconnect() {
  if (ws != null) {
    ws.close();
  }
  setConnected(false);
  console.log("Disconnected");
}

function sendName() {
  var data = $("#file").prop("files")[0];
  ws.send(data);
  console.log("WSClient Finished sending " + data.name);
}

function showGreeting(response) {
  console.log("WSSERVER: ", response);

  var binaryData = [];
  binaryData.push(response.data);
  var objectURL = window.URL.createObjectURL(
    new Blob(binaryData, { type: "image/png" })
  );

  var img = `<b>${new Date()}</b><br/></b><tr><td><img class="img img-responsive" style="width: 100%;" src="${objectURL}"></tr></td>`;
  $("#greetings").append(img);
}

$(function() {
  $("form").on("submit", function(e) {
    e.preventDefault();
  });
  $("#connect").click(function() {
    connect();
  });
  $("#disconnect").click(function() {
    disconnect();
  });
  $("#send").click(function() {
    sendName();
  });
});

function guid() {
  function s4() {
    return Math.floor((1 + Math.random()) * 0x10000)
      .toString(16)
      .substring(1);
  }

  return (
    s4() +
    s4() +
    "-" +
    s4() +
    "-" +
    s4() +
    "-" +
    s4() +
    "-" +
    s4() +
    s4() +
    s4()
  );
}

function getUniqueSessionId(additionalValue) {
  return btoa(guid() + "\\" + additionalValue);
}
