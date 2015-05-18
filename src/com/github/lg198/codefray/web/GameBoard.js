var canvas = document.getElementById("document");
var ctx = canvas.getContext("2d");

var transx = 0;
var transy = 0;
var gridSize = 50;
var minGridSize = 10;
var maxGridSize = 60;

function render() {
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    ctx.save();

    ctx.translate(transx, transy);

    var pad = gridSize / 10;
    var width =
}