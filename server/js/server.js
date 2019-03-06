"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
const express_1 = __importDefault(require("express"));
const morgan_1 = __importDefault(require("morgan"));
const body_parser_1 = __importDefault(require("body-parser"));
const cors_1 = __importDefault(require("cors"));
const mongoose = require('mongoose');
const User = require('./schema');
const API_PORT = 3001;
const client = express_1.default();
const router = express_1.default.Router();
const path = 'mongodb://Sean:ksjvaq7!@cluster0-c0qmb.mongodb.net/test?retryWrites=true';
client.use(cors_1.default());
mongoose.connect(path, { useNewUrlParser: true });
const db = mongoose.connection;
db.once('open', () => console.log('connected'));
db.on('error', console.error.bind(console, 'connection error:'));
client.use(body_parser_1.default.urlencoded({ extended: false }));
client.use(body_parser_1.default.json());
client.use(morgan_1.default('dev'));
client.use('/', router);
client.listen(API_PORT, () => console.log(`LISTENING ON PORT ${API_PORT}`));
//Type in new entry details here and run. Currently hard coded but data successfully reaches 
//the database.
const user = new User({
    _id: new mongoose.Types.ObjectId(),
    name: 'Dixie Normus',
    username: 'fondler',
    week: { day: [{ activity: [{ type: String, label: 'Test Activity', day: 'Tuesday', time: '16:00', duration: '4' }] },], week: '12/03/2019', },
});
user
    .save()
    .then(result => {
    console.log(result);
})
    .catch(err => {
    console.log(err);
});
