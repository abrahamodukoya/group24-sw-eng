import express from 'express';
import logger from 'morgan';
import bodyParser from 'body-parser';
import cors from 'cors';
import { resolve } from 'url';

const mongoose = require('mongoose');
const User = require('./schema');
const API_PORT = 3001;

const client = express();
const router = express.Router();
const path = 'mongodb://Sean:ksjvaq7!@cluster0-c0qmb.mongodb.net/test?retryWrites=true';

client.use(cors());

mongoose.connect(
  path, { useNewUrlParser: true },
);

const db = mongoose.connection;

db.once('open', () => console.log('connected'));
db.on('error', console.error.bind(console, 'connection error:'));

client.use(bodyParser.urlencoded({ extended: false }));
client.use(bodyParser.json());
client.use(logger('dev')); 
client.use('/', router);

client.listen(API_PORT, () => console.log(`LISTENING ON PORT ${API_PORT}`));


//Type in new entry details here and run. Currently hard coded but data successfully reaches 
//the database.


const user = new User({
   _id: new mongoose.Types.ObjectId(),
   name: 'Dixie Normus',
   username: 'fondler',
  week: {day: [{activity: [{type: String, label: 'Test Activity', day: 'Tuesday', time: '16:00', duration: '4'}]},],week: '12/03/2019',}, });
   user
   .save()
   .then(result => {
     console.log(result);
   })
   .catch(err => {
     console.log(err);
   });