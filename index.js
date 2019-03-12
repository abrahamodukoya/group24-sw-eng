
const express = require('express');
const app = express();
const morgan = require('morgan');
const mongoose = require('mongoose');

app.use(express.json());
app.use(morgan('dev'));

const path = ('mongodb+srv://group24:j5MATH!-AEQEZmA@group24-c1xu2.mongodb.net/test?retryWrites=true');

mongoose.connect(path,{ useNewUrlParser: true })
.then()
.catch( err => {
    console.log(err);
    process.exit(1);
});


// const db = mongoose.connections;

// db.once('open', ()=> console.log('connected'));
// db.on('error', console.error.bind(console, 'connection error:'));

const userRoutes = require('./userRoutes');
app.use("/api",userRoutes);


// PORT
// base - listening to port 3000
// can be changed with 'export PORT= xxxx' 
const port = process.env.PORT || 3000;
app.listen(port, ()=>{
    console.log(`listening on ${port}...`);
});

// error handler, if reached - no route before was entered
app.use((req, res, next)=>{
    const error = new Error('Route not found');
    console.log('ERROR : Route not found');
    error.status = 404;
    next(error);
});

// error handler
app.use((error, req, res, next)=>{
    // 500 error code for all other types of errors
    res.status(error.status || 500);
    res.json({
        error: {
            message: error.message
        }
    });
});
