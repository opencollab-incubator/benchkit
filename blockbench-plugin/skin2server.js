var socket;
var key;

Plugin.register('skin2server', {
    title: 'Skin to Server!',
    author: 'lukeeey',
    description: 'Applies your skin in game with the click of a button',
    version: '1.0.0',
    variant: 'both',
    onload() {
        if (Format.id === 'skin') {
            MenuBar.addAction(new Action({
                id: 'connect_to_server',
                name: 'Connect to Minecraft Server',
                category: 'filter',
                click: function (ev) {
                   showConnectDialog()
                }
            }), 'filter.0')

            MenuBar.addAction(new Action({
                id: 'export_skin_to_server',
                name: 'Apply Skin on Server',
                category: 'filter',
                click: function (ev) {
                   showExportDialog()
                }
            }), 'filter.0')
        }
    }
})

function createWebSocket(address, key) {
    socket = new WebSocket('ws://localhost:3000')
    socket.onopen = function (e) {
        this.key = key
        alert('Connected to socket')
        socket.send(JSON.stringify({
            type: 'authenticate',
            key: this.key
        }))
    }
    socket.onmessage = function (event) {
        let data = event.data
        if (data.type === 'authenticate') {
            if (data.key !== key) {
                socket.close()
                return alert('Failed to authenticate: keys did not match')
            }
        }
    }
    socket.onclose = function(event) {
        if (event.wasClean) {
          alert(`[close] Connection closed cleanly, code=${event.code} reason=${event.reason}`);
        } else {
          // e.g. server process killed or network down
          // event.code is usually 1006 in this case
          alert('[close] Connection died');
        }
      };
    socket.onerror = function (error) {
        alert('Error: ' + error.message)
    }
}   

function sendToSocket(type, data) {
    socket.send(JSON.stringify({
        type: type,
        key: key,
        data: data
    }))
}

function showConnectDialog() {
    var dialog = new Dialog({
        id: 'connect_to_server_dialog',
        title: 'Connect to Minecraft Server',
        lines: [
            'Enter the address of a Minecraft server with the Skin2Server plugin installed and the key specified in the config.',
        ],
        form: {
            address: {label: 'Server address', type: 'input'},
            key: {label: 'Key', type: 'input'},
            remember: {label: 'Save details', type: 'checkbox'}
        },
        onConfirm: function (formData) {
            createWebSocket(formData.address, formData.key)
            this.hide()
        }
    }).show()
}

function showExportDialog() {
    var dialog = new Dialog({
        id: 'export_to_server_dialog',
        title: 'Apply Skin on Server',
        form: {
            entityUuid: {label: 'Entity UUID', type: 'input'} // TODO: Store last used entity id in local storage and auto fill,
        },
        onConfirm: function (formData) {
            sendToSocket('apply_skin', {
                entityUuid: formData.entityUuid,
                texture: textures[0].img.src
            })
            alert('skin applied')
        }
    }).show()
}