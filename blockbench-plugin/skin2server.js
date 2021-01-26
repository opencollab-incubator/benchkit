var socket;
var key;

var playerList = []

Plugin.register('skin2server', {
    title: 'Skin to Server!',
    author: 'lukeeey',
    description: 'Applies your skin in game with the click of a button',
    version: '1.0.0',
    variant: 'both',
    onload() {
        // Initialize defaults
        if (localStorage.getItem('fetch_player_list') == null) localStorage.setItem('fetch_player_list', false)

        if (shouldRequestPlayerList()) updatePlayerList()

        if (Format.id === 'skin') {
            var menu = new BarMenu('skin2server_menu', [
                new Action({
                    id: 'connect_to_server',
                    name: 'Connect to Minecraft Server',
                    click: function (ev) {
                        showConnectDialog()
                    }
                }),
                new Action({
                    id: 'skin2server_configure',
                    name: 'Configure',
                    click: function (ev) {
                        showConfigureDialog()
                    }
                }),
                '_',
                new Action({
                    id: 'apply_skin_on_server',
                    name: 'Apply Skin on Server',
                    keybind: new Keybind({key:65, ctrl: true, shift: true}),
                    click: function (ev) {
                        showExportDialog()
                    }
                }),

            ], true)

            menu.label.innerText = 'Skin2Server'

            MenuBar.update()
        }
    }
})

/** Socket related */
function createWebSocket(address, key) {
    socket = new WebSocket('ws://' + address + ':3000')
    socket.onopen = function (e) {
        this.key = key
        
        Blockbench.showStatusMessage('Connected to ' + address, 3 * 1000)

        socket.send(JSON.stringify({
            type: 'authenticate',
            key: this.key
        }))
    }
    
    socket.onmessage = function (event) {
        var data = JSON.parse(event.data)

        if (data.type === 'authenticate') {
            if (data.key !== key) {
                socket.close()
                return alert('Failed to authenticate: keys did not match')
            }
        }

        if (data.type === 'fetch_player_list') {
            setPlayerList(data.data.players)

            Blockbench.showStatusMessage('Updated player list (' + data.data.players.length + ' players)', 3 * 1000)
        }
    }
    socket.onclose = function (event) {
        if (event.wasClean) {
            alert(`[close] Connection closed cleanly, code=${event.code} reason=${event.reason}`);
        } else {
            // e.g. server process killed or network down
            // event.code is usually 1006 in this case
            alert('[close] Connection died');
        }
    };
    socket.onerror = function (error) {
        alert('Socket error: ' + error.message)
    }
}

function sendToSocket(type, data) {
    socket.send(JSON.stringify({
        type: type,
        key: key,
        data: data
    }))
}


/** Helpers */
var playerListInterval

function updatePlayerList() {
    playerListInterval = setInterval(function () {
        if (!shouldRequestPlayerList()) {
            this.playerList = []
            return clearInterval(interval)
        }
        sendToSocket('fetch_player_list', {})
    }, 10 * 1000)
}


Blockbench.on('skin2server_prefs_updated', function (data) {
    if (data.fetch_player_list && playerList.length === 0) {
        updatePlayerList()
    } else {
        this.playerList = []
        clearInterval(playerListInterval)
    }
})


/** Dialogs */
function showConnectDialog() {
    var dialog = new Dialog({
        id: 'connect_to_server_dialog',
        title: 'Connect to Minecraft Server',
        lines: [
            'Enter the address of a Minecraft server with the Skin2Server plugin installed and the key specified in the config.',
        ],
        form: {
            address: { label: 'Server address', type: 'input' },
            key: { label: 'Key', type: 'input' },
            remember: { label: 'Save details', type: 'checkbox' }
        },
        onConfirm: function (formData) {
            createWebSocket(formData.address, formData.key)
            this.hide()

            // if (formData.remember) {
            //     localStorage.setItem('server_address', formData.address)
            //     localStorage.setItem('server_key', formData.key)
            // }
        }
    }).show()
}

function showExportDialog() {
    var dialog = new Dialog({
        id: 'export_to_server_dialog',
        title: 'Apply Skin on Server',
        form: {
            entityUuid: { label: 'Player UUID', type: 'input' } // TODO: Store last used player id in local storage and auto fill,
        },
        onConfirm: function (formData) {
            sendToSocket('apply_skin', {
                entityUuid: formData.entityUuid,
                texture: textures[0].img.src
            })
            alert('skin applied')
        }
    })

    if (shouldRequestPlayerList()) {
        var options = {}

        for (var player of playerList) {
            options[player.uuid] = player.name + ' (' + player.uuid + ')'
        }

        dialog.form =  {
            entityUuid: { label: 'Select Player', type: 'select', options: options }
        }
    }

    dialog.show()
}

function showConfigureDialog() {
    var dialog = new Dialog({
        id: 'skin2server_configure_dialog',
        title: 'Configure Skin2Server',
        lines: [
            '<h2>Settings</h2>',
            '<p></p>',
            '<ul>',
                '<li style="padding: 5px 0;">',
                    '<div class="setting_element">',
                        '<input type="checkbox" id="setting_fetch_player_list">',
                    '</div>',
                    '<label for="setting_fetch_player_list" atyle="display: inline-block;margin-left: 8px;width: calc(100% - 60px);">',
                        '<div class="setting_name" style="color: var(--color-light);height: 24px;font-size: 1.1em;">Fetch Player List</div>',
                        '<div class="setting_description" style="font-size: 0.9em;color: var(--color-text);">Request the player list from the server every 10 seconds</div>',
                    '</label>',
                '</li>',
            '</ul>'
        ],
        onConfirm: function() {
            setRequestPlayerList($('#setting_fetch_player_list').is(':checked'))
            this.hide()

            Blockbench.dispatchEvent('skin2server_prefs_updated', {
                fetch_player_list: shouldRequestPlayerList()
            })
        },
    }).show()

    $('#setting_fetch_player_list').prop('checked', shouldRequestPlayerList())
}

/** Getters */
function shouldRequestPlayerList() {
    return localStorage.getItem('fetch_player_list') === 'true'
}

function setRequestPlayerList(value) {
    if (value !== null && value !== shouldRequestPlayerList()) {
        localStorage.setItem('fetch_player_list', value)
    }
}

function getPlayerList() {
    return playerList
}

function setPlayerList(players) {
    if (players === undefined) {
        console.log('undefined, skipping')
        return;
    }
    playerList = players
}