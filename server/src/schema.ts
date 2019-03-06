const mongoose = require('mongoose');
const userSchema = mongoose.Schema({
  
  _id: String,
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
                day: String,
                time: String,
                duration: Number
                }
            ]
            },
        ],
        week: String,
      
    },
});
module.exports = mongoose.model('User', userSchema);