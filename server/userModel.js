const mongoose = require('mongoose');
const userSchema = mongoose.Schema({
    _id: mongoose.Schema.Types.ObjectId,

    username: {type: String, required : true, unique : true},
    password: {type: String, required : true},
    data: {
        index: false,
        day: [{
            index: false,
            date: String,
            activity: [{
                _id : false,
                type: {type: String, required : true},
                label: String,
                duration: {type: String, required : true}
            }]
        }],
    },
});

module.exports = mongoose.model('User', userSchema);
