
const Joi = require('joi');
const express = require('express');
const router = express.Router();
const mongoose = require('mongoose');


const User = require('./userModel');

//http://3.92.227.189:80/
router.get('/', (req, res, next)=> {
    res.send('Hello! This is a home page.');
});


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


//http://3.92.227.189:80/api/users
// post request
router.post('/users', (req, res, next) => {

    console.log(req.body);

    // create new object and assign attributes
    const user = new User(req.body);
    user._id = new mongoose.Types.ObjectId();
    //user.data.day = {activity:[]};

    user
    .save()
    .then(result =>{
        console.log(result);
        // return in body of response to client
        res.status(201).json({
            message : 'New user added',
            user: result
        });
    })
    .catch(err => {
        console.log(err);
        res.status(500).json({
            error: err
        });
    });
});



// patch request
//http://3.92.227.189:80/api/users/_id
router.patch('/users/:id', (req, res, next)=> {
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



// delete request
//http://3.92.227.189:80/api/users/_id
router.delete('/users/:id', (req, res, next) => {
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