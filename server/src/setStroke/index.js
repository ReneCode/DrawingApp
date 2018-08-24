module.exports = function (context, req) {
    let stroke = req.body;
    if (!stroke) {
        context.res = {
            status: 400,
            body: "Please pass a stroke in the request body"
        };
    } else {
        // context.log("In:", stroke);
        const newStroke = findSimilarStroke(stroke);
        // context.log("out:", newStroke);
        context.res = {
            status: 200, /* Defaults to 200 */
            headers: {
               'Content-Type': 'application/json'
            },
            body: newStroke
        };
    }
    context.done();
};

function random(max) {
    return -1 * max/2 + Math.random() * max;
}

function findSimilarStroke(stroke) {
    const newStroke = {};
    if (stroke.points) {
        newStroke.points = stroke.points.map(p => {
            return {
                x: p.x + random(30),
                y: p.y + random(30)
            }
        });
    }
    return newStroke;
}