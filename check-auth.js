
const jwt = require('jsonwebtoken');

// function which will check whether a protected route can be entered
// (to enter a protected route, the request must contain a valid token)

module.exports = (req, res, next) => {
    try{
        const decoded = jwt.verify(req.body.token, '24Jooan7g@ry%77ness');
        req.userData = decoded;
        next();
    }catch(error){
        return res.status(401).json({
            message : 'Authentication failed.'
        });
    }
};