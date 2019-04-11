// import dependencies
const Joi = require('joi');
const express = require('express');
const router = express.Router();
const mongoose = require('mongoose');
const bcrypt = require('bcrypt');
const jwt = require('jsonwebtoken');
const User = require('./userModel');
const checkAuth = require('./check-auth');



// NOT PROTECTED
// returns a list of all of the users
// http://3.92.227.189:80/api/users
router.get('/users', checkAuth, (req, res, next) => {
    User.find()
    .select('-__v')
    .exec()
    .then(docs => {
        const response = {
            count: docs.length,
            users: docs
        };
        // return list of users
        res.status(200).json(response);
    })
    .catch(err => {
        console.log(err);
        res.status(500).json({
            error: err
        });
    });
});



// PROTECTED
// returns a user object if one exists, based of id
// http://3.92.227.189:80/api/users/_id
router.get('/users/:id', checkAuth, (req, res, next) => {
    const id = req.params.id;
    User.findById(id)
    .select('-__v')
    .exec()
    .then(doc =>{
        if(doc){
            // if a user of this id exists
            res.status(200).json(doc);
        }else{
            // if a user of this id is not found
            res.status(404).json({
                message: ' No user found for this id.'
            });
        }
    })
    .catch(err => {
        console.log(err);
        res.status(500).json({error: err});
    });
});



// NOT PROTECTED
// register request, allows non existing users to register
// http://3.92.227.189:80/api/users/register
router.post('/users/register', (req, res, next) => {
    User.find({username : req.body.username})
    .exec()
    .then(foundUser => {
        // if user of this username already exists
        if(foundUser.length >= 1){
            // code 409 - conflict
            return res.status(409).json({
                message : 'Username taken.'
            });
        }
        // if username isn't taken
        else{
             // create new object and assign attributes
             // hash password
            bcrypt.hash(req.body.password, 10, (err, hash)=>{
                if(err){
                    return res.status(500).json({
                        error : err
                    });
                }
                else{
                    // create new user object
                    const user = new User({
                        _id : new mongoose.Types.ObjectId(),
                        username : req.body.username,
                        password : hash
                    });
                    // save user to the database
                    user
                    .save()
                    .then(result =>{
                        // return user_id in response to client
                        res.status(201).json({
                            userId: result._id
                        });
                    })
                    .catch(err => {
                        console.log(err);
                        res.status(500).json({
                            error: err
                        });
                    });
                }
            });
        }
    });
});



// NOT PROTECTED
// login request - checks if there exists a user of matching username and password
// http://3.92.227.189:80/api/users/login
router.post('/users/login', (req, res, next) => {
    User.find({ username : req.body.username})
    .exec()
    .then(user => {
        if(user.length < 1){
            // .find returns an array of users (should only ever be max length 1 as each user is unique)
            // if length < 1 --> no user found
            return res.status(401).json({
                message : 'Authentication failed.'
            });
        }
        // user found, compare hashed passwords
        bcrypt.compare(req.body.password, user[0].password, (err, result) =>{
            if(err){
                return res.status(401).json({
                    message : 'Authentication failed.'
                });
            }
            // if hashed passwords match, create a token and assign it to the user
            if(result){
                // token object
                const token = jwt.sign({
                    username : user[0].username,
                    userId : user[0]._id
                },
                // secret key for token creation
                '24Jooan7g@ry%77ness',
                {
                // expiration timer
                    expiresIn : '1h'
                });
                // return token and user id upon successful login
                return res.status(201).json({
                    message : 'Authentication successful.',
                    token : token,
                    _id : user[0].id
                });
            }
            // error message if something went wrong
            // all error messages are the same for security reasons so potential attackers can't 
            // figure out where the login failed
            return res.status(401).json({
                message : 'Authentication failed.'
            });
        });
    })
    .catch(err => {
        console.log(err);
        res.status(500).json({
            error: err
        });
    });
});



// patch request - PROTECTED
// updates the activity array of a user on a specific day, based on date from request
//http://3.92.227.189:80/api/users/_id
router.put('/users/:id', checkAuth, (req, res, next)=> {
    const id = req.params.id;
    User.findOne({_id : id}, (err,user)=>{
        if(err){
            console.log(err);
            res.status(500).send();
        }else{
            // if user doesn't exist
            if(!user){
                res.status(404).send();
            }
            // if user found
            else{
                // date of day from request
                // ( the model is designed such that the array of days for a user will be in chronological order )
                const date = req.body.date;
                // data to be updated (the activity to be added)
                const data = {
                    type: req.body.type,
                    label: req.body.label,
                    duration: req.body.duration
                };
                // some input validation for duration to be a positive number etc
                const userInput = validateInput(data);
                if(userInput.error){
                    // error code 400 - bad request
                    res.status(400).json({
                        error : userInput.error.details[0].message
                    });
                    return;
                }
                // if the user has no previous inputs, and hence no day objects in db 
                var day_len = user.data.day.length;
                if(day_len === 0){
                    // create activity array for the day object
                    user.data.day = {activity:[]};
                    // asign date and activity to the day
                    user.data.day[0].date = date; 
                    user.data.day[0].activity[0] = data;
                }
                // else if there are previous inputs, ie existing day objects in db
                else if(day_len !== 0){
                    // length of activity array of last day in array
                    var activity_len = user.data.day[day_len-1].activity.length;
                    // if date from request matches that of the last day in the array
                    // update the activity array of that day
                    if(date === user.data.day[day_len-1].date){
                        user.data.day[day_len-1].activity[activity_len] = data;       
                    }
                    // else if date is more recent than last day in the array
                    // create a new day object and assign corresponding values
                    else if(date > user.data.day[day_len-1].date){
                        user.data.day[day_len] = {activity:[]};
                        user.data.day[day_len].date = date;
                        user.data.day[day_len].activity[0] = data;
                    }
                    // if date is from the past - throw error
                    // AS SUCH THE ARRAY WILL BE IN CHRONOLOGICAL ORDER
                    else if(date < user.data.day[day_len-1].date){
                        res.send('Cannot add activity for a day in the past. Please enter a valid date.');
                        return;
                    }
                }
            }
            user
            .save()
            .then(result =>{
                // return updated user in body of response to client
                res.status(201).json({
                    message : 'User updated.',
                    user: result
                });
            })
            .catch(err => {
                console.log(err);
                res.status(500).json({
                    error: err
                });
            });
        }
    });
});

// Simple sign in/ verify user - checks for user with matching, username, password and ID and returns
// the id. (no encryption, secure route etc.). This is now obsolete.
router.get('/simpleSign/:id/:username/:password', checkAuth, function(req, res,next){
    const userId = req.params.id;
    const userName = req.params.username;
    const passWord = req.params.password;

    console.log(userId);
    console.log(userName);
    console.log(passWord);

  User.find({_id: userId, password: passWord, username: userName }, '_id', function(err, user)
  {
      if (err)
      {
        res.status(404).json({
                message: ' No valid entry for this id.'
            });
      }
    res.json(user);
    console.log(user)

 });
});

// Get all days
router.get('/activity/:id/', checkAuth, function(req, res){
    const userId = req.params.id;
    console.log(userId)

  User.find({_id: userId}, 'data', function(err, user)
   {
      if (err)
      {
          res.send(err);
      }
      res.json(user);
      console.log(user)

   });
  });


// Gives all activities for a given date
router.put('/getDate/:id/:dateReq/', checkAuth, async function(req, res){
    const userId = req.params.id;
    const dateReq = req.params.dateReq;
    
    const userObj = await getFullUser(userId);
    var found = false;
    var i = 0;

    while ( i<userObj.data.day.length && found ==false){
        if (userObj.data.day[i].date == dateReq){
           const dayReturn =  userObj.data.day[i].activity;
           res.json(dayReturn);
           found == true;
        }
        i++;
    }
    res.json("None found");
});


// Get tips - These tips are mostly to display functionality, I'm no expert on healthy student lifestyle.
router.put('/tips/:id/:dateReq', checkAuth, async function(req, res){
    const userId = req.params.id;
    const dateReq = req.params.dateReq;
    const userObj = await getFullUser(userId);
    var date = new Date(dateReq);
    console.log("date"+date)

    var tipArray = [];

// 1. This section generates tips based on how you're doing so far today

    //Get stats for today
    var today = getDaily(userObj, date);
    var week = getWeekly(userObj,date);

    // If you have 5 hours of activity or more and rest is not 1/5 of your total activities (exclude sleep) !Tip
    if(today.prodCount + today.fitnessCount + today.socialCount >=5 &&  today.restCount<(today.prodCount + today.fitnessCount + today.socialCount)/5){
        tipArray.push("You haven't gotten enough rest today, take a break!");
    }

// 2. This section generates tips based on how you did yesterday, but doesn't tell you to do something you're already doing.

    // Get string for yesterday date (inconvenient)
     var dayIndex = ("0" + date.getDate()).slice(-2);
     dayIndex = dayIndex - 1;
     var monthIndex = date.getMonth()+1;
     monthIndex = ("0"+ monthIndex).slice(-2);
     var yearIndex = date.getFullYear();
     var yesterdayDate = (yearIndex+'/'+monthIndex+'/'+dayIndex); 

    // Get stats for yesterday.
     var yesterday=getDaily(userObj, yesterdayDate);

    // If you got less than 8 hours sleep !Tip
    if(yesterday.sleepCount< 8 && yesterday.sleepCount!=0){
        tipArray.push("You got less than 8 hours sleep yesterday. Try and get more tonight");
    }

    // If your rest accounted for les than 1/4 of your activities !Tip
    if(yesterday.restCount < yesterday.prodCount + yesterday.fitnessCount + yesterday.socialCount){
        tipArray.push("You didn't get enough rest yesterday. Make sure you take breaks.");
        
    }
    // If your productivity less than 4 yesterday and is less than 4 today. 
    if(yesterday.prodCount< 4 && today.prodCount < 4){
       tipArray.push("You weren't very productive yesterday and haven't done much today. Try and do some work.");
    }

    // If you didn't do any fitness yesterday and didn't do any today.
    if(yesterday.fitnessCount == 0 && today.fitnessCount == 0){
        tipArray.push("You didn't do any fitness today and you didn't yesterday either. Try to stay active.")
    }

//3. This section generates tips based on how you've been doing over last 7 days, doesn't tell you things you're already doing.
    if(week.restCount < week.prodCount + week.fitnessCount + week.socialCount){
        tipArray.push("You haven't been getting enough rest over the past week. Try to chill and take breaks!")
    }

    // If you haven't been spending 3 times more time in productivity than social. 
    if(week.prodCount/3 < week.socialCount && prodCount < 20){
        tipArray.push("You haven't spent enough time productively over the last week. Try to cut into your social time and use it to do work!")
    }

    else if(tipArray.length == 0){
        tipArray.push("You're doing fine for now. Keep using the app to get more tips.")
    }
    res.send(tipArray);
});

// Get daily stat
router.put('/dailyStat/:id/:dateReq', checkAuth, async function(req, res){
    const userId = req.params.id;
    const dateReq = req.params.dateReq;
    const userObj = await getFullUser(userId);
   res.send(getDaily(userObj,dateReq));
});


// Get weekly stat
router.put('/weeklyStat/:id/:dateReq', checkAuth, async function(req, res){
    const userId = req.params.id;
    const dateReq = req.params.dateReq;
    const userObj = await getFullUser(userId);
    res.send(getWeekly(userObj,dateReq));
});


function getWeekly(userObj, dateReq){
    var date = new Date(dateReq);
    var dateWeekAgo = new Date(dateReq);
    dateWeekAgo.setDate(date.getDate() - 7);
    var weeklyStat = new statObj(0,0,0,0,0);

    // We will never have to check more days than this counting down if
    // the current date is entered. This limit should be removed 
    // if you want to get stats for the last 7 days from any given date.
    var limit = userObj.data.day.length - 7;

    var lowerFound = false;
    var i = userObj.data.day.length - 1;
    
    while( i >= 0 && i > limit && lowerFound ==false){
        var dateCheck = new Date(userObj.data.day[i].date);
       if(dateCheck<=date && dateWeekAgo <= dateCheck){
            var temp = getDaily(userObj, userObj.data.day[i].date);
            weeklyStat.fitCount = weeklyStat.fitCount + temp.fitCount;
            weeklyStat.socialCount = weeklyStat.socialCount + temp.socialCount;
            weeklyStat.restCount = weeklyStat.restCount + temp.restCount;
            weeklyStat.sleepCount = weeklyStat.sleepCount + temp.sleepCount;
            weeklyStat.prodCount = weeklyStat.prodCount + temp.prodCount;
        }
        else{
            lowerFound = true;
        }
        i--;
    }
    console.log(weeklyStat);
    return(weeklyStat);
}

// Get monthly stat
router.put('/monthlyStat/:id/:dateReq', checkAuth, async function(req, res){
    const userId = req.params.id;
    const dateReq = req.params.dateReq;
    const userObj = await getFullUser(userId);

    var date = new Date(dateReq);
    var dateMonthAgo = new Date(dateReq);
    dateMonthAgo.setDate(date.getDate() - 31);
    var monthlyStat = new statObj(0,0,0,0,0);

    // If not getting stats for current date (i.e latest day in array), remove limit.
    var limit = userObj.data.day.length - 31;

    var lowerFound = false;
    var i = userObj.data.day.length - 1;
    
    while( i >= 0 && i > limit && lowerFound ==false){
        var dateCheck = new Date(userObj.data.day[i].date);
       if(dateCheck<=date && dateMonthAgo <= dateCheck){
            var temp = getDaily(userObj, userObj.data.day[i].date);
            if(temp!= null){
            monthlyStat.fitCount = monthlyStat.fitCount + temp.fitCount;
            monthlyStat.socialCount = monthlyStat.socialCount + temp.socialCount;
            monthlyStat.restCount = monthlyStat.restCount + temp.restCount;
            monthlyStat.sleepCount = monthlyStat.sleepCount + temp.sleepCount;
            monthlyStat.prodCount = monthlyStat.prodCount + temp.prodCount;
            }
        }
        else{
            lowerFound = true;
        }
        i--;
    }
    console.log(monthlyStat);


    res.send(monthlyStat);
});

// function to get stats for one day
function getDaily(userObj, dateReq){
    var productivityCount = 0; var socialCount = 0; var restCount = 0; var sleepCount = 0;  var fitCount = 0;

    var found = false;
    var i = -1;
    var j =0;

    while ( i<userObj.data.day.length-1 && found ==false){
        i++;
        if (userObj.data.day[i].date == dateReq){
           found = true;
        }
    }

    if(found == true) {
        while(j<userObj.data.day[i].activity.length){
            if(userObj.data.day[i].activity[j].type=='productivity'){
                var productivityString =userObj.data.day[i].activity[j].duration;
                var prod = parseInt(productivityString, 10);
                productivityCount = productivityCount + prod;
            }
            else if(userObj.data.day[i].activity[j].type=='social'){
                var socialString =userObj.data.day[i].activity[j].duration;
                var social = parseInt(socialString, 10);
                socialCount = socialCount + social;
            }
            else if(userObj.data.day[i].activity[j].type=='rest'){
                var restString =userObj.data.day[i].activity[j].duration;
                var rest = parseInt(restString, 10);
                restCount = restCount + rest;
            }
            else if(userObj.data.day[i].activity[j].type=='fitness'){
                var fitnessString = userObj.data.day[i].activity[j].duration;
                var fitness = parseInt(fitnessString, 10);
                fitCount = fitCount + fitness;
            }
            else if(userObj.data.day[i].activity[j].type=='sleep'){
                var sleepString =userObj.data.day[i].activity[j].duration;
                var sleep = parseInt(sleepString, 10);
                sleepCount = sleepCount + sleep;
            }
            j++;
        } 
}
    var daily = new statObj(productivityCount, socialCount, restCount, sleepCount, fitCount);
    console.log(daily)
    return daily;
}

// stat object that gets sent back to frontend
var statObj = function(prodCount, socialCount, restCount, sleepCount, fitCount) {
    this.prodCount = prodCount;
    this.socialCount = socialCount;
    this.restCount = restCount;
    this.sleepCount = sleepCount;
    this.fitCount = fitCount;
  }

// returns full user object
async function getFullUser(userId) {
    try {
        const temp = await User.findOne({ _id: userId });
        const user = new User(temp);
        user
        .save()
        .then(result => {
        console.log(result);
        console.log(user.data.day.length);
        });
    console.log('Waited for temp ');
    return user;
    //callback(user); 
    
    }catch (e) {

    }  
};



// delete request - PROTECTED
//http://3.92.227.189:80/api/users/_id
router.delete('/users/:id', (req, res, next) => {
    const id = req.params.id;
    User.deleteOne({_id: id})
    .exec()
    .then(result => {
        res.status(200).json({
            message : 'User deleted.'
        });
    })
    .catch(err => {
        console.log(err);
        res.status(500).json({
            error: err
        });
    });
});



// function to validate user input
// checks whether duration is a positive number less than 24, and if the required fields are there
function validateInput(data){
    const schema = {
        type : Joi.string().required(),
        label : Joi.string(),
        duration : Joi.number().min(1).less(24).required()
    };
    return Joi.validate(data, schema);
}


module.exports = router;