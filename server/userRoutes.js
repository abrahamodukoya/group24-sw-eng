
const Joi = require('joi');
const express = require('express');
const router = express.Router();
const mongoose = require('mongoose');


const User = require('./userModel');

//http://localhost:3000/
router.get('/', (req, res, next)=> {
    res.send('Hello! This is a home page.');
});


//http://localhost:3000/api/users
router.get('/users',(req, res, next) => {
    User.find()
    .exec()
    .then(docs => {
        console.log(docs);
        res.status(200).json(docs);
    })
    .catch(err => {
        console.log(err);
        res.status(500).json({
            error: err
        });
    });
});

//http://localhost:3000/api/users/1
router.get('/users/:id', (req, res, next) => {
    const id = req.params.id;
    User.findById(id)
    .exec()
    .then(doc =>{
        console.log('From database\n', doc);
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



// post request
router.post('/users', (req, res, next) => {

    console.log(req.body);

    // create new object and assign attributes
    const user = new User(req.body);
    user._id = new mongoose.Types.ObjectId();
    user.week.day = {activity:[]};

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
router.patch('/users/:id', (req, res, next)=> {
    const id = req.params.id;
    console.log('this is the id number ' + id);
    // prints whatever is inputted as ':id'

    User.findOne({_id : id}, (err,user)=>{
        if(err){
            console.log(err);
            res.status(500).send();
        }else{
            if(!user){
                res.status(404).send();
            }
            else{
                var data = {
                    type: req.body.type,
                    label: req.body.label,
                    dayName: req.body.dayName,
                    date: req.body.date,
                    duration: req.body.duration
                };

                User.update({_id : id}, {$push: {activity : data}});
            }

            user.save((err, updatedObj)=>{
                if(err){
                    console.log(err);
                    res.status(500).send();
                }
                else{
                    res.send(updatedObj);
                }
            });
        }
    });
});



// delete request
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