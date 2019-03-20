const mongoose = require('mongoose');
const activitySchema = mongoose.Schema({
    _id : false,
    type: {type: String, required : true},
    label: String,
    duration: {type: String, required : true}
});
const daySchema = mongoose.Schema({
    _id : false,
    index: false,
    date: String,
    activity: [activitySchema]
});
const userSchema = mongoose.Schema({

    _id: mongoose.Schema.Types.ObjectId,
    username: {type: String, required : true},
    password: {type: String, required : true},
    data: {
        index: false,
        day: [daySchema],
    },
});

module.exports = mongoose.model('User', userSchema);