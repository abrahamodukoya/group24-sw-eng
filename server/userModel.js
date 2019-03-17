const mongoose = require('mongoose');
const activitySchema = mongoose.Schema({
    _id : false,
    type: String,
    label: String,
    dayName: String,
    date: String,
    duration: Number
});
const daySchema = mongoose.Schema({
    _id : false,
    index: false,
    activity: [activitySchema]
});
const userSchema = mongoose.Schema({

    _id: mongoose.Schema.Types.ObjectId,
    name: String,
    username: String,
    week: {
        index: false,
        day: [daySchema],
    },
});

module.exports = mongoose.model('User', userSchema);
