/**
 *This is the server-side script. It will respond to requests from the app to store and retrieve data. Data storage and retrieval can be put into seperate scripts if deemed necessary.
 *
 */

 
const Joi = require('joi');
const express = require('express');
const app = express();

app.use(express.json());

// initial array of users with some sample details
const users = [
    { id: 1, rest : 8, social : 3, productive : 4, fitness : 1 },
    { id: 2, rest : 3, social : 6, productive : 1, fitness : 10 }
];

//http://localhost:3000/
app.get('/', (req, res)=> {
    res.send('Hello! This is a test page.');
});

//http://localhost:3000/api/users
app.get('/api/users',(req,res) => {
    res.send(users);
});

//http://localhost:3000/api/users/1
app.get('/api/users/:id', (req,res) => {
    const user = users.find(c => c.id === parseInt(req.params.id));
    // error 404 - not found
    if(!user) res.status(404).send('User not found.');
    res.send(user);
});

// post request - adds a new user to the array and the rest,social,productive,fitness attributes to its object
// doesn't check for duplicate id numbers (yet)
app.post('/api/users', (req, res) => {
    const result = validateUserTimes(req.body);
    if(result.error){
        // error code 400 - bad request
        res.status(400).send(result.error.details[0].message);
        return;
    }
    // create new object and assign attributes
    const userTimes = {
        id: users.length + 1,
        rest: req.body.rest,
        social: req.body.social,
        productive: req.body.productive,
        fitness: req.body.fitness
    };
    // add to array
    users.push(userTimes);
    // return in body of response to client
    res.send(userTimes);
});

// put request - currently overwrites(updates) old values for rest,social,productive,fitness 
app.put('/api/users/:id', (req,res)=> {
    const user = users.find(c => c.id === parseInt(req.params.id));
    if(!user) {
        // error 404 - not found
        res.status(404).send('User not found');    
        return;
    }
    const result = validateUserTimes(req.body);
    if(result.error){
        // error code 400 - bad request
        res.status(400).send(result.error.details[0].message);
        return;
    }
    // update the corresponding values
    user.rest = req.body.rest;
    user.social = req.body.social;
    user.productive = req.body.productive;
    user.fitness = req.body.fitness;
    // send info back to client
    res.send(user);
});


app.delete('/api/users/:id', (req,res) => {
    const user = users.find(c => c.id === parseInt(req.params.id));
    if(!user) {
        // error 404 - not found
        res.status(404).send('User not found.');    
        return;
    }
    // remove user from array
    const index = users.indexOf(user);
    users.splice(index,1);
    // send back the removed user object back to client
    res.send(user);
});

// PORT
// base - listening to port 3000
// can be changed with 'export PORT= xxxx' 
const port = process.env.PORT || 3000;
app.listen(port, ()=>{
    console.log(`listening on ${port}...`);
});


// function to validate user input
// currently checks if positive and if less than 24 hours, since it's daily input
// in the future check if sum of all times < 24 ???
function validateUserTimes(userTimes){
    const schema = {
        rest : Joi.number().positive().less(24),
        social : Joi.number().positive().less(24),
        productive : Joi.number().positive().less(24),
        fitness :  Joi.number().positive().less(24)
    };
    return Joi.validate(userTimes, schema);
}