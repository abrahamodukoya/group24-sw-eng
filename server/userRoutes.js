
const Joi = require('joi');
const express = require('express');
const router = express.Router();
const mongoose = require('mongoose');
const bcrypt = require('bcrypt');
const jwt = require('jsonwebtoken');
const User = require('./userModel');
const checkAuth = require('./check-auth');


// NOT PROTECTED
//http://3.92.227.189:80/api/users
router.get('/users',(req, res, next) => {
    User.find()
    .select('-__v')
    .exec()
    .then(docs => {
        const response = {
            count: docs.length, 
            users: docs
        };
        res.status(200).json(response);
    })
    .catch(err => {
        console.log(err);
        res.status(500).json({
            error: err
        });
    });
});


// NOT PROTECTED
//http://3.92.227.189:80/api/users/_id
router.get('/users/:id', (req, res, next) => {
    const id = req.params.id;
    User.findById(id)
    .select('-__v')
    .exec()
    .then(doc =>{
        if(doc){
            res.status(200).json(doc);
        }else{
            res.status(404).json({
                message: ' No valid entry for this id.'
            });
        }
    })
    .catch(err => {
        console.log(err);
        res.status(500).json({error: err});
    });
});


//http://3.92.227.189:80/api/users/signup
// post request
router.post('/users/signup', (req, res, next) => {
    console.log(req.body);
    User.find({username : req.body.username})
    .exec()
    .then(foundUser => {
        // if user of this username already exists
        if(foundUser.length >= 1){
            // code 409 - conflict
            return res.status(409).json({
                message : 'Username taken'
            });
        }
        // if username isn't taken
        else{
             // create new object and assign attributes
            bcrypt.hash(req.body.password, 10, (err, hash)=>{
                if(err){
                    return res.status(500).json({
                        error : err
                    });
                }
                else{
                    const user = new User({
                        _id : new mongoose.Types.ObjectId(),
                        username : req.body.username,
                        password : hash
                    });
                    user
                    .save()
                    .then(result =>{
                        console.log(result);
                        // return in body of response to client
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



// login request
router.post('/users/login', (req, res, next) => {
    User.find({ username : req.body.username})
    .exec()
    .then(user => {
        if(user.length < 1){
            return res.status(401).json({
                message : 'Auth failed'
            });
        }
        bcrypt.compare(req.body.password, user[0].password, (err, result) =>{
            if(err){
                return res.status(401).json({
                    message : 'Auth failed'
                });
            }
            if(result){
                const token = jwt.sign({
                    username : user[0].username,
                    userId : user[0]._id
                },
                // secret key for token creation
                '24Jooan7g@ry%77ness',
                {
                    expiresIn : '1h'
                });
                return res.status(201).json({
                    message : 'Auth successful',
                    token : token
                });
            }
            return res.status(401).json({
                message : 'Auth failed'
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
//http://3.92.227.189:80/api/users/_id
router.patch('/users/:id', checkAuth, (req, res, next)=> {
    const id = req.params.id;
    User.findOne({_id : id}, (err,user)=>{
        if(err){
            console.log(err);
            res.status(500).send();
        }else{
            // user doesn't exist
            if(!user){
                res.status(404).send();
            }
            // user found
            else{
                // date of day
                const date = req.body.date;
                // data to be updated
                const data = {
                    type: req.body.type,
                    label: req.body.label,
                    duration: req.body.duration
                };
                // if no days 
                var day_len = user.data.day.length;
                if(day_len === 0){
                    user.data.day = {activity:[]};
                    user.data.day[0].date = date; 
                    user.data.day[0].activity[0] = data;
                    console.log('date is ======> ', user.data.day[0].date);
                }
                // else if there are days
                else if(day_len !== 0){
                    var activity_len = user.data.day[day_len-1].activity.length;
                    // update existing day
                    if(date === user.data.day[day_len-1].date){
                        console.log('DATES EQUAL ======> ', date, ' ====> ',user.data.day[day_len-1].date);
                        user.data.day[day_len-1].activity[activity_len] = data;       
                    }
                    // new day
                    else if(date > user.data.day[day_len-1].date){
                        console.log('DATE BIGGER..........', date, ' ====> ',user.data.day[day_len-1].date);
                        user.data.day[day_len] = {activity:[]};
                        user.data.day[day_len].date = date; 
                        user.data.day[day_len].activity[0] = data;
                    }
                    // if date is from the past - throw error
                    else if(date < user.data.day[day_len-1].date){
                        console.log('DATE LOWER............', date, ' ====> ',user.data.day[day_len-1].date);
                        res.send('Cannot add activity for a day in the past. Please enter a valid date.');
                        return;
                    }
                }
            }

            user
            // .select('-__v')
            .save()
            .then(result =>{
                console.log(result);
                // return in body of response to client
                res.status(201).json({
                    message : 'User updated',
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


// delete request - PROTECTED
//http://3.92.227.189:80/api/users/_id
router.delete('/users/:id', checkAuth, (req, res, next) => {
    const id = req.params.id;
    User.remove({_id: id})
    .exec()
    .then(result => {
        res.status(200).json(result);
    })
    .catch(err => {
        console.log(err);
        res.status(500).json({
            error: err
        });
    });
});



// function to validate user input
// currently checks if positive and if not negative and less than 24 hours, since it's daily input
// is there a way to check is sum < 24???
// function validateUserTimes(userTimes){
//     const schema = {
//         rest : Joi.number().min(0).less(24),
//         social : Joi.number().min(0).less(24),
//         productive : Joi.number().min(0).less(24),
//         fitness :  Joi.number().min(0).less(24)
//     };
//     return Joi.validate(userTimes, schema);
// }


module.exports = router;