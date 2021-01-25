const express = require('express')
const app = express()
const bodyParser = require('body-parser')
const cors = require('cors')

app.use(bodyParser.json())
app.use(bodyParser.urlencoded({
    extended: true
  }));
app.use(cors())

const sessions = []

function makeid(length) {
    var result           = '';
    var characters       = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
    var charactersLength = characters.length;
    for ( var i = 0; i < length; i++ ) {
       result += characters.charAt(Math.floor(Math.random() * charactersLength));
    }
    return result;
 }

app.post('/', (req, res) => {
    console.log('got request: ' + req.body)
})

app.get('/new-session', (req, res) => {
    const id = makeid(5)

    sessions.push({
        sessionId: id,
        connected: false,
        newSkin: null
    })
    res.json({
        sessionId: id
    })
    console.log('session created: ' + id)
})

app.get('/session/:id', (req, res) => {
    for (let i = 0; i < sessions.length; i++) {
        if (sessions[i].sessionId === req.params.id) {
            return res.json(sessions[i])
        }
    }
    res.status(500).json({
        error: 'Session not found with id ' + req.params.id
    })
})

app.post('/session/:id/export', (req, res) => {
    for (let i = 0; i < sessions.length; i++) {
        if (sessions[i].sessionId === req.params.id) {
            sessions[i].newSkin = req.body.texture
        }
    }
})

app.get('/join-session/:id', (req, res) => {
    let id = req.params.id
    
    for (let i = 0; i < sessions.length; i++) {
        if (sessions[i].sessionId === id) {
            sessions[i].connected = true

            return res.json(sessions[i])
        }
    }
    res.status(500).json({
        error: 'Session not found with id ' + req.params.id
    })
})

app.listen(8000)
console.log('listening')