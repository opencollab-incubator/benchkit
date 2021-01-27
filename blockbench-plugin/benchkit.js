var socket;
var key;

var playerList = []

Plugin.register('benchkit', {
    title: 'Benchkit',
    author: 'lukeeey',
    description: 'Applies your skin in game with the click of a button',
    version: '1.0.0',
    variant: 'both',
    onload() {
        // Initialize defaults
        if (localStorage.getItem('fetch_player_list') == null) localStorage.setItem('fetch_player_list', false)
        if (localStorage.getItem('server_connection') == null) localStorage.setItem('server_connection', null)
        if (localStorage.getItem('last_player_uuid') == null) localStorage.setItem('last_player_uuid', null)

        if (shouldRequestPlayerList()) updatePlayerList()

        var menu = new BarMenu('benchkit_menu', [
            new Action({
                id: 'connect_to_server',
                name: 'Connect to Minecraft Server',
                condition: Format.id === 'skin' || Format.id === 'bedrock',
                click: function (ev) {
                    var conn;
                    if ((conn = getConnectionDetails()) !== null) {
                        return createWebSocket(conn.address, conn.port, conn.key)
                    }
                    showConnectDialog()
                }
            }),
            new Action({
                id: 'disconnect_from_server',
                name: 'Disconnect from Minecraft Server',
                condition: Format.id === 'skin' || Format.id === 'bedrock',
                keybind: new Keybind({ key: 68, ctrl: true, shift: true }),
                click: function (ev) {
                    closeWebSocket()
                }
            }),
            new Action({
                id: 'benchkit_configure',
                name: 'Configure',
                condition: Format.id === 'skin' || Format.id === 'bedrock',
                click: function (ev) {
                    showConfigureDialog()
                }
            }),
            '_',
            new Action({
                id: 'apply_skin_on_server',
                name: 'Apply Skin on Server',
                keybind: new Keybind({ key: 65, ctrl: true, shift: true }),
                condition: Format.id === 'skin',
                click: function (ev) {
                    showExportDialog()
                }
            }),
            new Action({
                id: 'apply_model_on_server',
                name: 'Apply Model on Server',
                keybind: new Keybind({ key: 65, ctrl: true, shift: true }),
                condition: Format.id === 'bedrock',
                click: function (ev) {
                    showExportModelDialog()
                }
            }),

        ], true)

        menu.label.innerText = 'Benchkit'

        MenuBar.update()

        new Action({
            id: 'apply_skin_last_player',
            name: 'Apply skin for last player',
            keybind: new Keybind({ key: 76, ctrl: true, shift: true }),
            click: function (ev) {
                var uuid;
                if ((uuid = getLastPlayerUuid()) !== null) {
                    sendToSocket('apply_skin', {
                        entityUuid: uuid,
                        texture: textures[0].img.src
                    })
                    Blockbench.showQuickMessage('Skin applied', 1.5 * 1000)
                } else {
                    Blockbench.showQuickMessage('You have not previously selected a player to apply a skin to!', 2 * 1000)
                }
            }
        })
    }
})

/** Socket related */
function createWebSocket(address, port, key) {
    socket = new WebSocket('ws://' + address + ':' + port)
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
                return Blockbench.showQuickMessage('Failed to authenticate: key did not match', 3 * 1000)
            }
        }

        if (data.type === 'fetch_player_list') {
            setPlayerList(data.data.players)

            Blockbench.showStatusMessage('Updated player list (' + data.data.players.length + ' players)', 1.5 * 1000)
        }
    }
    socket.onclose = function (event) {
        Blockbench.showStatusMessage('Disconnected from Minecraft Server' + (event.reason != null ? ' (' + event.reason + ')' : ''), 5 * 1000)

        if (event.wasClean) {
            Blockbench.showQuickMessage('Socket connection closed: ' + event.reason, 3 * 1000)
        } else {
            Blockbench.showQuickMessage('Socket connection died')
        }
    }
    socket.onerror = function (error) {
        alert('Socket error: ' + error.message)
    }
}

function closeWebSocket() {
    socket.close()
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


Blockbench.on('benchkit_prefs_updated', function (data) {
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
            'Enter the address of a Minecraft server with the Benchkit plugin installed and the key specified in the config.',
        ],
        form: {
            address: { label: 'Server address', type: 'input' },
            port: { label: 'Server port', type: 'input' },
            key: { label: 'Key', type: 'input' },
            remember: { label: 'Save details (You can reset them in the Configure dialog)', type: 'checkbox' }
        },
        onConfirm: function (formData) {
            createWebSocket(formData.address, formData.port, formData.key)
            this.hide()

            if (formData.remember) {
                setConnectionDetails(formData.address, formData.port, formData.key)
            }
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
            setLastPlayerUuid(formData.entityUuid)
            Blockbench.showQuickMessage('Skin applied!')
            this.hide()
        }
    })

    if (shouldRequestPlayerList()) {
        var options = {}

        for (var player of playerList) {
            options[player.uuid] = player.name + ' (' + player.uuid + ')'
        }

        dialog.form = {
            entityUuid: { label: 'Select player', type: 'select', options: options }
        }
    }

    dialog.show()
}

// TODO: Merge this with the above function
function showExportModelDialog() {
    var geometry = JSON.parse(Codecs.bedrock.compile())
    geometry['minecraft:geometry'][0]['description']['identifier'] = 'geometry.humanoid.customSlim'

    var dialog = new Dialog({
        id: 'export_model_to_server_dialog',
        title: 'Apply Model on Server',
        form: {
            entityUuid: { label: 'Player UUID', type: 'input' } 
        },
        onConfirm: function (formData) {
            sendToSocket('apply_model', {
                entityUuid: formData.entityUuid,
                model: JSON.stringify(geometry)
            })
            setLastPlayerUuid(formData.entityUuid)
            Blockbench.showQuickMessage('Model applied!')
            this.hide()
        }
    })

    if (shouldRequestPlayerList()) {
        var options = {}

        for (var player of playerList) {
            options[player.uuid] = player.name + ' (' + player.uuid + ')'
        }

        dialog.form = {
            entityUuid: { label: 'Select player', type: 'select', options: options }
        }
    }

    dialog.show()
}

function showConfigureDialog() {
    var dialog = new Dialog({
        id: 'benchkit_configure_dialog',
        title: 'Configure Benchkit',
        lines: [
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
            '<li style="padding: 5px 0;padding-top:15px;">',
            '<label for="reset_conn_details" atyle="display: inline-block;margin-left: 8px;width: calc(100% - 60px);">',
            '<div class="setting_name" style="color: var(--color-light);height: 24px;font-size: 1.1em;">Reset Connection Details</div>',
            '<div class="setting_description" style="font-size: 0.9em;color: var(--color-text);">Reset the saved Minecraft server connection details</div>',
            '</label>',
            '<button id="reset_conn_details">Reset</button>',
            '</li>',
            '</ul>'
        ],
        onConfirm: function () {
            setRequestPlayerList($('#setting_fetch_player_list').is(':checked'))
            this.hide()

            Blockbench.dispatchEvent('benchkit_prefs_updated', {
                fetch_player_list: shouldRequestPlayerList()
            })
        },
    }).show()

    $('#setting_fetch_player_list').prop('checked', shouldRequestPlayerList())
    $('#reset_conn_details').click(function (e) {
        clearConnectionDetails()
        dialog.hide()
        Blockbench.showQuickMessage('Minecraft Server connection details reset', 2 * 1000)
    })
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

function getConnectionDetails() {
    var connection = localStorage.getItem('server_connection')
    return connection !== null ? JSON.parse(connection) : null
}

function setConnectionDetails(address, port, key) {
    var data = {
        address: address,
        port: port,
        key: key
    }
    if (data !== getConnectionDetails()) {
        localStorage.setItem('server_connection', JSON.stringify(data))
    }
}

function clearConnectionDetails() {
    localStorage.setItem('server_connection', null)
}

function getLastPlayerUuid() {
    return localStorage.getItem('last_player_uuid')
}

function setLastPlayerUuid(uuid) {
    localStorage.setItem('last_player_uuid', uuid)
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