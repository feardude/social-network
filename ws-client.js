const WebSocket = require('ws');

const userId = 2548;
const url = `ws://localhost:8080/post/feed/posted?userId=${userId}`;

const socket = new WebSocket(url);

socket.on('open', function() {
    console.log('Соединение установлено.');
});

socket.on('message', function(data) {
    console.log(`Получено сообщение: ${data}`);
});

socket.on('close', function() {
    console.log('Соединение закрыто.');
});

socket.on('error', function(error) {
    console.error(`Ошибка: ${error.message}`);
});
