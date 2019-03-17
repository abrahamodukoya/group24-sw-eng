const mongoose = require('mongoose');
const userSchema = mongoose.Schema({

    _id: mongoose.Schema.Types.ObjectId,
    name: String,
    username: String,
    week: {
        index: false,
        day: [
            {
                activity: [
                    {
                        type: String,
                        label: String,
                        dayName: String,
                        date: String,
                        duration: Number
                    }
                ]
            },
        ],
    },   
});

module.exports = mongoose.model('User', userSchema);
