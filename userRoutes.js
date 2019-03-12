
const Joi = require('joi');
const express = require('express');
const router = express.Router();
const mongoose = require('mongoose');


const User = require('./userModel');

/*********************************************************************************************************************************************/

// initial array of users with some sample details
const users = [
    { id: 1, rest : 8, social : 3, productive : 4, fitness : 1 },
    { id: 2, rest : 3, social : 6, productive : 1, fitness : 10 }
];

// /*********************************************************************************************************************************************/



//http://localhost:3000/
router.get('/', (req, res, next)=> {
    res.send('Hello! This is a home page.');
});


//http://localhost:3000/api/users
router.get('/users',(req, res, next) => {
    // res.send(users);
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
    // const user = users.find(c => c.id === parseInt(req.params.id));
    // // error 404 - not found
    // if(!user) res.status(404).send('User not found.');
    const id = user.id;
    User.findById(id)
    .exec()
    .then(doc =>{
        console.log('From database', doc);
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




// post request - adds a new user to the array and the rest,social,productive,fitness attributes to its object
// doesn't check for duplicate id numbers (yet)
router.post('/users', (req, res, next) => {
   // const result = validateUserTimes(req.body);
    if(result.error){
        // error code 400 - bad request
        res.status(400).send(result.error.details[0].message);
        return;
    }
    // create new object and assign attributes
    const user = new User({
        _id: new mongoose.Types.ObjectId(),
        name : req.body.name,
        username: req.body.username,
        week: req.body.week
    });

    // save() stores in the db
    user
    .save()
    .then(result =>{
        console.log(result);
        // add to array
        // users.push(userTimes);

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



// put request - currently overwrites(updates) old values for rest,social,productive,fitness 
router.patch('/users/:id', (req, res, next)=> {
    const id = req.params.productId;
    const updateOps = {};
    for (const ops of req.body){
        updateOps[ops.propName] = ops.value;
    }
    User.update({_id: id}, {$set : updateOps})
    .exec()
    .then(result => {
        console.log(result);
        res.status(200).json(result);
    })
    .catch(err => {
        console.log(err);
        res.status(500).json({
            error: err
        });
    });
});



router.delete('/users/:id', (req, res, next) => {
    const id = req.params.productId;
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